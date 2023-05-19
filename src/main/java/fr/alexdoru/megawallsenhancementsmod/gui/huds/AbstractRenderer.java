package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class AbstractRenderer extends Gui implements IRenderer {

    protected static final Minecraft mc = Minecraft.getMinecraft();
    public String displayText = "";
    protected final GuiPosition guiPosition;

    public AbstractRenderer(GuiPosition guiPosition) {
        this.guiPosition = guiPosition;
    }

    @Override
    public GuiPosition getGuiPosition() {
        return this.guiPosition;
    }

}
