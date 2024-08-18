package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmorHUD extends AbstractRenderer {

    private final ItemStack[] dummyArmor;

    public ArmorHUD() {
        super(MWEConfig.armorHUDPositon);
        dummyArmor = new ItemStack[4];
        dummyArmor[0] = new ItemStack(Items.diamond_boots);
        dummyArmor[1] = new ItemStack(Items.iron_leggings);
        dummyArmor[2] = new ItemStack(Items.iron_chestplate);
        dummyArmor[3] = new ItemStack(Items.iron_helmet);
    }

    @Override
    public void render(ScaledResolution resolution) {
        if (MWEConfig.lowDuraArmorHUD) {
            boolean shouldRender = false;
            for (final ItemStack stack : mc.thePlayer.inventory.armorInventory) {
                if (stack != null) {
                    final int durability = stack.getMaxDamage() - stack.getItemDamage();
                    if (durability <= MWEConfig.lowDuraArmorHUDValue) {
                        shouldRender = true;
                        break;
                    }
                }
            }
            if (!shouldRender) return;
        }
        final int width;
        final int height;
        if (MWEConfig.horizontalArmorHUD) {
            width = 18 * 4;
            height = 18;
        } else {
            width = 18;
            height = 18 * 4;
        }
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, width, height, -width / 2, -height / 2);
        this.renderArmorBar(mc.thePlayer.inventory.armorInventory, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY());
    }

    @Override
    public void renderDummy() {
        final int width;
        final int height;
        if (MWEConfig.horizontalArmorHUD) {
            width = 18 * 4;
            height = 18;
        } else {
            width = 18;
            height = 18 * 4;
        }
        final int x = this.guiPosition.getAbsoluteRenderX() - width / 2;
        final int y = this.guiPosition.getAbsoluteRenderY() - height / 2;
        this.renderArmorBar(dummyArmor, x, y);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled();
    }

    private void renderArmorBar(ItemStack[] armorInventory, int x, int y) {
        for (int i = armorInventory.length - 1; i >= 0; i--) {
            final ItemStack stack = armorInventory[i];
            if (stack != null) {
                drawItemStack(stack, x, y);
            }
            if (MWEConfig.horizontalArmorHUD) {
                x += 18;
            } else {
                y += 18;
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        final RenderItem itemRender = mc.getRenderItem();
        FontRenderer fr = stack.getItem().getFontRenderer(stack);
        if (fr == null) {
            fr = mc.fontRendererObj;
        }
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        if (MWEConfig.showArmorDurability) {
            if (MWEConfig.showArmorDurabilityAsNumber) {
                final int duraI = stack.getMaxDamage() - stack.getItemDamage();
                final String s = ColorUtil.getHPColor(stack.getMaxDamage(), duraI).toString() + duraI;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fr.drawStringWithShadow(s, (float) (x + 19 - 2 - fr.getStringWidth(s)), (float) (y + 6 + 3), 0xFFFFFFFF);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            } else {
                itemRender.renderItemOverlayIntoGUI(fr, stack, x, y, null);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

}
