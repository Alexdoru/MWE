package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.client.Minecraft;

public class CategoryGuiButton {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConfigGuiScreen configGui;
    private final String categoryName;
    private final String displayname;
    private int posX, posY;

    public CategoryGuiButton(ConfigGuiScreen configGuiScreen, String name) {
        this(configGuiScreen, name, name);
    }

    public CategoryGuiButton(ConfigGuiScreen configGuiScreen, String name, String displayname) {
        this.configGui = configGuiScreen;
        this.categoryName = name;
        this.displayname = displayname;
    }

    public void draw(int drawX, int drawY) {
        this.posX = drawX;
        this.posY = drawY;
        mc.fontRendererObj.drawStringWithShadow(displayname, drawX, drawY, 0xFFFFFFFF);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && mouseX >= posX && mouseX < posX + getWidth() && mouseY >= posY && mouseY < posY + getHeight()) {
            this.configGui.setFocusedCategory(this.categoryName);
            SoundUtil.playButtonPress();
            return true;
        }
        return false;
    }

    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(displayname);
    }

    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

}
