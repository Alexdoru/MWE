package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.ChatLineAccessor;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GuiNewChatHook_SearchBox {

    private final GuiNewChat guiNewChat;
    private final List<ChatLine> matchingLines = new ArrayList<>(100);
    private String prevTextToSearch = "";
    private String textToSearch = "";
    private Pattern searchPattern = null;
    private boolean needsUpdate = false;
    private boolean isRegexSearch = false;

    public GuiNewChatHook_SearchBox(GuiNewChat guiNewChat) {
        this.guiNewChat = guiNewChat;
    }

    public List<ChatLine> updateLinesToRender(List<ChatLine> drawnChatLines) {
        if (isSearching()) {
            if (needsUpdate) updateMatchingLines(drawnChatLines);
            return matchingLines;
        }
        return drawnChatLines;
    }

    public List<ChatLine> getLinesToRender(List<ChatLine> drawnChatLines) {
        return isSearching() ? matchingLines : drawnChatLines;
    }

    public ChatLine addDrawnChatLine(ChatLine chatLine) {
        if (isSearching()) {
            final String lineText = ((ChatLineAccessor) chatLine).getmwe$Text();
            if (lineText != null) {
                if (isRegexSearch ? searchPattern.matcher(lineText).find() : lineText.toLowerCase().contains(textToSearch)) {
                    matchingLines.add(0, chatLine);
                }
            }
        }
        return chatLine;
    }

    public void deleteChatLine(int id) {
        matchingLines.removeIf(chatLine -> chatLine.getChatLineID() == id);
    }

    public void clearSearch() {
        matchingLines.clear();
        prevTextToSearch = "";
        textToSearch = "";
        searchPattern = null;
        isRegexSearch = false;
    }

    public void setSearchText(Object obj) {
        if (obj instanceof Pattern) {
            searchPattern = ((Pattern) obj);
            needsUpdate = true;
            isRegexSearch = true;
        } else if (obj instanceof String) {
            prevTextToSearch = textToSearch;
            textToSearch = ((String) obj).toLowerCase();
            needsUpdate = !prevTextToSearch.equals(textToSearch) || isRegexSearch;
            isRegexSearch = false;
        }
    }

    private void updateMatchingLines(List<ChatLine> drawnChatLines) {
        this.guiNewChat.resetScroll();
        if (!isRegexSearch && !prevTextToSearch.isEmpty() && !matchingLines.isEmpty() && textToSearch.contains(prevTextToSearch)) {
            final Iterator<ChatLine> it = matchingLines.iterator();
            while (it.hasNext()) {
                final String lineText = ((ChatLineAccessor) it.next()).getmwe$Text();
                if (lineText == null || !lineText.toLowerCase().contains(textToSearch)) {
                    it.remove();
                }
            }
        } else {
            matchingLines.clear();
            for (final ChatLine chatLine : drawnChatLines) {
                final String lineText = ((ChatLineAccessor) chatLine).getmwe$Text();
                if (lineText != null) {
                    if (isRegexSearch ? searchPattern.matcher(lineText).find() : lineText.toLowerCase().contains(textToSearch)) {
                        matchingLines.add(chatLine);
                    }
                }
            }
        }
        needsUpdate = false;
    }

    private boolean isSearching() {
        return this.guiNewChat.getChatOpen() && (!textToSearch.isEmpty() || isRegexSearch && !searchPattern.pattern().isEmpty());
    }

}
