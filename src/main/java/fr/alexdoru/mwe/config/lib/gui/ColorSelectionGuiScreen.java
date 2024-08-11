package fr.alexdoru.mwe.config.lib.gui;

import fr.alexdoru.mwe.gui.GuiUtil;
import fr.alexdoru.mwe.utils.DelayedTask;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class ColorSelectionGuiScreen extends GuiScreen {

    private static final ResourceLocation BLUR = new ResourceLocation("mwe", "blur.json");
    private final GuiScreen parent;
    private final Field field;
    private final int initialColor;
    private final int defaultColor;
    private GuiSlider sliderRed;
    private GuiSlider sliderGreen;
    private GuiSlider sliderBlue;
    private final boolean hasAlpha;
    private GuiSlider sliderAlpha;
    private final Consumer<Integer> setter;

    public ColorSelectionGuiScreen(ConfigGuiScreen parentScreen, Field field, int defaultColor, Consumer<Integer> setter) throws IllegalAccessException {
        this.parent = parentScreen;
        this.field = field;
        this.initialColor = ((int) field.get(null));
        this.defaultColor = defaultColor;
        this.hasAlpha = ((defaultColor >> 24) & 0xff) != 0;
        this.setter = setter;
    }

    @Override
    public void initGui() {
        this.buttonList.add(this.sliderRed = new GuiSlider(1, getxCenter() - 150, getButtonYPos(2), 150, 20, "Red: ", "", 0.0D, 255.0D, (this.initialColor >> 16 & 0xFF), false, true));
        this.buttonList.add(this.sliderGreen = new GuiSlider(2, getxCenter() - 150, getButtonYPos(3), 150, 20, "Green: ", "", 0.0D, 255.0D, (this.initialColor >> 8 & 0xFF), false, true));
        this.buttonList.add(this.sliderBlue = new GuiSlider(3, getxCenter() - 150, getButtonYPos(4), 150, 20, "Blue: ", "", 0.0D, 255.0D, (this.initialColor & 0xFF), false, true));
        if (this.hasAlpha) {
            this.buttonList.add(this.sliderAlpha = new GuiSlider(4, getxCenter() - 150, getButtonYPos(5), 150, 20, "Alpha: ", "", 0.0D, 255.0D, (this.initialColor >> 24 & 0xFF), false, true));
        }
        this.buttonList.add(new GuiButton(5, getxCenter() - 150, getButtonYPos(this.hasAlpha ? 6 : 5), 150, 20, "Reset default color"));
        this.buttonList.add(new GuiButton(6, getxCenter() - 150 / 2, getButtonYPos(this.hasAlpha ? 8 : 7), 150, 20, "Done"));
        if (ForgeVersion.getVersion().contains("2318")) {
            mc.entityRenderer.loadShader(BLUR);
        }
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            this.field.set(null, getCurrentColor());
            this.setter.accept(getCurrentColor());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't set color!");
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        final int sideLength = 10 + (20 + 4) * (this.hasAlpha ? 4 : 3) + 20 + 10;
        final int leftX = getxCenter() + 10;
        final int topY = getButtonYPos(2) - 10;
        GuiUtil.drawBoxWithOutline(leftX, topY, leftX + sideLength, topY + sideLength, 255 << 24 | getCurrentColor(), Color.BLACK.getRGB());
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 5) {
            this.resetDefaultColor();
        } else if (button.id == 6) {
            new DelayedTask(() -> mc.displayGuiScreen(parent));
        }
    }

    @Override
    public void onGuiClosed() {
        try {
            this.field.set(null, getCurrentColor());
            this.setter.accept(getCurrentColor());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't set color!");
        }
        if (ForgeVersion.getVersion().contains("2318")) {
            mc.entityRenderer.stopUseShader();
        }
        super.onGuiClosed();
        new DelayedTask(() -> mc.displayGuiScreen(parent));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int getCurrentColor() {
        if (this.hasAlpha) {
            return this.sliderAlpha.getValueInt() << 24 | this.sliderRed.getValueInt() << 16 | this.sliderGreen.getValueInt() << 8 | this.sliderBlue.getValueInt();
        }
        return this.sliderRed.getValueInt() << 16 | this.sliderGreen.getValueInt() << 8 | this.sliderBlue.getValueInt();
    }

    private void resetDefaultColor() {
        this.sliderRed.setValue(this.defaultColor >> 16 & 0xFF);
        this.sliderRed.updateSlider();
        this.sliderGreen.setValue(this.defaultColor >> 8 & 0xFF);
        this.sliderGreen.updateSlider();
        this.sliderBlue.setValue(this.defaultColor & 0xFF);
        this.sliderBlue.updateSlider();
        if (this.hasAlpha) {
            this.sliderAlpha.setValue(this.defaultColor >> 24 & 0xFF);
            this.sliderAlpha.updateSlider();
        }
    }

    private int getButtonYPos(int i) {
        return this.height / 8 + 24 * (i + 1);
    }

    private int getxCenter() {
        return this.width / 2;
    }

}
