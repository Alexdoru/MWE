package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.lib.gui.elements.ColorGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorSelectionGuiScreen extends GuiScreen implements GuiSlider.ISlider {

    private static final ResourceLocation BLUR = new ResourceLocation("configlib", "blur.json");
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTONS_GAP = 4;
    private static final int TOP_BOTTOM_MARGIN = 16;

    private final GuiScreen parent;
    private final ColorGuiButton colorButton;
    private final int initialColor;
    private final boolean hasAlpha;
    private final List<GuiSlider> colorSliders = new ArrayList<>();

    private int allButtonsHeight;

    public ColorSelectionGuiScreen(GuiScreen parent, ColorGuiButton colorButton) {
        this.parent = parent;
        this.colorButton = colorButton;
        this.initialColor = colorButton.getColor();
        this.hasAlpha = ((colorButton.getDefaultColor() >> 24) & 0xff) != 0;
    }

    @Override
    public void initGui() {
        final int color = colorButton.getColor();
        final int centerX = this.width / 2;
        final int slidersX = centerX - BUTTON_WIDTH;
        final List<String> colorChannels = new ArrayList<>(Arrays.asList("Red", "Green", "Blue"));
        if (hasAlpha) colorChannels.add("Alpha");
        this.allButtonsHeight = ((colorChannels.size() + 2) * (BUTTON_HEIGHT + BUTTONS_GAP) - BUTTONS_GAP) + TOP_BOTTOM_MARGIN + BUTTON_HEIGHT;
        int drawY = (this.height - allButtonsHeight) / 2;
        buttonList.clear();
        colorSliders.clear();
        for (int i = 0; i < colorChannels.size(); ++i) {
            final int value = getColorChannelValue(color, i);
            final GuiSlider slider = new GuiSlider(i + 1, slidersX, drawY, BUTTON_WIDTH, BUTTON_HEIGHT, colorChannels.get(i) + ": ", "", 0, 255, value, false, true, this);
            this.colorSliders.add(slider);
            this.buttonList.add(slider);
            drawY += BUTTON_HEIGHT + BUTTONS_GAP;
        }
        this.buttonList.add(new GuiButton(5, slidersX, drawY, BUTTON_WIDTH, BUTTON_HEIGHT, "Undo Changes"));
        drawY += BUTTON_HEIGHT + BUTTONS_GAP;
        this.buttonList.add(new GuiButton(6, slidersX, drawY, BUTTON_WIDTH, BUTTON_HEIGHT, "Reset to Default"));
        drawY += BUTTON_HEIGHT + TOP_BOTTOM_MARGIN;
        this.buttonList.add(new GuiButton(7, centerX - BUTTON_WIDTH / 2, drawY, BUTTON_WIDTH, BUTTON_HEIGHT, "Done"));
        this.mc.entityRenderer.loadShader(BLUR);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final int firstSliderY = colorSliders.get(0).yPosition;
        final int lineLength = allButtonsHeight - BUTTON_HEIGHT - TOP_BOTTOM_MARGIN;
        final int colorBoxLeft = this.width / 2 + 10;
        final int colorBoxTop = firstSliderY;
        final int colorBoxRight = colorBoxLeft + lineLength;
        final int colorBoxBottom = colorBoxTop + lineLength;
        if (GuiUtil.beginClearRect(mc, colorBoxLeft, colorBoxTop, colorBoxRight, colorBoxBottom)) {
            drawDefaultBackground();
            GuiUtil.endClearRect();
        }
        drawCenteredString(fontRendererObj, colorButton.getCategory() + " - " + colorButton.getName(), this.width / 2, firstSliderY - TOP_BOTTOM_MARGIN - fontRendererObj.FONT_HEIGHT / 2, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtil.drawBoxWithOutline(colorBoxLeft, colorBoxTop, colorBoxRight, colorBoxBottom, hasAlpha ? colorButton.getColor() : (colorButton.getColor() | (0xFF << 24)), Color.BLACK.getRGB());
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 5: updateToColor(initialColor); break;
            case 6: updateToColor(colorButton.getDefaultColor()); break;
            case 7: mc.displayGuiScreen(parent); break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        try {
            colorButton.saveColorToField();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't set color!");
        }
        this.mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void updateToColor(int newColor) {
        for (final GuiSlider slider : colorSliders) {
            slider.setValue(getColorChannelValue(newColor, slider.id-1));
            slider.updateSlider();
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        colorButton.setColor(getColorWithUpdatedChannel(colorButton.getColor(), slider.id-1, slider.getValueInt()));
    }

    private static int getColorWithUpdatedChannel(int color, int channel, int channelValue) {
        final int shiftBy = getColorChannelBitShift(channel);
        final int mask = 0xFF << shiftBy;
        return (color & ~mask) | ((channelValue & 0xFF) << shiftBy);
    }

    private static int getColorChannelValue(int color, int channel) {
        return (color >> getColorChannelBitShift(channel)) & 0xFF;
    }

    private static int getColorChannelBitShift(int channel) {
        if (channel < 0 || channel > 3)
            throw new IllegalArgumentException("Argument must be one of: (0, 1, 2, 3)");
        return channel == 3 ? 24 : (16 - (channel << 3)); // 8 * (2-channel)
    }
}
