package fr.alexdoru.mwe.api;

import fr.alexdoru.mwe.api.asm.IClassNodeTransformer;
import fr.alexdoru.mwe.asm.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.interfaces.ChatComponentTextAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.SkinChatHead;
import fr.alexdoru.mwe.config.lib.ConfigHandler;
import fr.alexdoru.mwe.data.AliasData;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.HUDRenderer;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.requests.MojangNameToUUID;
import fr.alexdoru.mwe.http.requests.MojangUUIDToName;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public final class MWEApi {

    private MWEApi() {}

    /**
     * Register the main class of your addon, this should be the name of the class,
     * must be called before mods are constructed e.g. from a coremod.
     * The addon must implement {@link fr.alexdoru.mwe.api.IMWEAddon}.
     * For example : "net.myname.myaddon.MWEAddon"
     */
    public static void registerAddon(String classname) {
        final Object o = Launch.blackboard.computeIfAbsent("mwe.addons", k -> new ArrayList<>());
        if (o instanceof ArrayList) {
            //noinspection unchecked
            ((ArrayList<String>) o).add(classname);
        }
    }

    public static final class Alias {

        private Alias() {}

        /**
         * Returns true if the player has an alias
         */
        public static boolean hasAlias(@Nullable UUID id, @Nullable String playername) {
            return AliasData.getAlias(id, playername) != null;
        }

        /**
         * Returns the alias for this player, if any
         */
        @Nullable
        public static String getAlias(@Nullable UUID id, @Nullable String playername) {
            return AliasData.getAlias(id, playername);
        }

        /**
         * Sets the alias for the specified player
         */
        public static void setAlias(@Nullable UUID id, @Nullable String playername, String alias) {
            AliasData.putAlias(id, playername, alias);
        }

        /**
         * Removes a player from the alias list, returns true if the player was succesfully removed
         */
        public static boolean removeAlias(@Nullable UUID id, @Nullable String playername) {
            return AliasData.removeAlias(id, playername);
        }

    }

    public static final class Asm {

        private Asm() {}

        /**
         * Register your ASM transformers using the name of their class,
         * this must be called from the constructor of your coremod.
         * The transformer must implement {@link IClassNodeTransformer}.
         * For example : "net.myname.myaddon.asm.MinecraftTransformer"
         */
        public static void registerTransformer(String classname) {
            final Object o = Launch.blackboard.computeIfAbsent("mwe.transformers", k -> new ArrayList<>());
            if (o instanceof ArrayList) {
                //noinspection unchecked
                ((ArrayList<String>) o).add(classname);
            }
        }

        /**
         * Returns true if we are in an obfuscated environment
         */
        public static boolean isObfuscatedEnvironment() {
            return MWELoadingPlugin.isObf();
        }

    }

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

    public static final class Config {

        private Config() {}

        /**
         * Register your configuration class, fields and methods of the class
         * should be static and annotated with annotations from the fr.alexdoru.mwe.api.config package
         */
        public static void registerConfig(Class<?> clazz) {
            Objects.requireNonNull(clazz);
            ConfigHandler.registerConfig(clazz);
        }

        /**
         * Saves config values to the config file
         */
        public static void saveConfig() {
            ConfigHandler.saveConfig();
        }

        /**
         * Gets the config gui screen
         */
        public static GuiScreen getConfigGuiScreen() {
            return ConfigHandler.getConfigGuiScreen();
        }

        /**
         * Displays the config gui screen
         */
        public static void displayConfigGuiScreen() {
            ConfigHandler.displayConfigGuiScreen();
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

    public static final class MojangApi {

        private MojangApi() {}

        /**
         * Queries the mojang api to get the name of a player from their UUID,
         * this should not be called from the main thread to avoid blocking
         */
        public static String getPlayerName(UUID id) throws ApiException {
            return MojangUUIDToName.getName(id);
        }

        /**
         * Queries the mojang api to get the UUID of a player from their name,
         * this should not be called from the main thread to avoid blocking
         */
        public static IPlayerUUID getPlayerUUID(String playername) throws ApiException {
            return MojangNameToUUID.getPlayerUUID(playername);
        }

    }

    public static final class Player {

        private Player() {}

        /**
         * Returns an accessor to get and set certain player specific data
         */
        public static IPlayerInfoAccessor getPlayerInfo(EntityPlayer player) {
            if (player instanceof IPlayerInfoAccessor) {
                return ((IPlayerInfoAccessor) player);
            }
            return null;
        }

    }

    public static final class ReportList {

        private ReportList() {}

        /**
         * Returns true if the player is in the report list
         */
        public static boolean isPlayerReported(@NotNull UUID uuid, @Nullable String playername) {
            return WdrData.getWdr(uuid, playername) != null;
        }

        /**
         * Returns information about the report for the player, might be null
         */
        @Nullable
        public static IReportInfo getReportInfoFor(@NotNull UUID uuid, @Nullable String playername) {
            return WdrData.getWdr(uuid, playername);
        }

        /**
         * Adds a player to the reportlist
         */
        public static void addToReportList(@NotNull UUID uuid, @Nullable String playername, List<String> cheats) {
            WdrData.addReport(uuid, playername, cheats);
        }

        /**
         * Removes a player from the reportlist, returns true if the player was succesfully removed
         */
        public static boolean removeFromReportList(@NotNull UUID uuid, @Nullable String playername) {
            return WdrData.remove(uuid, playername);
        }

    }

    public static final class Scoreboard {

        private Scoreboard() {}

        /**
         * Returns the reference to the scoreboard parser singleton, this can be saved in a field
         */
        public static IScoreboardParser getScoreboarParser() {
            return ScoreboardTracker.getParser();
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
         * Removes a player from the squad, returns true if the player was succesfully removed
         */
        public static boolean removeSquadmate(String playername) {
            return SquadHandler.removePlayer(playername);
        }

        /**
         * Returns an unmodifiable view of the squad map
         */
        public static Map<String, String> getSquadMap() {
            return SquadHandler.getSquad();
        }

    }

    public static final class Tasks {

        private Tasks() {}

        /**
         * Schedules a task to run asynchronously
         */
        public static Future<?> queueAsyncTask(Runnable c) {
            return MultithreadingUtil.addTaskToQueue(c);
        }

        /**
         * Schedules a task to run asynchronously
         */
        public static <V> Future<V> queueAsyncTask(Callable<V> c) {
            return MultithreadingUtil.addTaskToQueue(c);
        }

        /**
         * Schedules a task to run in X ticks on the main thread
         */
        public static void queueSyncDelayedTask(Runnable task, int ticks) {
            new DelayedTask(task, ticks);
        }

    }

}
