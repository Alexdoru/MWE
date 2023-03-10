package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.gui.guiscreens.MyGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.function.Consumer;

public final class ColorSelectionScreen extends MyGuiScreen {

    private final GuiScreen parent;
    private final int initialColor;
    private final Consumer<Integer> consumer;
    private GuiSlider sliderRed;
    private GuiSlider sliderGreen;
    private GuiSlider sliderBlue;

    public ColorSelectionScreen(GuiScreen parent, int currentColor, Consumer<Integer> consumer) {
        this.parent = parent;
        this.initialColor = currentColor;
        this.consumer = consumer;
    }

    @Override
    public void initGui() {
        this.maxWidth = 150 * 2;
        this.maxHeight = (buttonsHeight + 4) * 7 + buttonsHeight;
        super.initGui();
        this.buttonList.add(this.sliderRed = new GuiSlider(1, getxCenter() - 150, getButtonYPos(2), 150, buttonsHeight, "Red: ", "", 0.0D, 255.0D, (this.initialColor >> 16 & 0xFF), false, true));
        this.buttonList.add(this.sliderGreen = new GuiSlider(2, getxCenter() - 150, getButtonYPos(3), 150, buttonsHeight, "Green: ", "", 0.0D, 255.0D, (this.initialColor >> 8 & 0xFF), false, true));
        this.buttonList.add(this.sliderBlue = new GuiSlider(3, getxCenter() - 150, getButtonYPos(4), 150, buttonsHeight, "Blue: ", "", 0.0D, 255.0D, (this.initialColor & 0xFF), false, true));
        this.buttonList.add(new GuiButton(200, getxCenter() - 150 / 2, getButtonYPos(6), 150, buttonsHeight, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final int top = getButtonYPos(2) - 10;
        final int bottom = getButtonYPos(4) + buttonsHeight + 10;
        final int left = getxCenter() + 10;
        final int right = left + bottom - top;
        drawColoredRectWithOutline(top, bottom, left, right, getCurrentColor());
        this.consumer.accept(getCurrentColor());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            mc.displayGuiScreen(parent);
        }
        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        this.consumer.accept(getCurrentColor());
        super.onGuiClosed();
    }

    private int getCurrentColor() {
        return this.sliderRed.getValueInt() << 16 | this.sliderGreen.getValueInt() << 8 | this.sliderBlue.getValueInt();
    }

}
