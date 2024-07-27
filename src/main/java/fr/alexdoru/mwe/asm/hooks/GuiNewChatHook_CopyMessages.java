package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.ChatLineAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class GuiNewChatHook_CopyMessages {

    private static String text;

    public static ChatLine setText(ChatLine chatLine, IChatComponent chatComponent) {
        text = StringUtil.removeFormattingCodes(chatComponent.getUnformattedText());
        ((ChatLineAccessor) chatLine).setmwe$Text(text);
        return chatLine;
    }

    public static ChatLine setText1(ChatLine chatLine) {
        ((ChatLineAccessor) chatLine).setmwe$Text(text);
        return chatLine;
    }

    public static void copyChatLine(ChatLine chatLine) {
        if (GuiChatHook_CopyMessages.copyChatLine()) {
            if (MWEConfig.shiftClickChatLineCopy && GuiScreen.isShiftKeyDown()) {
                GuiScreen.setClipboardString(StringUtil.removeFormattingCodes(chatLine.getChatComponent().getUnformattedText()).trim());
            } else if (((ChatLineAccessor) chatLine).getmwe$Text() != null) {
                GuiScreen.setClipboardString(((ChatLineAccessor) chatLine).getmwe$Text().trim());
            }
        }
    }

}
