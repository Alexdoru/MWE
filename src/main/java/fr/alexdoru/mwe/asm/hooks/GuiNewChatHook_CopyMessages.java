package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.ChatLineAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class GuiNewChatHook_CopyMessages {

    private static String text;

    public static ChatLine setText(ChatLine chatLine, IChatComponent chatComponent) {
        text = EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText());
        ((ChatLineAccessor) chatLine).setmwe$Text(text);
        return chatLine;
    }

    public static ChatLine setText1(ChatLine chatLine) {
        ((ChatLineAccessor) chatLine).setmwe$Text(text);
        return chatLine;
    }

    public static void copyChatLine(ChatLine chatLine) {
        if (GuiChatHook_CopyMessages.copyChatLine()) {
            if (ConfigHandler.shiftClickChatLineCopy && GuiScreen.isShiftKeyDown()) {
                GuiScreen.setClipboardString(EnumChatFormatting.getTextWithoutFormattingCodes(chatLine.getChatComponent().getUnformattedText()).trim());
            } else if (((ChatLineAccessor) chatLine).getmwe$Text() != null) {
                GuiScreen.setClipboardString(((ChatLineAccessor) chatLine).getmwe$Text().trim());
            }
        }
    }

}
