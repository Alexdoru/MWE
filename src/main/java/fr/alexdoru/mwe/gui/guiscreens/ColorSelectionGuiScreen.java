package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.gui.elements.ColoredSquareElement;
import fr.alexdoru.mwe.gui.elements.SimpleGuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.util.function.Consumer;

public class ColorSelectionGuiScreen extends MyGuiScreen {

    private final GuiScreen parent;
    private final int initialColor;
    private final int defaultColor;
    private final Consumer<Integer> setter;
    private GuiSlider sliderRed;
    private GuiSlider sliderGreen;
    private GuiSlider sliderBlue;
    private final boolean hasAlpha;
    private GuiSlider sliderAlpha;

    public ColorSelectionGuiScreen(GuiScreen parent, int currentColor, int defaultColor, Consumer<Integer> setter) {
        this.parent = parent;
        this.initialColor = currentColor;
        this.defaultColor = defaultColor;
        this.setter = setter;
        this.hasAlpha = ((defaultColor >> 24) & 0xff) != 0;
    }

    @Override
    public void initGui() {
        this.maxWidth = 150 * 2;
        this.maxHeight = (buttonsHeight + 4) * (this.hasAlpha ? 9 : 8) + buttonsHeight;
        super.initGui();
        this.elementList.add(new ColoredSquareElement(getxCenter() + 10, getButtonYPos(2) - 10, 10 + (20 + 4) * (this.hasAlpha ? 4 : 3) + 20 + 10, this::getCurrentColor));
        this.buttonList.add(this.sliderRed = new GuiSlider(1, getxCenter() - 150, getButtonYPos(2), 150, buttonsHeight, "Red: ", "", 0.0D, 255.0D, (this.initialColor >> 16 & 0xFF), false, true));
        this.buttonList.add(this.sliderGreen = new GuiSlider(2, getxCenter() - 150, getButtonYPos(3), 150, buttonsHeight, "Green: ", "", 0.0D, 255.0D, (this.initialColor >> 8 & 0xFF), false, true));
        this.buttonList.add(this.sliderBlue = new GuiSlider(3, getxCenter() - 150, getButtonYPos(4), 150, buttonsHeight, "Blue: ", "", 0.0D, 255.0D, (this.initialColor & 0xFF), false, true));
        if (this.hasAlpha) {
            this.buttonList.add(this.sliderAlpha = new GuiSlider(4, getxCenter() - 150, getButtonYPos(5), 150, buttonsHeight, "Alpha: ", "", 0.0D, 255.0D, (this.initialColor >> 24 & 0xFF), false, true));
        }
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150, getButtonYPos(this.hasAlpha ? 6 : 5), 150, buttonsHeight, "Reset default color", this::resetDefaultColor));
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(this.hasAlpha ? 8 : 7), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.setter.accept(getCurrentColor());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        this.setter.accept(getCurrentColor());
        super.onGuiClosed();
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

}
