package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class KeybindGuiButton extends ConfigGuiButton {

    private final ClickGuiButton button;
    private final int minButtonWidth;
    private final KeyBinding keyBinding;
    private boolean listening;

    public KeybindGuiButton(ConfigFieldContainer fieldContainer) throws IllegalAccessException {
        super(fieldContainer);
        this.keyBinding = (KeyBinding) this.field.get(null);
        this.minButtonWidth = mc.fontRendererObj.getStringWidth(" Disabled ");
        this.button = new ClickGuiButton(minButtonWidth, 20);
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.displayString = this.getDisplayString();
        button.width = Math.max(minButtonWidth, mc.fontRendererObj.getStringWidth("  " + button.displayString));
        final int centerX = drawX + boxWidth - minButtonWidth / 2 - 20;
        button.xPosition = centerX - button.width / 2;
        button.yPosition = drawY + this.getTopPaddingToCenter(button.height);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    private String getDisplayString() {
        final String keyName = GameSettings.getKeyDisplayString(keyBinding.getKeyCode());
        if (listening) {
            return EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + keyName + EnumChatFormatting.WHITE + " <";
        } else if (hasConflict()) {
            return EnumChatFormatting.RED + "[" + keyName + "]";
        } else {
            return (keyBinding.getKeyCode() == 0 ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.GOLD) + "[" + keyName + "]";
        }
    }

    private boolean hasConflict() {
        if (keyBinding.getKeyCode() == 0) return false;
        for (final KeyBinding other : mc.gameSettings.keyBindings) {
            if (other != keyBinding && other.getKeyCode() == keyBinding.getKeyCode()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft() && button.mousePressed(mouseX, mouseY)) {
            this.listening = true;
            return true;
        }
        if (mouseButton.isRight() && button.mousePressed(mouseX, mouseY)) {
            this.setKeyCode(keyBinding.getKeyCodeDefault());
            return true;
        }
        return false;
    }

    /** Same key code rules as {@link net.minecraft.client.gui.GuiControls#keyTyped} */
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.setKeyCode(Keyboard.KEY_NONE);
        } else if (keyCode != 0) {
            this.setKeyCode(keyCode);
        } else if (typedChar > 0) {
            this.setKeyCode(typedChar + 256);
        }
        this.listening = false;
    }

    public void setKeyCode(int keyCode) {
        mc.gameSettings.setOptionKeyBinding(keyBinding, keyCode);
        KeyBinding.resetKeyBindingArrayAndHash();
        this.listening = false;
        this.invokeConfigEvent();
    }

    public void stopListening() {
        this.listening = false;
    }

    public boolean isListening() {
        return listening;
    }

}
