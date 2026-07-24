package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiNewChatHook_ChatHeads {

    private static boolean isHeadLine = false;

    public static void preRenderStringCall(ChatLine chatLine, int alpha, int x, int y) {
        if (MWEConfig.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead() != null) {
            isHeadLine = true;
            RenderHelper.renderSkinHead(
                    ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead().getSkin(),
                    x,
                    y - 8,
                    true,
                    8,
                    ((float) alpha / 255.0F)
            );
            GlStateManager.translate(9F, 0F, 0F);
            return;
        }
        isHeadLine = false;
    }

    public static void postRenderStringCall() {
        if (isHeadLine) {
            GlStateManager.translate(-9F, 0F, 0F);
        }
    }

    private static final Pattern NAME_PATTERN = Pattern.compile("\\w{2,16}");
    private static final Pattern CHAT_TIMESTAMP_PATTERN = Pattern.compile("^(?:\\[\\d\\d:\\d\\d(:\\d\\d)?(?: AM| PM|)]|<\\d\\d:\\d\\d>) ");

    public static void addHeadToMessage(IChatComponent message) {
        if (message instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) message).getSkinChatHead() == null) {
            String msg = message.getFormattedText().split("\n")[0];
            msg = StringUtil.removeFormattingCodes(msg);
            if (msg.startsWith("   ")) {
                return;
            }
            msg = CHAT_TIMESTAMP_PATTERN.matcher(msg).replaceAll("").trim();
            final Matcher matcher = NAME_PATTERN.matcher(msg);
            while (matcher.find()) {
                if (ChatUtil.tryAddSkinToComponent(message, matcher.group())) {
                    return;
                }
            }
        }
    }

    public static int fixComponentHover(ChatLine chatLine) {
        if (MWEConfig.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead() != null) {
            return -9;
        }
        return 0;
    }

}
