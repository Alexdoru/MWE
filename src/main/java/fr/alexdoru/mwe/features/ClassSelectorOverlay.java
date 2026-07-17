package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardParser;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.ItemStackUtil;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClassSelectorOverlay {

    private static final Pattern DISPLAY_NAME_PATTERN = Pattern.compile("(\\w+)\\s?(:|✫{0,5})");
    private static final Pattern CLASSPOINTS_PATTERN = Pattern.compile("Class Points:\\s([,\\d]+)");

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ScoreboardParser parser = ScoreboardTracker.getParser();
    private final Map<MWClass, MWSkin> selectedSkins = new EnumMap<>(MWClass.class);
    private boolean active;
    private boolean dirty;

    public ClassSelectorOverlay() {
        for (final MWClass value : MWClass.values()) {
            final MWSkin skin = MWSkin.fromName(value.className);
            if (skin != null) {
                this.selectedSkins.put(value, skin);
            }
        }
        // TODO load selected classes from file
    }

    // TODO add random kit

    @SubscribeEvent
    public void onRenderSlot(ContainerSlotRenderEvent event) {
        if (this.active && event.itemStack != null && event.guiContainer instanceof GuiChest && !(event.slot.inventory instanceof InventoryPlayer)) {
            final Item item = event.itemStack.getItem();
            if (item == null) return;
            final MWClass mwClass = MWClass.fromItem(item);
            if (mwClass == null || !event.itemStack.hasDisplayName()) return;
            final String displayName = StringUtil.removeFormattingCodes(event.itemStack.getDisplayName());
            final Matcher matcher = DISPLAY_NAME_PATTERN.matcher(displayName);
            if (matcher.find()) {
                final String classname = matcher.group(1);
                if (mwClass != MWClass.fromName(classname)) return;
                final int prestiges = matcher.groupCount() == 1 ? 0 : matcher.group(2).length();
                final int classpoints = this.getClasspoints(event.itemStack);
                final MWSkin skin = this.selectedSkins.get(mwClass);
                if (skin == null) return;
                this.renderIcon(event.slot.xDisplayPosition, event.slot.yDisplayPosition, skin, prestiges, classpoints);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.active = this.mc.thePlayer != null && MWEConfig.classSelectorOverlay && parser.isMWEnvironement() && (parser.isPreGameLobby() || !parser.isInMwGame());
            if (this.mc.thePlayer != null && this.active && parser.isPreGameLobby()) {
                final MWSkin skin = MWSkin.ofPlayer(this.mc.thePlayer);
                if (skin != null && skin.mwClass != null) {
                    final MWSkin oldValue = this.selectedSkins.put(skin.mwClass, skin);
                    if (oldValue != skin) {
                        this.dirty = true;
                    }
                }
            }
        }
    }

    private int getClasspoints(ItemStack itemStack) {
        if (ItemStackUtil.hasLore(itemStack)) {
            final NBTTagList lore = ItemStackUtil.getLore(itemStack);
            if (lore != null) {
                final int size = lore.tagCount();
                for (int i = size - 1; i >= 0; --i) { // loop in reverse to find faster
                    final String line = lore.getStringTagAt(i);
                    if (line.contains("Class Points")) {
                        final Matcher matcher = CLASSPOINTS_PATTERN.matcher(StringUtil.removeFormattingCodes(line));
                        if (matcher.find()) {
                            return Integer.parseInt(matcher.group(1).replace(",", ""));
                        }
                    }
                }
            }
        }
        return 0;
    }

    private void renderIcon(int x, int y, MWSkin skin, int prestiges, int classpoints) { // FIXME ca flicker
        final Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        mc.getTextureManager().bindTexture(skin.getSkin());
        final int headSize = 16;
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, headSize, headSize, 64.0F, 64.0F);
        Gui.drawScaledCustomSizeModalRect(x, y, 40F, 8F, 8, 8, headSize, headSize, 64.0F, 64.0F);
        final int classpointColor = ColorUtil.getColorInt(ColorUtil.getPrestige4Color(prestiges >= 4 ? classpoints : 0)) | 0xFF000000;
        if (MWEConfig.classSelectorColoredBorder && classpoints >= 2000 && prestiges >= 4) {
            RenderHelper.drawOutline(x - 1, y - 1, x + 16 + 1, y + 16 + 1, classpointColor);
        }
        final float TEXT_SCALE = 0.6F;
        if (MWEConfig.classSelectorPrestigeLevel && prestiges != 0) {
            final String prestigeText = this.formatPrestiges(prestiges);
            this.drawTextAt(x + 1, y + 1, mc, prestigeText, TEXT_SCALE, 0xFFFFFFFF);
        }
        if (MWEConfig.classSelectorClasspoints && classpoints != 0) {
            final String pointsText = this.formatClasspoints(classpoints);
            this.drawTextAt(
                    x + 16 - mc.fontRendererObj.getStringWidth(pointsText) * TEXT_SCALE,
                    y + 16 - mc.fontRendererObj.FONT_HEIGHT * TEXT_SCALE,
                    mc,
                    pointsText,
                    TEXT_SCALE,
                    classpointColor
            );
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @SuppressWarnings("SameParameterValue")
    private void drawTextAt(float x, float y, Minecraft mc, String prestigeText, float scale, int color) {
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawStringWithShadow(prestigeText, 0, 0, color);
        GlStateManager.scale(1F / scale, 1F / scale, 1F / scale);
        GlStateManager.translate(-x, -y, 0);
    }

    private String formatPrestiges(int prestiges) {
        if (prestiges == 1) return EnumChatFormatting.GOLD.toString() + '✫';
        return EnumChatFormatting.GOLD.toString() + prestiges + '✫';
    }

    private String formatClasspoints(int classpoints) {
        if (classpoints < 1000) return String.valueOf(classpoints);
        if (classpoints < 10_000) return String.format("%.1f", classpoints / 1000F) + 'k';
        return String.valueOf(classpoints / 1000) + 'k';
    }

}
