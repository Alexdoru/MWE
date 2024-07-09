package fr.alexdoru.mwe.gui.elements;

import fr.alexdoru.mwe.gui.guiapi.IRenderer;
import fr.alexdoru.mwe.gui.guiapi.PositionEditGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HUDSettingGuiButtons {

    private static final int SIDE_BUTTON_WIDTH = 78;
    private static final int CENTER_BUTTON_WIDTH = 200;

    private final SimpleGuiButton resetButton;
    private final FancyGuiButton actionButton;
    private final SimpleGuiButton moveButton;

    public HUDSettingGuiButtons(int xCenter, int y, Supplier<String> buttonTextSupplier, Runnable action, IRenderer renderer, GuiScreen parent, String... tooltipLines) {
        this.resetButton = new SimpleGuiButton(
                xCenter - CENTER_BUTTON_WIDTH / 2 - 4 - SIDE_BUTTON_WIDTH,
                y,
                SIDE_BUTTON_WIDTH,
                20,
                "Reset position",
                () -> renderer.getGuiPosition().resetToDefault());
        this.actionButton = new FancyGuiButton(
                xCenter - CENTER_BUTTON_WIDTH / 2,
                y,
                buttonTextSupplier,
                action,
                tooltipLines);
        this.moveButton = new SimpleGuiButton(
                xCenter + CENTER_BUTTON_WIDTH / 2 + 4,
                y,
                SIDE_BUTTON_WIDTH,
                20,
                "Move HUD",
                () -> Minecraft.getMinecraft().displayGuiScreen(new PositionEditGuiScreen(renderer, parent)));
    }

    public HUDSettingGuiButtons(int xCenter, int y, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, IRenderer renderer, GuiScreen parent, String... tooltipLines) {
        this.resetButton = new SimpleGuiButton(
                xCenter - CENTER_BUTTON_WIDTH / 2 - 4 - SIDE_BUTTON_WIDTH,
                y,
                SIDE_BUTTON_WIDTH,
                20,
                "Reset position",
                () -> renderer.getGuiPosition().resetToDefault());
        this.actionButton = new OptionGuiButton(
                xCenter - CENTER_BUTTON_WIDTH / 2,
                y,
                optionName,
                setter,
                getter,
                tooltipLines);
        this.moveButton = new SimpleGuiButton(
                xCenter + CENTER_BUTTON_WIDTH / 2 + 4,
                y,
                SIDE_BUTTON_WIDTH,
                20,
                "Move HUD",
                () -> Minecraft.getMinecraft().displayGuiScreen(new PositionEditGuiScreen(renderer, parent)));
    }

    public void accept(List<GuiButton> buttonList) {
        buttonList.add(resetButton);
        buttonList.add(actionButton);
        buttonList.add(moveButton);
    }

}
