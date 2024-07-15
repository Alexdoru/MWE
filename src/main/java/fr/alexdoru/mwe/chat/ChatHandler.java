package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.asm.interfaces.GuiNewChatAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.List;

public class ChatHandler {

    //Note :
    //chatLines : contains all the messages printed in the chat, each item in the list is one message
    //drawnChatLines : contains the messages printed in the chat according to the width off the chat, each item in the list is one line of chat and not necessarily one message
    //as a result drawnChatLines.size() >= chatLines.size()

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final IChatComponent stopMovingMsg = new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Stop moving for a second to send a report!");

    public static void deleteMessageFromChat(IChatComponent messageToDelete) {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final String textToMatch = messageToDelete.getUnformattedText();
        final int chatSearchLength = 100;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            final String text = chatComponent.getUnformattedText();
            if (textToMatch.equals(text)) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
                break;
            }
            i++;
        }
    }

    public static void printStopMovingInstruction() {
        deleteStopMovingInstruction();
        ChatUtil.addChatMessage(stopMovingMsg);
    }

    public static void deleteStopMovingInstruction() {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final int chatSearchLength = 40;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            if (stopMovingMsg == chatComponent) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
                break;
            }
            i++;
        }
    }

    public static void deleteFlagFromChat(String flagKey) {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final int chatSearchLength = 100;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            if (chatComponent instanceof FlagChatComponent && flagKey.equals(((FlagChatComponent) chatComponent).getFlagKey())) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
                break;
            }
            i++;
        }
    }

    public static void deleteScanFlagFromChat(String key) {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final int chatSearchLength = 250;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            if (chatComponent instanceof ScanFlagChatComponent && key.equals(((ScanFlagChatComponent) chatComponent).getKey())) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
                break;
            }
            i++;
        }
    }

    public static void deleteWarningFromChat(String playername) {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final int chatSearchLength = 100;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            if (chatComponent instanceof WarningChatComponent && playername.equals(((WarningChatComponent) chatComponent).getPlayername())) {
                iterator.remove();
                deleteFromDrawnChatLines(chatComponent, chatSearchLength * 3);
                break;
            }
            i++;
        }
    }

    public static void deleteAllWarningMessages() {
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        final int chatSearchLength = 300;
        final Iterator<ChatLine> iterator = chatLines.iterator();
        int i = 0;
        while (iterator.hasNext() && i < chatSearchLength) {
            final IChatComponent chatComponent = iterator.next().getChatComponent();
            if (chatComponent instanceof WarningChatComponent) {
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
        // This for loop finds the first line of the message to delete
        // and then backtracks to delete the rest of the message if it fits on more than one line
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
