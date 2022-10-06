package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonFancy extends GuiButton {

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("fkcounter", "button.png");

    private final double scale;

    public ButtonFancy(int id, int x, int y, int width, int height, String text) {
        this(id, x, y, width, height, text, 1);
    }

    public ButtonFancy(int id, int x, int y, int width, int height, String text, double textScale) {
        super(id, x, y, width, height, text);
        this.scale = textScale;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;


            if (hovered)
                GlStateManager.color(0.2F, 0.6F, 1.0F, 0.5F);
            else
                GlStateManager.color(0.6F, 0.8F, 1.0F, 0.5F);

            mc.getTextureManager().bindTexture(BUTTON_TEXTURE);
            drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);


            GlStateManager.scale(scale, scale, 1);
            final int x = (xPosition + (width / 2));
            final int y = (int) ((yPosition + (height / 2)) - mc.fontRendererObj.FONT_HEIGHT * scale / 2) + 1;

            this.drawCenteredString(mc.fontRendererObj, this.displayString, (int) (x / scale), (int) (y / scale), 0xFFFFFF);
            GlStateManager.scale(1 / scale, 1 / scale, 1);

        }
    }

}
