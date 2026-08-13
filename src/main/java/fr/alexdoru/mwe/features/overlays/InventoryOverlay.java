package fr.alexdoru.mwe.features.overlays;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.config.SkinStyle;
import fr.alexdoru.mwe.config.TeamIndicatorStyle;
import fr.alexdoru.mwe.scoreboard.ScoreboardParser;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SameParameterValue")
public abstract class InventoryOverlay {

    protected static final Pattern NAME_PATTERN = Pattern.compile("(\\w+)$");

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final ScoreboardParser parser = ScoreboardTracker.getParser();
    protected boolean active;

    protected boolean isPlayerSkull(ItemStack stack) {
        final Item item = stack.getItem();
        return item != null && item == Items.skull && stack.getMetadata() == 3;
    }

    protected boolean isChestWithTitleOpened(@NotNull Predicate<String> titleTest) {
        if (mc.currentScreen instanceof GuiChest) {
            final GuiChest guiChest = (GuiChest) mc.currentScreen;
            final ContainerChest container = (ContainerChest) guiChest.inventorySlots;
            final IInventory lowerChestInventory = container.getLowerChestInventory();
            final String displayName = StringUtil.removeFormattingCodes(lowerChestInventory.getDisplayName().getUnformattedText());
            return titleTest.test(displayName);
        }
        return false;
    }

    @Nullable
    protected NetworkPlayerInfo getPlayerInfoFromReplayLine(String formattedLine) {
        final Matcher matcher = NAME_PATTERN.matcher(StringUtil.removeFormattingCodes(formattedLine));
        if (matcher.find()) {
            // in replay the names from the tablist have an additional §r at the end, if the name
            // is 15 or 16 long the last letters are trimmed so that the name is 16 long with the §r included
            final String playername = matcher.group(1);
            final String toSearch;
            if (playername.length() >= 15) {
                toSearch = playername.substring(0, 14) + "§r";
            } else {
                toSearch = playername + "§r";
            }
            final NetworkPlayerInfo netInfo = this.mc.getNetHandler().getPlayerInfo(toSearch);
            if (netInfo != null) return netInfo;
            // fallback to the normal name in case they ever remove the §r from the names
            return this.mc.getNetHandler().getPlayerInfo(playername);
        }
        return null;
    }

    protected void renderNetInfo(int x, int y, NetworkPlayerInfo netInfo, SkinStyle style) {
        switch (style) {
            case FLAT_SKIN:
                this.renderFlatSkin(x, y, netInfo.getLocationSkin());
                break;
            case SKULL:
                this.renderItemStack(x, y, this.getPlayerSkull(netInfo));
                break;
            case FACING_SKULL:
                this.renderFacingSkull(x, y, netInfo.getGameProfile());
                break;
        }
    }

    protected void renderSkin(int x, int y, MWSkin skin, SkinStyle style) {
        switch (style) {
            case FLAT_SKIN:
                this.renderFlatSkin(x, y, skin.getSkin());
                break;
            case SKULL:
                this.renderItemStack(x, y, skin.getPlayerSkullItemStack());
                break;
            case FACING_SKULL:
                this.renderFacingSkull(x, y, skin.getPlayerSkullItemStack());
                break;
        }
    }

    protected void renderSkull(int x, int y, ItemStack skullStack, SkinStyle style) {
        switch (style) {
            case FLAT_SKIN:
                this.renderFlatSkin(x, y, this.getSkinResourceFromSkullStack(skullStack));
                break;
            case SKULL:
                this.renderItemStack(x, y, skullStack);
                break;
            case FACING_SKULL:
                this.renderFacingSkull(x, y, skullStack);
                break;
        }
    }

    protected void renderItemStack(int x, int y, @NotNull ItemStack stack) {
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
    }

    protected ResourceLocation getSkinResourceFromSkullStack(ItemStack stack) {
        GameProfile profile = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("SkullOwner", 10)) {
            profile = NBTUtil.readGameProfileFromNBT(stack.getTagCompound().getCompoundTag("SkullOwner"));
        }
        if (profile == null) {
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }
        final Minecraft minecraft = Minecraft.getMinecraft();
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        } else {
            return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
        }
    }

    protected void renderFacingSkull(int x, int y, @NotNull ItemStack stack) {
        GameProfile profile = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("SkullOwner", 10)) {
            profile = NBTUtil.readGameProfileFromNBT(stack.getTagCompound().getCompoundTag("SkullOwner"));
        }
        this.renderFacingSkull(x, y, profile);
    }

    protected void renderFacingSkull(int x, int y, GameProfile profile) {
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableCull();

        GlStateManager.translate(x - 4, y + 14, 100.0F);
        GlStateManager.scale(1.0F, 1.0F, -1.0F);
        final float size = 24F;
        GlStateManager.scale(size, size, size);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

        TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 180F, 3, profile, -1);

        GlStateManager.enableCull();

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    protected void renderTeamIndicator(int x, int y, NetworkPlayerInfo netInfo, TeamIndicatorStyle style) {
        if (style == TeamIndicatorStyle.NONE) return;
        final ScorePlayerTeam team = netInfo.getPlayerTeam();
        if (team != null) {
            final char c = StringUtil.getLastColorCharOf(team.getColorPrefix());
            final int color = ColorUtil.getColorInt(c);
            this.renderTeamIndicator(x, y, color, style);
        }
    }

    protected void renderTeamIndicator(int x, int y, int color, TeamIndicatorStyle style) {
        switch (style) {
            case SMALL_SQUARE:
                this.renderSmallSquare(x, y, color);
                break;
            case OUTLINE:
                this.renderOutline(x, y, 16, color);
                break;
        }
    }

    protected void renderSmallSquare(int x, int y, int color) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        final int RECT_SIZE = 3;
        GuiChest.drawRect(
                x + 16 - RECT_SIZE - 1,
                y + 1,
                x + 16 - 1,
                y + 1 + RECT_SIZE,
                color | 0xFF000000
        );
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderOutline(int x, int y, int size, int color) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        RenderHelper.drawOutline(x, y, x + size, y + size, color | 0xFF000000);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderFlatSkin(int x, int y, ResourceLocation skin) {
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.translate(0, 0, 250F);
        GlStateManager.disableLighting();
        RenderHelper.renderSkinHead(skin, x + 1, y + 1, true, 14);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawTextAt(float x, float y, String text, float scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        if (scale != 1F) {
            GlStateManager.scale(scale, scale, scale);
        }
        mc.fontRendererObj.drawStringWithShadow(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    protected ItemStack getPlayerSkull(NetworkPlayerInfo netInfo) {
        final ItemStack skull = new ItemStack(Items.skull, 1, 3);
        final GameProfile profile = netInfo.getGameProfile();
        final NBTTagCompound skullOwnerTag = new NBTTagCompound();
        NBTUtil.writeGameProfile(skullOwnerTag, profile);
        final NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setTag("SkullOwner", skullOwnerTag);
        skull.setTagCompound(itemTag);
        return skull;
    }

}
