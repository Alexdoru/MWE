package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.ChatComponentTextAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class GuiNewChatHook_ChatHeads {

    private static boolean isHeadLine = false;

    public static void preBlendCall(ChatLine chatLine) {
        if (ConfigHandler.chatHeads && chatLine.getChatComponent() instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkin() != null) {
            isHeadLine = true;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            return;
        }
        isHeadLine = false;
    }

    public static void postBlendCall(ChatLine chatLine, int x, int y) {
        if (isHeadLine) {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(((ChatComponentTextAccessor) chatLine.getChatComponent()).getSkin());
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(x, y - 8, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
            GlStateManager.translate(9, 0, 0);
        }
    }

    public static void postRenderStringCall() {
        if (isHeadLine) {
            GlStateManager.translate(-9, 0, 0);
        }
    }

}
