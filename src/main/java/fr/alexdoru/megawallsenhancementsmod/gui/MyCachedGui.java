package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.ICachedHUDText;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class MyCachedGui extends Gui implements IRenderer, ICachedHUDText {

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final FontRenderer frObj = mc.fontRendererObj;

    public String displayText = "";
    public boolean isRenderingDummy = false;
    public GuiPosition guiPosition;

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public void updateDisplayText() {}

    @Override
    public int getHeight() {
        return frObj.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
        return frObj.getStringWidth(displayText);
    }

    @Override
    public void render() {
        isRenderingDummy = false;
    }

    @Override
    public void renderDummy() {
        isRenderingDummy = true;
    }

    @Override
    public boolean isEnabled() {
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

    public void drawMultilineString(String msg, int x, int y, boolean shadow) {
        for (String line : msg.split("\n")) {
            frObj.drawString(line, x, y, 16777215, shadow);
            y += frObj.FONT_HEIGHT;
        }
    }

    public int getMultilineWidth(String string) {
        int maxwidth = 0;
        for (String line : string.split("\n")) {
            int width = frObj.getStringWidth(line);
            if (width > maxwidth) {
                maxwidth = width;
            }
        }
        return maxwidth;
    }

}
