package fr.alexdoru.mwe.api;

import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.SkinChatHead;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.HUDRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class MWEApi {

    private MWEApi() {}

    public static final class Chat {

        private Chat() {}

        /**
         * Adds a message to the chat
         */
        public static void addChatMessage(String msg) {
            ChatUtil.addChatMessage(new ChatComponentText(msg));
        }

        /**
         * Adds a message to the chat
         */
        public static void addChatMessage(IChatComponent msg) {
            ChatUtil.addChatMessage(msg);
        }

        /**
         * Returns true if the IChatComponent already has a skin attached to
         */
        public static boolean hasChatHead(IChatComponent msg) {
            return msg instanceof ChatComponentTextAccessor && ((ChatComponentTextAccessor) msg).getSkinChatHead() != null;
        }

        /**
         * Adds a chat head to the IChatComponent
         *
         * @return true if it succeeds
         */
        public static boolean addChatHead(IChatComponent msg, ResourceLocation locationSkin) {
            return ChatUtil.addSkinToComponent(msg, locationSkin);
        }

        /**
         * Adds a chat head to the IChatComponent
         *
         * @return true if it succeeds
         */
        public static boolean addChatHeadOfPlayer(IChatComponent msg, String playername) {
            return ChatUtil.addSkinToComponent(msg, playername);
        }

        /**
         * Gets the ResourceLocation of the skin attached to this IChatComponent
         */
        @Nullable
        public static ResourceLocation getSkin(IChatComponent msg) {
            if (msg instanceof ChatComponentTextAccessor) {
                final SkinChatHead skin = ((ChatComponentTextAccessor) msg).getSkinChatHead();
                if (skin != null) return skin.getSkin();
            }
            return null;
        }
    }

    public static final class Hud {

        private Hud() {}

        /**
         * Register a HUD to render
         */
        public static void registerHUD(@NotNull IRenderer renderer) {
            HUDRenderer.registerRenderer(renderer);
        }
    }

    public static final class Squad {

        private Squad() {}

        /**
         * Returns true if the player is in the squad
         */
        public static boolean isSquadmate(String playername) {
            return SquadHandler.isSquadmate(playername);
        }

        /**
         * Adds a player to the squad
         */
        public static void addSquadmate(String playername) {
            SquadHandler.addPlayer(playername);
        }

        /**
         * Adds a player to the squad with a replacement name
         */
        public static void addSquadmate(String playername, String friendlyName) {
            SquadHandler.addPlayer(playername, friendlyName);
        }

        /**
         * Removes a player from the squad
         */
        public static void removeSquadmate(String playername) {
            SquadHandler.removePlayer(playername);
        }

        /**
         * Returns an unmodifiable view of the squad map
         */
        public static Map<String, String> getSquadMap() {
            return SquadHandler.getSquad();
        }
    }
}
