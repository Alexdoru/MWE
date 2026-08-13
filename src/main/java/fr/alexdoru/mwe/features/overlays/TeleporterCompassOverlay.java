package fr.alexdoru.mwe.features.overlays;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.config.SkinStyle;
import fr.alexdoru.mwe.config.TeamIndicatorStyle;
import fr.alexdoru.mwe.utils.StringUtil;
import fr.alexdoru.mwe.utils.UUIDUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;

public final class TeleporterCompassOverlay extends InventoryOverlay {

    @SubscribeEvent
    public void onRenderSlot(ContainerSlotRenderEvent event) {
        if (!this.active || event.itemStack == null || !(event.guiContainer instanceof GuiChest) || event.slot.inventory instanceof InventoryPlayer) {
            return;
        }
        if (!this.isPlayerSkull(event.itemStack)) return;
        final boolean showMWSkin = MWEConfig.teleporterCompassMegaWallsSkins && parser.isInMwGame();
        final boolean showTeamIndicator = MWEConfig.teleporterCompassTeamIndicator != TeamIndicatorStyle.NONE;
        if (!showMWSkin && MWEConfig.teleporterCompassSkinStyle != SkinStyle.SKULL) {
            this.renderSkull(event.slot.xDisplayPosition, event.slot.yDisplayPosition, event.itemStack, MWEConfig.teleporterCompassSkinStyle);
            event.setCanceled(true);
        }
        if (showMWSkin || showTeamIndicator) {
            final NetworkPlayerInfo netInfo = this.getPlayerInfo(event.itemStack);
            if (netInfo != null) {
                if (showMWSkin) {
                    final MWSkin skin = MWSkin.fromResourceLocation(netInfo.getLocationSkin());
                    if (skin != null) {
                        this.renderSkin(event.slot.xDisplayPosition, event.slot.yDisplayPosition, skin, MWEConfig.teleporterCompassSkinStyle);
                        event.setCanceled(true);
                    }
                }
                if (showTeamIndicator) {
                    this.renderTeamIndicator(event.slot.xDisplayPosition, event.slot.yDisplayPosition, netInfo, MWEConfig.teleporterCompassTeamIndicator);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.active = MWEConfig.teleporterCompassOverlay
                    && this.mc.thePlayer != null
                    && this.mc.thePlayer.capabilities.allowFlying
                    && (parser.isReplayMode() || this.mc.thePlayer.isPotionActive(Potion.invisibility))
                    && this.isHoldingCompass();
        }
    }

    private boolean isHoldingCompass() {
        final ItemStack stack = this.mc.thePlayer.getHeldItem();
        return stack != null
                && stack.getItem() == Items.compass
                && stack.hasDisplayName()
                && StringUtil.removeFormattingCodes(stack.getDisplayName()).startsWith("Teleport");
    }

    @Nullable
    private NetworkPlayerInfo getPlayerInfo(ItemStack stack) {
        if (parser.isReplayMode()) {
            return this.getPlayerInfoReplay(stack);
        } else {
            return this.getPlayerInfoSpec(stack);
        }
    }

    @Nullable
    private NetworkPlayerInfo getPlayerInfoReplay(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return this.getPlayerInfoFromReplayLine(stack.getDisplayName());
        }
        return null;
    }

    @Nullable
    private NetworkPlayerInfo getPlayerInfoSpec(ItemStack stack) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound stackNbt = stack.getTagCompound();
            if (stackNbt.hasKey("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
                final NBTTagCompound skullOwner = stackNbt.getCompoundTag("SkullOwner");
                if (skullOwner.hasKey("Id", Constants.NBT.TAG_STRING)) {
                    final String id = skullOwner.getString("Id");
                    final UUID uuid = UUIDUtil.fromString(id);
                    final NetworkPlayerInfo netInfo = this.mc.getNetHandler().getPlayerInfo(uuid);
                    if (netInfo != null) return netInfo;
                }
            }
            // for nicked players the uuid in the SkullOwner doesn't match the uuid in the tablist
            // and the Name in the SkullOwner doesn't match the name in the tablist either
            if (stack.hasDisplayName()) {
                final String clearStackName = StringUtil.removeFormattingCodes(stack.getDisplayName());
                final Matcher matcher = NAME_PATTERN.matcher(clearStackName);
                if (matcher.find()) {
                    final String playername = matcher.group(1);
                    return this.mc.getNetHandler().getPlayerInfo(playername);
                }
            }
        }
        return null;
    }

}
