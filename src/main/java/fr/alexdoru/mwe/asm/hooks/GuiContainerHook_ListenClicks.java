package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.nocheaters.ReportQueue;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiContainerHook_ListenClicks {

    private static final Item STAINED_HARDENED_CLAY = Item.getItemFromBlock(Blocks.stained_hardened_clay);

    public static void listenClick(GuiContainer guiContainer, Slot slot) {
        if (guiContainer instanceof GuiChest && guiContainer.inventorySlots instanceof ContainerChest && slot != null) {
            if (isSubmitReportButton(slot) && isReportGui(guiContainer)) {
                addReportedPlayer(guiContainer);
            }
        }
    }

    private static boolean isSubmitReportButton(Slot slot) {
        if (slot.slotNumber == 11) {
            final ItemStack stack = slot.getStack();
            if (stack != null && stack.getItemDamage() == 13 && stack.getItem() == STAINED_HARDENED_CLAY) {
                return stack.hasDisplayName() && stack.getDisplayName().contains("Submit Report");
            }
        }
        return false;
    }

    private static boolean isReportGui(GuiContainer guiContainer) {
        return ((ContainerChest) guiContainer.inventorySlots).getLowerChestInventory().getDisplayName().getFormattedText().contains("Report Cheating/Hacking");
    }

    private static void addReportedPlayer(GuiContainer guiContainer) {
        if (guiContainer.inventorySlots.inventorySlots.size() > 13) {
            final Slot slot = guiContainer.inventorySlots.inventorySlots.get(13);
            if (slot != null) {
                final ItemStack stack = slot.getStack();
                if (stack != null && stack.getItem() == Items.skull) {
                    if (stack.hasDisplayName()) {
                        final String text = StringUtil.removeFormattingCodes(stack.getDisplayName());
                        final Matcher matcher = Pattern.compile("/report (\\w+)").matcher(text);
                        if (matcher.find()) {
                            ReportQueue.INSTANCE.addPlayerReportedThisGame(matcher.group(1));
                        }
                    }
                }
            }
        }
    }

}
