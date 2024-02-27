package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.ChatComponentTextAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.opengl.GL11;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GuiNewChatHook_ChatHeads {

    private static boolean isHeadLine = false;

    public static void preRenderStringCall(ChatLine chatLine, int alpha, int x, int y) {
        if (ConfigHandler.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead() != null) {
            isHeadLine = true;
            GlStateManager.color(1.0F, 1.0F, 1.0F, ((float) alpha / 255.0F));
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead().getSkin());
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 8F, 8F, 8, 8, 8, 8, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 40F, 8F, 8, 8, 8, 8, 64.0F, 64.0F);
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
            msg = EnumChatFormatting.getTextWithoutFormattingCodes(msg);
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
        if (ConfigHandler.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead() != null) {
            return -9;
        }
        return 0;
    }

}
