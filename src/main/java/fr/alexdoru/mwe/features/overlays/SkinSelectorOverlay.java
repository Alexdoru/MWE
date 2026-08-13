package fr.alexdoru.mwe.features.overlays;

import fr.alexdoru.mwe.api.events.ContainerSlotRenderEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.config.SkinStyle;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SkinSelectorOverlay extends InventoryOverlay {

    @SubscribeEvent
    public void onRenderSlot(ContainerSlotRenderEvent event) {
        if (this.active && event.itemStack != null && event.guiContainer instanceof GuiChest && !(event.slot.inventory instanceof InventoryPlayer)) {
            if (this.isPlayerSkull(event.itemStack) && event.itemStack.hasDisplayName()) {
                final String clearStackName = StringUtil.removeFormattingCodes(event.itemStack.getDisplayName());
                if (clearStackName.startsWith("Skin")) {
                    this.renderSkull(event.slot.xDisplayPosition, event.slot.yDisplayPosition, event.itemStack, MWEConfig.skinSelectorStyle);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.active = MWEConfig.skinSelectorStyle != SkinStyle.SKULL
                    && this.mc.thePlayer != null
                    && parser.isMWEnvironement()
                    && (parser.isPreGameLobby() || !parser.isInMwGame());
        }
    }

}
