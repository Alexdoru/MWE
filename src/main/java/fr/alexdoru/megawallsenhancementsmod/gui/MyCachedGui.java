package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.gui.hudapi.HUDPosition;
import fr.alexdoru.fkcountermod.gui.hudapi.ICachedHUDText;
import fr.alexdoru.fkcountermod.gui.hudapi.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class MyCachedGui extends Gui implements IRenderer, ICachedHUDText {

    public final Minecraft mc = Minecraft.getMinecraft();
    public final FontRenderer frObj = mc.fontRendererObj;

    public String displayText = "";
    public boolean isRenderingDummy = false;
    public HUDPosition hudPosition;

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
        return false;
    }

    @Override
    public void save() {}

    @Override
    public HUDPosition getHUDPosition() {
        return this.hudPosition;
    }

    public void drawMultilineString(String msg, int x, int y, boolean shadow) {
        for (String line : msg.split("\n")) {
            if (shadow) {
                frObj.drawStringWithShadow(line, x, y, 0);
            } else {
                frObj.drawString(line, x, y, 0);
            }
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
