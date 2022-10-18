package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class SquadHealthHUD implements IRenderer {

    public GuiPosition guiPosition;

    public SquadHealthHUD() {
        this.guiPosition = ConfigHandler.squadHealthHUDPosition;
    }

    @Override
    public int getHeight() {
        // TODO : Implement auto-generated method
        return 0;
    }

    @Override
    public int getWidth() {
        // TODO : Implement auto-generated method
        return 0;
    }

    @Override
    public void render(ScaledResolution resolution) {
        // TODO : Implement auto-generated method

    }

    @Override
    public void renderDummy() {
        // TODO : Implement auto-generated method

    }

    @Override
    public boolean isEnabled() {
        // TODO : Implement auto-generated method
        return true;
    }

    @Override
    public void save() {
        ConfigHandler.saveConfig();
    }

    @Override
    public GuiPosition getHUDPosition() {
        return this.guiPosition;
    }

}
