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
    private static final IChatComponent stopMovingMsg = new ChatComponentText(ChatUtil.getTagHackerDetector() + EnumChatFormatting.RED + "Stop moving for a second to send a report!");

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

    private static void deleteFromDrawnChatLines(IChatComponent componentToDelete, int searchLength) {
        final List<ChatLine> drawnChatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getDrawnChatLines();
        final int width = MathHelper.floor_float((float) mc.ingameGUI.getChatGUI().getChatWidth() / mc.ingameGUI.getChatGUI().getChatScale());
        final List<IChatComponent> toDeleteList = GuiUtilRenderComponents.splitText(componentToDelete, width, Minecraft.getMinecraft().fontRendererObj, false, false);
        // This for loop finds the first sequence of the list of strings to delete
        // and then loops from there to delete the rest of the message if it fits on more than one line
        final String firstLineToMatch = toDeleteList.get(0).getUnformattedText(); // cache the first line for speed
        int deletionIndex = -1;
        loop:
        for (int i = toDeleteList.size() - 1; i < drawnChatLines.size() && i < searchLength; i++) {
            if (firstLineToMatch.equals(drawnChatLines.get(i).getChatComponent().getUnformattedText())) {
                for (int j = 1; j < toDeleteList.size(); j++) {
                    if (!toDeleteList.get(j).getUnformattedText().equals(drawnChatLines.get(i - j).getChatComponent().getUnformattedText())) {
                        continue loop;
                    }
                }
                deletionIndex = i;
                break;
            }
        }
        if (deletionIndex != -1) {
            for (int j = 0; j < toDeleteList.size(); j++) {
                drawnChatLines.remove(deletionIndex);
                deletionIndex--;
            }
        }
    }

}
