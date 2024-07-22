package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.GuiChatAccessor;
import fr.alexdoru.mwe.asm.interfaces.GuiNewChatExt;
import fr.alexdoru.mwe.chat.GuiChatSearchBox;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@SuppressWarnings("unused")
public class GuiChatHook_SearchBox {

    private static Object lastSearch;
    private final GuiChat guiChat;
    private GuiChatSearchBox searchBox;
    private boolean isSearchBoxFocused;
    private boolean isRegexSearch;

    public GuiChatHook_SearchBox(GuiChat guiChat) {
        this.guiChat = guiChat;
    }

    public void onInitGui() {
        if (!ConfigHandler.searchBoxChat) return;
        final boolean isFirstOpening = searchBox == null;
        final String prevSearch = isFirstOpening ? null : searchBox.getText();
        final String prevErrorMessage = isFirstOpening ? null : searchBox.getErrorMessage();
        final int searchBoxWidth = 64;
        final int searchBoxHeight = this.guiChat.mc.fontRendererObj.FONT_HEIGHT + 1;
        final int x = MathHelper.clamp_int(
                4 + ConfigHandler.searchBoxXOffset,
                0,
                this.guiChat.width - searchBoxWidth);
        final int y = MathHelper.clamp_int(
                this.guiChat.height - 12 - 2 - (searchBoxHeight) - 1 - ConfigHandler.searchBoxYOffset + (Loader.isModLoaded("chatting") ? -12 : 0),
                0,
                this.guiChat.height);
        searchBox = new GuiChatSearchBox(
                -65181591,
                this.guiChat.mc.fontRendererObj,
                x,
                y,
                searchBoxWidth,
                searchBoxHeight
        );
        if (isFirstOpening) restoreLastSearch();
        if (prevSearch != null) searchBox.setText(prevSearch);
        if (isRegexSearch) searchBox.setRegexSearch(true);
        if (prevErrorMessage != null) searchBox.setErrorMessage(prevErrorMessage);
        if (isSearchBoxFocused) {
            setFocusOnSearchBox();
        }
    }

    private void restoreLastSearch() {
        if (ConfigHandler.keepPreviousChatSearch) {
            if (lastSearch instanceof Pattern) {
                isRegexSearch = true;
                searchBox.setRegexSearch(true);
                searchBox.setText(((Pattern) lastSearch).pattern());
                ((GuiNewChatExt) this.guiChat.mc.ingameGUI.getChatGUI()).mwe$setSearchText(lastSearch);
            } else if (lastSearch instanceof String) {
                searchBox.setText(((String) lastSearch));
                ((GuiNewChatExt) this.guiChat.mc.ingameGUI.getChatGUI()).mwe$setSearchText(lastSearch);
            }
        }
    }

    public void onGuiClosed() {
        ((GuiNewChatExt) this.guiChat.mc.ingameGUI.getChatGUI()).mwe$clearSearch();
    }

    public void onDrawScreen(int mouseX, int mouseY) {
        if (searchBox == null) return;
        if (!isSearchBoxFocused && !ConfigHandler.showSearchBoxUnfocused) return;
        searchBox.drawTextBox(this.guiChat, mouseX, mouseY);
    }

    public boolean onKeyTyped(char typedChar, int keyCode) {
        if (searchBox == null) return false;
        if (isSearchBoxFocused) {
            if (keyCode == 1) {
                unsetFocusFromSearchBox();
            } else if (isCtrlFShorcutPressed(keyCode)) {
                final boolean shiftKeyDown = GuiScreen.isShiftKeyDown();
                if (isRegexSearch && shiftKeyDown || !isRegexSearch && !shiftKeyDown) {
                    unsetFocusFromSearchBox();
                } else if (isRegexSearch) {
                    this.isRegexSearch = false;
                    this.updateSearch();
                } else {
                    this.isRegexSearch = true;
                    this.updateSearch();
                }
            } else {
                searchBox.textboxKeyTyped(typedChar, keyCode);
                this.updateSearch();
            }
            return true;
        } else {
            if (isCtrlFShorcutPressed(keyCode)) {
                setFocusOnSearchBox();
                this.isRegexSearch = GuiScreen.isShiftKeyDown();
                this.updateSearch();
                return true;
            }
        }
        return false;
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (searchBox == null) return false;
        if (!isSearchBoxFocused && !ConfigHandler.showSearchBoxUnfocused) return false;
        if (mouseButton == 0) {
            if (isSearchBoxFocused) {
                if (searchBox.isMouseOnSearchIcon(mouseX, mouseY)) {
                    this.isRegexSearch = !this.isRegexSearch;
                    this.updateSearch();
                    return true;
                } else if (searchBox.isMouseOnTextField(mouseX, mouseY)) {
                    searchBox.mouseClicked(mouseX, mouseY, mouseButton);
                    return true;
                } else {
                    unsetFocusFromSearchBox();
                }
            } else {
                if (searchBox.isMouseOnSearchIcon(mouseX, mouseY)) {
                    setFocusOnSearchBox();
                    if (GuiScreen.isShiftKeyDown()) {
                        this.isRegexSearch = true;
                        this.updateSearch();
                    }
                    return true;
                } else if (searchBox.isTextFieldShowing() && searchBox.isMouseOnTextField(mouseX, mouseY)) {
                    setFocusOnSearchBox();
                    searchBox.mouseClicked(mouseX, mouseY, mouseButton);
                    return true;
                }
            }
        } else {
            if (isSearchBoxFocused) {
                if (searchBox.isMouseOnSearchBar(mouseX, mouseY)) {
                    return true;
                } else {
                    unsetFocusFromSearchBox();
                }
            }
        }
        return false;
    }

    private void setFocusOnSearchBox() {
        isSearchBoxFocused = true;
        ((GuiChatAccessor) this.guiChat).getInputField().setFocused(false);
        searchBox.setFocused(true);
    }

    private void unsetFocusFromSearchBox() {
        isSearchBoxFocused = false;
        ((GuiChatAccessor) this.guiChat).getInputField().setFocused(true);
        searchBox.setFocused(false);
    }

    private void updateSearch() {
        if (this.isRegexSearch) {
            try {
                final Pattern pattern = Pattern.compile(searchBox.getText(), Pattern.CASE_INSENSITIVE);
                ((GuiNewChatExt) this.guiChat.mc.ingameGUI.getChatGUI()).mwe$setSearchText(pattern);
                lastSearch = pattern;
                this.searchBox.setErrorMessage(null);
            } catch (PatternSyntaxException e) {
                this.searchBox.setErrorMessage(e.getDescription());
            }
        } else {
            ((GuiNewChatExt) this.guiChat.mc.ingameGUI.getChatGUI()).mwe$setSearchText(searchBox.getText());
            lastSearch = searchBox.getText();
        }
        this.searchBox.setRegexSearch(this.isRegexSearch);
    }

    private static boolean isCtrlFShorcutPressed(int keyCode) {
        return ConfigHandler.searchBoxChatShortcuts && keyCode == Keyboard.KEY_F && GuiScreen.isCtrlKeyDown();
    }

}
