package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class ItemCountHUD extends AbstractRenderer {
    public ItemCountHUD() {
        super(MWEConfig.heldItemCountDisplay); // Use the GuiPosition from config
    }

    @Override
    public void render(ScaledResolution resolution) {
        // Ensure the HUD is enabled and the configuration is applied correctly
        if (!MWEConfig.heldItemCountDisplay.isEnabled()) {
            return; // Do not render if the main display option is disabled
        }

        // Update the position based on the resolution and configuration
        updatePosition(resolution);

        // Get the item the player is holding
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            return; // Do not render if nothing is held
        }

        int itemCount = 0;

        // Determine what to count based on the item and configuration
        if (heldItem.getItem() == net.minecraft.init.Items.bow) {
            if (MWEConfig.arrowCountDisplay) {
                // If arrow count display is enabled, only count arrows
                itemCount = getItemCount(new ItemStack(net.minecraft.init.Items.arrow));
            } else {
                // If arrow count display is not enabled, count arrows and the held item
                itemCount = getItemCount(new ItemStack(net.minecraft.init.Items.arrow)); // Arrow count
                itemCount += getItemCount(heldItem); // Add held item count if not just counting arrows
            }
        } else {
            if (MWEConfig.arrowCountDisplay) {
                // If arrow count display is enabled but not holding a bow, do not count anything
                itemCount = 0;
            } else {
                // If not holding a bow and arrow count display is not enabled, count the held item
                itemCount = getItemCount(heldItem);
            }
        }

        if (!MWEConfig.arrowCountDisplay && heldItem.getItem() == net.minecraft.init.Items.bow) {
            itemCount -= 1; // Subtract 1 from itemCount if arrowCountDisplay is not true
        }

        if (itemCount == 0) {
            return; // Do not render if the item count is zero
        }

        // Render the item count
        final FontRenderer fontRenderer = mc.fontRendererObj;
        final String countText = String.valueOf(itemCount);

        final int x = this.guiPosition.getAbsoluteRenderX();
        final int y = this.guiPosition.getAbsoluteRenderY();

        fontRenderer.drawStringWithShadow(countText, x, y, 0xFFFFFFFF); // White color
    }

    @Override
    public void renderDummy() {
        // Dummy number for testing, showing a fixed count (e.g., 64)
        final FontRenderer fontRenderer = mc.fontRendererObj;
        final String dummyCountText = "64"; // Fixed dummy count for testing

        final int x = this.guiPosition.getAbsoluteRenderX();
        final int y = this.guiPosition.getAbsoluteRenderY();

        fontRenderer.drawStringWithShadow(dummyCountText, x, y, 0xFFFFFFFF); // White color
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled() && MWEConfig.heldItemCountDisplay.isEnabled();
    }

    private int getItemCount(ItemStack item) {
        int count = 0;
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack != null && stack.isItemEqual(item)) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    private void updatePosition(ScaledResolution resolution) {
        // Assuming guiPosition has methods to set and get positions
        // This should be similar to how it's done in ArmorHUD
        int width = 20; // Approximate width for the text
        int height = 10; // Approximate height for the text
        
        // Adjust the offsets to align the HUD correctly
        int offsetX = 0; // Center horizontally
        int offsetY = 0; // Center vertically
        
        // Update the position based on the resolution and configuration
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, width, height, offsetX, offsetY);
    }
}