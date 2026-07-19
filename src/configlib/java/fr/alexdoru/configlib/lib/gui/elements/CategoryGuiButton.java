package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class CategoryGuiButton implements SizedElement {

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

    public void draw(ColorPalette colorPalette, int drawX, int drawY) {
        this.posX = drawX;
        this.posY = drawY;
        mc.fontRendererObj.drawStringWithShadow(displayname, drawX, drawY, colorPalette.CATEGORY_BUTTON_TEXT);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isMouseHovering(mouseX, mouseY)) {
            this.configGui.setFocusedCategory(this.categoryName);
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            return true;
        }
        return false;
    }

    private boolean isMouseHovering(int mouseX, int mouseY) {
        final int extraY = ConfigGuiScreen.ELEMENT_GAP / 2;
        return mouseX >= posX && mouseY >= posY - extraY && mouseX < posX + getWidth() && mouseY < posY + extraY + getHeight();
    }

    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(displayname);
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

}
