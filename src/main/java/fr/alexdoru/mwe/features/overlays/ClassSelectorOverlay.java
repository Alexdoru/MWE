package fr.alexdoru.mwe.features.overlays;

import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.ItemStackUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClassSelectorOverlay extends InventoryOverlay {

    private static final Pattern DISPLAY_NAME_PATTERN = Pattern.compile("^(\\w+)\\s?(:|✫{0,5}|- RECOMMENDED)$");
    private static final Pattern CLASSPOINTS_PATTERN = Pattern.compile("Class Points:\\s([,\\d]+)");

    private final Map<MWClass, MWSkin> selectedSkins = new EnumMap<>(MWClass.class);
    private final File configFolder;
    private UUID loadedUUID;
    private boolean dirty;
    private int tickCount;
    private MWSkin randomSkin = MWSkin.COW$COW;

    public ClassSelectorOverlay(File configFolder) {
        this.configFolder = configFolder;
    }

    @SubscribeEvent
    public void onRenderSlot(ContainerSlotRenderEvent event) {
        if (!this.active || event.itemStack == null || !(event.guiContainer instanceof GuiChest) || event.slot.inventory instanceof InventoryPlayer) {
            return;
        }
        final Item item = event.itemStack.getItem();
        if (item == null) return;
        if (!event.itemStack.hasDisplayName()) return;
        if (this.randomSkin != null && this.isRandomItem(event.itemStack)) {
            this.renderIcon(event.slot.xDisplayPosition, event.slot.yDisplayPosition, this.randomSkin);
            event.setCanceled(true);
            return;
        }
        final MWClass mwClass = MWClass.fromItem(item);
        if (mwClass == null) return;
        final String displayName = StringUtil.removeFormattingCodes(event.itemStack.getDisplayName());
        final Matcher matcher = DISPLAY_NAME_PATTERN.matcher(displayName);
        if (matcher.matches()) {
            final String classname = matcher.group(1);
            if (mwClass != MWClass.fromName(classname)) return;
            final int prestiges = (matcher.groupCount() == 1 || matcher.group(2).contains("RECOMMENDED")) ? 0 : matcher.group(2).length();
            final int classpoints = this.getClasspoints(event.itemStack);
            final MWSkin skin = this.selectedSkins.get(mwClass);
            if (skin == null) return;
            this.renderIcon(event.slot.xDisplayPosition, event.slot.yDisplayPosition, skin);
            this.renderPrestigesAndClasspoints(event.slot.xDisplayPosition, event.slot.yDisplayPosition, prestiges, classpoints);
            event.setCanceled(true);
        }
    }

    private boolean isRandomItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.nether_star && (itemStack.getDisplayName()).contains("Random!");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            final boolean prevActive = this.active;
            this.active = MWEConfig.classSelectorOverlay && this.mc.thePlayer != null && parser.isMWEnvironement() && (parser.isPreGameLobby() || !parser.isInMwGame());
            if (this.active && !prevActive) {
                final UUID uuid = this.mc.thePlayer.getUniqueID();
                if (uuid != null && (this.loadedUUID == null || !this.loadedUUID.equals(uuid))) {
                    this.loadedUUID = uuid;
                    this.dirty = false;
                    this.loadDefaultSkins();
                    this.loadSkinsAsync(uuid);
                }
            }
            if (prevActive && !this.active) {
                if (this.dirty && this.loadedUUID != null) {
                    this.dirty = false;
                    this.saveSkinsAsync(this.loadedUUID);
                }
            }
            if (this.mc.thePlayer != null && this.active && parser.isPreGameLobby()) {
                final MWSkin skin = MWSkin.ofPlayer(this.mc.thePlayer);
                if (skin != null && skin.mwClass != null) {
                    final MWSkin oldValue = this.selectedSkins.put(skin.mwClass, skin);
                    if (oldValue != skin) {
                        this.dirty = true;
                    }
                }
            }
            if (this.active) {
                this.tickCount++;
                if (this.tickCount % 5 == 0) {
                    final MWSkin nextSkin = this.selectedSkins.get(this.randomSkin.mwClass.next());
                    if (nextSkin != null) {
                        this.randomSkin = nextSkin;
                    }
                }
            }
        }
    }

    private void loadDefaultSkins() {
        for (final MWClass value : MWClass.values()) {
            final MWSkin skin = MWSkin.fromName(value.className);
            if (skin != null) {
                this.selectedSkins.put(value, skin);
            }
        }
    }

    private File getSkinsFile(@NotNull UUID uuid) {
        return new File(this.configFolder, "class_selector_" + uuid + ".properties");
    }

    private void loadSkinsAsync(@NotNull UUID uuid) {
        MultithreadingUtil.queueIOTask(() -> {
            final File file = this.getSkinsFile(uuid);
            if (!file.exists()) {
                return;
            }
            final Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            final Map<MWClass, MWSkin> map = new EnumMap<>(MWClass.class);
            for (final MWClass mwClass : MWClass.values()) {
                final String skinName = props.getProperty(mwClass.name());
                if (skinName == null) continue;
                final MWSkin skin = MWSkin.fromName(skinName);
                if (skin != null) {
                    map.put(mwClass, skin);
                }
            }
            this.mc.addScheduledTask(() -> {
                if (uuid.equals(this.loadedUUID)) {
                    this.dirty = false;
                    this.selectedSkins.putAll(map);
                }
            });
        });
    }

    private void saveSkinsAsync(@NotNull UUID uuid) {
        final Map<MWClass, MWSkin> snapshot = new EnumMap<>(this.selectedSkins);
        MultithreadingUtil.queueIOTask(() -> {
            if (!this.configFolder.exists()) {
                //noinspection ResultOfMethodCallIgnored
                this.configFolder.mkdirs();
            }
            final File file = this.getSkinsFile(uuid);
            final Properties props = new Properties();
            for (final Map.Entry<MWClass, MWSkin> entry : snapshot.entrySet()) {
                props.setProperty(entry.getKey().name(), entry.getValue().skinName);
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                props.store(out, "Class selector skin choices for " + uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    private void renderIcon(int x, int y, MWSkin skin) {
        final boolean renderSkull = MWEConfig.classSelectorPlayerHeads;
        if (renderSkull) {
            this.renderItemStack(x, y, skin.getPlayerSkullItemStack());
        } else {
            this.renderFlatSkin(x, y, skin);
        }
    }

    private void renderPrestigesAndClasspoints(int x, int y, int prestiges, int classpoints) {
        final int classpointColor = ColorUtil.getColorInt(ColorUtil.getPrestige4Color(prestiges >= 4 ? classpoints : 0));
        if (MWEConfig.classSelectorColoredBorder && classpoints >= 2000 && prestiges >= 4) {
            if (MWEConfig.classSelectorPlayerHeads) {
                this.renderOutline(x, y, x + 16, y + 16, classpointColor);
            } else {
                this.renderOutline(x - 1, y - 1, x + 16 + 1, y + 16 + 1, classpointColor);
            }
        }
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        final float TEXT_SCALE = 0.6F;
        if (MWEConfig.classSelectorPrestigeLevel && prestiges != 0) {
            final String prestigeText = this.formatPrestiges(prestiges);
            this.drawTextAt(x + 1, y + 1, prestigeText, TEXT_SCALE, 0xFFFFFFFF);
        }
        if (MWEConfig.classSelectorClasspoints && classpoints != 0) {
            final String pointsText = this.formatClasspoints(classpoints);
            this.drawTextAt(
                    x + 16 - mc.fontRendererObj.getStringWidth(pointsText) * TEXT_SCALE,
                    y + 16 - mc.fontRendererObj.FONT_HEIGHT * TEXT_SCALE,
                    pointsText,
                    TEXT_SCALE,
                    classpointColor
            );
        }
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
