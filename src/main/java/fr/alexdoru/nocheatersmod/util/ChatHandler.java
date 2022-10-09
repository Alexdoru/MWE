package fr.alexdoru.nocheatersmod.util;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GuiNewChatAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ChatHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String ANY_FORMATTING_CODE = "\u00a7[0-9a-f]";

    /*
     * Note :
     * chatLines : contains all the messages printed in the chat, each item in the list is one message
     * drawnChatLines : contains the messages printed in the chat according to the width off the chat, each item in the list is one line of chat and not necessarily one message
     * as a result drawnChatLines.size() >= chatLines.size()
     */
    public static void deleteWarningMessagesFor(String playername) {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final Pattern pattern = Pattern.compile("^\u00a7cWarning : (?:|\u00a77\u2716 )" + ANY_FORMATTING_CODE + playername + "(?:|" + ANY_FORMATTING_CODE + " \\[[A-Z]{3}\\])\u00a77 joined,.*");
        IChatComponent targetedChatComponent = null;
        final int chatSearchLength = 100;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            final String text = chatComponent.getUnformattedText();
            if (pattern.matcher(text).matches()) {
                targetedChatComponent = chatComponent;
                iterator.remove();
                break;
            }
            i++;
        }
        if (targetedChatComponent == null) {
            return;
        }
        deleteFromDrawnChatLines(targetedChatComponent, chatSearchLength * 3);
    }

    public static void deleteAllWarningMessages() {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final Pattern pattern = Pattern.compile("^\u00a7cWarning : (?:|\u00a77\u2716 )" + ANY_FORMATTING_CODE + "\\w{2,16}" + "(?:|" + ANY_FORMATTING_CODE + " \\[[A-Z]{3}\\])\u00a77 joined,.*");
        final int chatSearchLength = 300;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            final String text = chatComponent.getUnformattedText();
            if (pattern.matcher(text).matches()) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
            }
            i++;
        }
    }

    private static void deleteFromDrawnChatLines(IChatComponent targetedChatComponent, int drawnChatSearchLength) {
        final List<ChatLine> drawnChatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getDrawnChatLines();
        final int j = MathHelper.floor_float((float) mc.ingameGUI.getChatGUI().getChatWidth() / mc.ingameGUI.getChatGUI().getChatScale());
        final List<IChatComponent> todeleteList = GuiUtilRenderComponents.splitText(targetedChatComponent, j, Minecraft.getMinecraft().fontRendererObj, false, false);
        String textToDelete = todeleteList.get(0).getUnformattedText();
        /*
         * This for loop finds the first line of the warning message : "Warning : playername joined, ..."
         * and then backtracks to delete the rest of the message if it fits on more than one line
         * It does that ensure that it deletes the messages for the right player
         */
        for (int index = 0; index < drawnChatLines.size() && index < drawnChatSearchLength; index++) {
            String text = drawnChatLines.get(index).getChatComponent().getUnformattedText();
            if (textToDelete.equals(text)) {
                drawnChatLines.remove(index);
                index--;
                for (int index2 = 1; index2 < todeleteList.size() && index >= 0; index2++) {
                    textToDelete = todeleteList.get(index2).getUnformattedText();
                    text = drawnChatLines.get(index).getChatComponent().getUnformattedText();
                    if (textToDelete.equals(text)) {
                        drawnChatLines.remove(index);
                    }
                    index--;
                }
                break;
            }
        }
    }

}
