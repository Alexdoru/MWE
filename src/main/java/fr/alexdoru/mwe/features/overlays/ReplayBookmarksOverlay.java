package fr.alexdoru.mwe.features.overlays;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.ItemStackUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ReplayBookmarksOverlay extends InventoryOverlay {

    private static final Pattern BED_PATTERN = Pattern.compile("^(\\w+)\\sBed\\sDestroyed$");
    private static final Pattern WITHER_PATTERN = Pattern.compile("^(\\w+)\\sWither\\sDied$");
    private static final ItemStack WITHER_SKULL = new ItemStack(Items.skull, 1, 1);
    private static final ItemStack BED = new ItemStack(Items.bed);

    @SubscribeEvent
    public void onRenderSlot(ContainerSlotRenderEvent event) {
        if (!this.active || event.itemStack == null || !(event.guiContainer instanceof GuiChest) || event.slot.inventory instanceof InventoryPlayer) {
            return;
        }
        if (!this.isPaper(event.itemStack)) return;
        final String clearStackName = StringUtil.removeFormattingCodes(event.itemStack.getDisplayName());
        final int x = event.slot.xDisplayPosition;
        final int y = event.slot.yDisplayPosition;
        if ("Final Kill".equals(clearStackName) || "Final Death".equals(clearStackName)) {
            final NetworkPlayerInfo netInfo = this.getPlayerInfo(event.itemStack);
            if (netInfo != null) {
                if (parser.isMWReplay()) {
                    final MWSkin skin = MWSkin.fromResourceLocation(netInfo.getLocationSkin());
                    if (skin != null) {
                        this.renderSkin(x, y, skin, MWEConfig.replayBookmarksSkinStyle);
                        event.setCanceled(true);
                    }
                } else {
                    this.renderNetInfo(x, y, netInfo, MWEConfig.replayBookmarksSkinStyle);
                    event.setCanceled(true);
                }
                this.renderTeamIndicator(x, y, netInfo, MWEConfig.replayBookmarksPlayerTeamStyle);
            }
            return;
        }
        if (parser.isMWReplay()) {
            final Matcher witherMatcher = WITHER_PATTERN.matcher(clearStackName);
            if (witherMatcher.matches()) {
                this.renderItemStack(x, y, WITHER_SKULL);
                final String witherTeam = witherMatcher.group(1);
                final int color = this.getTeamColor(witherTeam);
                if (color != 0) {
                    this.renderTeamIndicator(x, y, color, MWEConfig.replayBookmarksEventTeamStyle);
                }
                event.setCanceled(true);
            }
            return;
        }
        final Matcher bedMatcher = BED_PATTERN.matcher(clearStackName);
        if (bedMatcher.matches()) {
            this.renderItemStack(x, y, BED);
            final String bedTeam = bedMatcher.group(1);
            final int color = this.getTeamColor(bedTeam);
            if (color != 0) {
                this.renderTeamIndicator(x, y, color, MWEConfig.replayBookmarksEventTeamStyle);
            }
            event.setCanceled(true);
        }
    }

    private int getTeamColor(String team) {
        switch (team) {
            case "Aqua":
                return ColorUtil.getColorInt(EnumChatFormatting.AQUA);
            case "Blue":
                return ColorUtil.getColorInt(EnumChatFormatting.BLUE);
            case "Gray":
                return ColorUtil.getColorInt(EnumChatFormatting.GRAY);
            case "Green":
                return ColorUtil.getColorInt(EnumChatFormatting.GREEN);
            case "Pink":
                return ColorUtil.getColorInt(EnumChatFormatting.LIGHT_PURPLE);
            case "Red":
                return ColorUtil.getColorInt(EnumChatFormatting.RED);
            case "White":
                return ColorUtil.getColorInt(EnumChatFormatting.WHITE);
            case "Yellow":
                return ColorUtil.getColorInt(EnumChatFormatting.YELLOW);
        }
        return 0;
    }

    private boolean isPaper(ItemStack stack) {
        final Item item = stack.getItem();
        return item != null && item == Items.paper && stack.hasDisplayName();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.active = MWEConfig.replayBookmarksOverlay
                    && this.parser.isReplayMode()
                    && this.mc.thePlayer != null
                    && this.mc.thePlayer.capabilities.allowFlying
                    && this.isChestWithTitleOpened(s -> s.contains("Bookmarks"));
        }
    }

    @Nullable
    private NetworkPlayerInfo getPlayerInfo(ItemStack stack) {
        if (ItemStackUtil.hasLore(stack)) {
            final NBTTagList lore = ItemStackUtil.getLore(stack);
            if (lore != null) {
                final int size = lore.tagCount();
                for (int i = 0; i < size; ++i) {
                    final String line = lore.getStringTagAt(i);
                    if (line.contains("Player:")) {
                        return getPlayerInfoFromReplayLine(line);
                    }
                }
            }
        }
        return null;
    }

}
