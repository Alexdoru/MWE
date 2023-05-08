package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HUDSettingGuiButtons {

    private final SimpleGuiButton resetButton;
    private final FancyGuiButton actionButton;
    private final SimpleGuiButton moveButton;

    public HUDSettingGuiButtons(int xCenter, int y, Supplier<String> buttonTextSupplier, Runnable action, GuiPosition position, IRenderer renderer, GuiScreen parent, String... tooltipLines) {
        this.resetButton = new SimpleGuiButton(
                xCenter - 200 / 2 - 4 - 90,
                y,
                90,
                20,
                "Reset position",
                position::resetToDefault);
        this.actionButton = new FancyGuiButton(
                xCenter - 200 / 2,
                y,
                buttonTextSupplier,
                action,
                tooltipLines);
        this.moveButton = new SimpleGuiButton(
                xCenter + 200 / 2 + 4,
                y,
                90,
                20,
                "Move HUD",
                () -> Minecraft.getMinecraft().displayGuiScreen(new PositionEditGuiScreen(renderer, parent)));
    }

    public HUDSettingGuiButtons(int xCenter, int y, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, GuiPosition position, IRenderer renderer, GuiScreen parent, String... tooltipLines) {
        this.resetButton = new SimpleGuiButton(
                xCenter - 200 / 2 - 4 - 90,
                y,
                90,
                20,
                "Reset position",
                position::resetToDefault);
        this.actionButton = new OptionGuiButton(
                xCenter - 200 / 2,
                y,
                optionName,
                setter,
                getter,
                tooltipLines);
        this.moveButton = new SimpleGuiButton(
                xCenter + 200 / 2 + 4,
                y,
                90,
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
