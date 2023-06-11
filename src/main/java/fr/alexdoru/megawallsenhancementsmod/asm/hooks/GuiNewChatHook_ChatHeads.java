package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.ChatComponentTextAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.accessors.NetworkPlayerInfoAccessor_ChatHeads;
import fr.alexdoru.megawallsenhancementsmod.chat.SkinChatHead;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GuiNewChatHook_ChatHeads {

    private static boolean isHeadLine = false;

    public static void preBlendCall(ChatLine chatLine, int alpha) {
        if (ConfigHandler.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead() != null) {
            isHeadLine = true;
            GlStateManager.color(1.0F, 1.0F, 1.0F, ((float) alpha / 255.0F));
            GlStateManager.enableAlpha();
            return;
        }
        isHeadLine = false;
    }

    public static void postBlendCall(ChatLine chatLine, int x, int y) {
        if (isHeadLine) {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkinChatHead().getSkin());
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 8F, 8F, 8, 8, 8, 8, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 40F, 8F, 8, 8, 8, 8, 64.0F, 64.0F);
            GlStateManager.translate(9F, 0F, 0F);
        }
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
            int i = 0;
            while (matcher.find() && i < 3) {
                i++;
                final String potentialName = matcher.group();
                final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.getPlayerInfo(potentialName);
                if (networkPlayerInfo instanceof NetworkPlayerInfoAccessor_ChatHeads) {
                    final SkinChatHead skinChatHead = new SkinChatHead(networkPlayerInfo.getLocationSkin());
                    ((ChatComponentTextAccessor) message).setSkinChatHead(skinChatHead);
                    ((NetworkPlayerInfoAccessor_ChatHeads) networkPlayerInfo).setSkinChatHead(skinChatHead);
                    return;
                } else {
                    final ResourceLocation resourceLocation = NetHandlerPlayClientHook.getPlayerSkin(potentialName);
                    if (resourceLocation != null) {
                        ((ChatComponentTextAccessor) message).setSkinChatHead(new SkinChatHead(resourceLocation));
                    }
                }
            }
        }
    }

}
