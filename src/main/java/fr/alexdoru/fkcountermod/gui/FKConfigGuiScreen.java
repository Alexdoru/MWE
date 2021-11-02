package fr.alexdoru.fkcountermod.gui;

import com.google.common.collect.Lists;
import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.config.EnumFKConfigSetting;
import fr.alexdoru.fkcountermod.gui.elements.ButtonFancy;
import fr.alexdoru.fkcountermod.gui.elements.ButtonToggle;
import fr.alexdoru.megawallsenhancementsmod.gui.MyGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class FKConfigGuiScreen extends MyGuiScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("fkcounter", "background.png");

    @SuppressWarnings("FieldMayBeFinal")
    private int columns = 3;
    private int rows;

    private final int buttonSize = 50;
    private final int widthBetweenButtons = 10;
    private final int heightBetweenButtons = 30;

    private final GuiScreen parent;

    public FKConfigGuiScreen() {
        this.parent = null;
    }

    public FKConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        List<EnumFKConfigSetting> settings = Lists.newArrayList(EnumFKConfigSetting.values());
        rows = (int) Math.ceil(settings.size() / ((float) columns));

        buttonList.add(new ButtonFancy(100, width / 2 + 45, height / 2 - findMenuHeight() / 2 + 8, 30, 14, "Move HUD", 0.5));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int index = i * columns + j;
                if (index < settings.size()) {
                    addSettingButton(settings.get(index), i, j);
                }
            }
        }

        this.buttonList.add(new GuiButton(200, getxCenter() - 150 / 2, getyCenter() + 86, 150, 20, parent == null ? "Close" : "Done"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int rectWidth = findMenuWidth();
        int rectHeight = findMenuHeight();

        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 0.7F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(width / 2 - rectWidth / 2, height / 2 - rectHeight / 2, 0, 0, rectWidth, rectHeight, rectWidth, rectHeight);
        drawCenteredString(fontRendererObj, "FKCounter v" + FKCounterMod.VERSION, width / 2, height / 2 - rectHeight / 2 + 10, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {

        if (button instanceof ButtonToggle) {

            ButtonToggle buttonToggle = (ButtonToggle) button;
            buttonToggle.getSetting().toggleValue();

            if (buttonToggle.getSetting() == EnumFKConfigSetting.SHOW_PLAYERS && buttonToggle.getSetting().getValue() && EnumFKConfigSetting.COMPACT_HUD.getValue()) {
                EnumFKConfigSetting.COMPACT_HUD.toggleValue();
            }

            if (buttonToggle.getSetting() == EnumFKConfigSetting.COMPACT_HUD && buttonToggle.getSetting().getValue() && EnumFKConfigSetting.SHOW_PLAYERS.getValue()) {
                EnumFKConfigSetting.SHOW_PLAYERS.toggleValue();
            }
            FKCounterGui.instance.updateDisplayText();
        }

        if (button instanceof ButtonFancy) {
            if (button.id == 100) {
                Minecraft.getMinecraft().displayGuiScreen(new PositionEditGuiScreen(FKCounterGui.instance, this));
            }
        }

        if (button.id == 200) {
            mc.displayGuiScreen(parent);
        }

    }

    @Override
    public void onGuiClosed() {
        FKCounterMod.getConfigHandler().saveConfig();
        super.onGuiClosed();
    }

    private void addSettingButton(EnumFKConfigSetting setting, int row, int column) {

        int x;
        final int i = (widthBetweenButtons + buttonSize) * (column - columns / 2);

        if (columns % 2 == 0) { // even
            x = getxCenter() + widthBetweenButtons / 2 + i;
        } else { // odd
            x = getxCenter() - buttonSize / 2 + i;
        }

        int y = getyCenter() - findMenuHeight() / 2 + heightBetweenButtons + row * buttonSize;
        buttonList.add(new ButtonToggle(x + 13, y + 20, setting));
    }

    private int findMenuWidth() {
        return buttonSize * columns + widthBetweenButtons * (columns - 1);
    }

    private int findMenuHeight() {
        return heightBetweenButtons + buttonSize * rows;
    }

}
