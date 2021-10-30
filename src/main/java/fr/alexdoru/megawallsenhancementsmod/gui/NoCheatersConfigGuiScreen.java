package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

public class NoCheatersConfigGuiScreen extends MyGuiScreen {

    private final int ButtonsHeight = 20;

    @Override
    public void initGui() { // TODO still missing time between report and autoreport
        /*
         * Defines the button list
         */
        int buttonsWidth = 200;
        this.buttonList.add(new GuiButton(0, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 3, buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return "Warning icons on names : " + getSuffix(MWEnConfigHandler.toggleicons);
            case 1:
                return "Warning messages in chat : " + getSuffix(MWEnConfigHandler.togglewarnings);
            case 2:
                return "Autoreport cheaters : " + getSuffix(MWEnConfigHandler.toggleautoreport);
            case 3:
                return "Done";
            default:
                return "invalid button id";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        switch (button.id) {
            case 0:
                MWEnConfigHandler.toggleicons = !MWEnConfigHandler.toggleicons;
                break;
            case 1:
                MWEnConfigHandler.togglewarnings = !MWEnConfigHandler.togglewarnings;
                break;
            case 2:
                MWEnConfigHandler.toggleautoreport = !MWEnConfigHandler.toggleautoreport;
                break;
            case 3:
                mc.displayGuiScreen(new GeneralConfigGuiScreen());
                break;
            default:
                break;
        }
        button.displayString = getButtonDisplayString(button.id);
        super.actionPerformed(button);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        {
            int dilatation = 2;
            String title = "NoCheaters v" + NoCheatersMod.version;
            GlStateManager.translate((width / 2.0f) - mc.fontRendererObj.getStringWidth(title), getyCenter() - (ButtonsHeight + 4) * 3, 0);
            GlStateManager.scale(dilatation, dilatation, dilatation);
            mc.fontRendererObj.drawString(title, 0, 0, Integer.parseInt("55FF55", 16));
        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        MWEnConfigHandler.saveConfig();
        super.onGuiClosed();
    }

}
