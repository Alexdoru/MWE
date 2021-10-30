package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

public class MWEnConfigGuiScreen extends MyGuiScreen {

    private final int ButtonsHeight = 20;

    @Override
    public void initGui() { // TODO still missing GUI positioning
        /*
         * Defines the button list
         */
        int buttonsWidth = 200;
        this.buttonList.add(new GuiButton(0, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 - (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(0)));
        this.buttonList.add(new GuiButton(1, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(1)));
        this.buttonList.add(new GuiButton(2, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4), buttonsWidth, ButtonsHeight, getButtonDisplayString(2)));
        this.buttonList.add(new GuiButton(3, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 2, buttonsWidth, ButtonsHeight, getButtonDisplayString(3)));
        this.buttonList.add(new GuiButton(4, getxCenter() - buttonsWidth / 2, getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * 3, buttonsWidth, ButtonsHeight, getButtonDisplayString(4)));
        super.initGui();
    }

    private String getButtonDisplayString(int id) {
        switch (id) {
            case 0:
                return "Shorten coin messages : " + getSuffix(MWEnConfigHandler.shortencoinmessage);
            case 1:
                return "Report suggestions in chat : " + getSuffix(MWEnConfigHandler.reportsuggestions);
            case 2:
                return "Show /kill cooldown GUI : " + getSuffix(MWEnConfigHandler.show_killcooldownGUI);
            case 3:
                return "Show Arrow Hit GUI : " + getSuffix(MWEnConfigHandler.show_ArrowHitGui);
            case 4:
                return "Done";
            default:
                return "invalid button id";
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                MWEnConfigHandler.shortencoinmessage = !MWEnConfigHandler.shortencoinmessage;
                break;
            case 1:
                MWEnConfigHandler.reportsuggestions = !MWEnConfigHandler.reportsuggestions;
                break;
            case 2:
                MWEnConfigHandler.show_killcooldownGUI = !MWEnConfigHandler.show_killcooldownGUI;
                break;
            case 3:
                MWEnConfigHandler.show_ArrowHitGui = !MWEnConfigHandler.show_ArrowHitGui;
                break;
            case 4:
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
            String title = "Mega Walls Enhancements v" + MegaWallsEnhancementsMod.version;
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
