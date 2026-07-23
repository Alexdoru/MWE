package fr.alexdoru.configlib.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ColorEnumPickerScreen extends GuiScreen {

    private static final ResourceLocation BLUR = new ResourceLocation("configlib", "blur.json");

    private final GuiScreen parent;
    private final Field field;
    private final EnumChatFormatting[] colors;
    private EnumChatFormatting current;
    private final Runnable onPick;

    private int gridStartY;

    public ColorEnumPickerScreen(ConfigGuiScreen parent, Field field, EnumChatFormatting current, Runnable onPick) {
        this.parent = parent;
        this.field = field;
        this.current = current;
        this.onPick = onPick;

        final List<EnumChatFormatting> list = new ArrayList<>();
        for (final EnumChatFormatting c : EnumChatFormatting.values()) {
            if (c.isColor()) list.add(c);
        }
        this.colors = list.toArray(new EnumChatFormatting[0]);
    }

    @Override
    public void initGui() {
        final int square = 18;
        final int gap = 4;
        final int totalW = 8 * square + 7 * gap;
        final int startX = (this.width - totalW) / 2;
        this.gridStartY = this.height / 2 - (square + gap / 2);

        int id = 1000;
        for (int i = 0; i < colors.length; i++) {
            final int row = i / 8;
            final int col = i % 8;
            this.buttonList.add(new ColorSquareButton(id++,
                    startX + col * (square + gap), this.gridStartY + row * (square + gap),
                    square, square, colors[i]));
        }

        this.buttonList.add(new GuiButton(2000, this.width / 2 - 50,
                this.gridStartY + 2 * (square + gap) + 10, 100, 20, "Done"));

        this.mc.entityRenderer.loadShader(BLUR);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        EnumChatFormatting hovered = null;

        for (final GuiButton button : this.buttonList) {
            if (button instanceof ColorSquareButton) {
                final ColorSquareButton b = (ColorSquareButton) button;
                if (b.color == this.current) {
                    GuiUtil.drawBoxWithOutline(
                            b.xPosition - 2, b.yPosition - 2,
                            b.xPosition + b.width + 2, b.yPosition + b.height + 2,
                            0x00000000, Color.WHITE.getRGB());
                }
                if (mouseX >= b.xPosition && mouseY >= b.yPosition
                        && mouseX < b.xPosition + b.width && mouseY < b.yPosition + b.height) {
                    hovered = b.color;
                }
            }
        }

        if (hovered != null) {
            final String name = hovered + hovered.name();
            final int textX = (this.width - this.fontRendererObj.getStringWidth(name)) / 2;
            final int textY = this.gridStartY - 12 - this.fontRendererObj.FONT_HEIGHT;
            this.fontRendererObj.drawStringWithShadow(name, textX, textY, 0xFFFFFF);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2000) {
            this.mc.displayGuiScreen(this.parent);
            return;
        }
        if (button instanceof ColorSquareButton) {
            this.current = ((ColorSquareButton) button).color;
            try {
                this.field.set(null, this.current);
            } catch (IllegalAccessException ignored) {}
            if (this.onPick != null) this.onPick.run();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        this.mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class ColorSquareButton extends GuiButton {

        private final EnumChatFormatting color;

        ColorSquareButton(int buttonId, int x, int y, int w, int h, EnumChatFormatting color) {
            super(buttonId, x, y, w, h, "");
            this.color = color;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;

            final int rgb = 255 << 24 | mc.fontRendererObj.getColorCode(color.toString().charAt(1));
            final boolean hover = mouseX >= xPosition && mouseY >= yPosition
                    && mouseX < xPosition + width && mouseY < yPosition + height;

            GuiUtil.drawBoxWithOutline(
                    xPosition, yPosition,
                    xPosition + width, yPosition + height,
                    rgb,
                    hover ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
        }

    }

}
