package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.AliasData;
import fr.alexdoru.mwe.features.NameFormatter;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.cache.CachedHypixelPlayerData;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.http.requests.MojangNameToUUID;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CommandAddAlias extends MyAbstractCommand {

    public CommandAddAlias() {
        AliasData.init();
    }

    @Override
    public String getCommandName() {
        return "addalias";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.listAliasInLobby();
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            this.listAlias(args);
            return;
        }
        if (args.length != 2) {
            this.printCommandHelp();
            return;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            this.removeAlias(args[1]);
            return;
        }
        this.addAlias(args);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return getListOfStringsMatchingLastWord(args, AliasData.getAllNames());
        }
        final List<String> onlinePlayersByName = TabCompletionUtil.getOnlinePlayersByName();
        onlinePlayersByName.addAll(Arrays.asList("list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ad");
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                EnumChatFormatting.GREEN + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GOLD + "AddAlias Help\n\n")
                        + EnumChatFormatting.YELLOW + "/addalias" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Lists the alias in the lobby\n"
                        + EnumChatFormatting.YELLOW + "/addalias <player> <alias>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Adds an alias for the player\n"
                        + EnumChatFormatting.YELLOW + "/addalias <remove> <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Removes the alias for the player\n"
                        + EnumChatFormatting.YELLOW + "/addalias list" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Prints list of alias\n"
                        + EnumChatFormatting.GREEN + ChatUtil.bar()
        );
    }

    private void listAlias(String[] args) {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }

        final int displaypage;
        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid page number");
                return;
            }
        } else {
            displaypage = 1;
        }

        final List<Map.Entry<String, String>> entryList = AliasData.getEntries();

        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbAlias = 1;
        int nbpage = 1;
        boolean warning = true;

        for (int i = entryList.size() - 1; i >= 0; i--) {
            final Map.Entry<String, String> entry = entryList.get(i);
            if (nbAlias == 11) {
                nbAlias = 1;
                nbpage++;
            }
            if (nbpage == displaypage) {
                warning = false;
                futureList.add(MultithreadingUtil.addTaskToQueue(new ListAliasLineTask(entry.getKey(), entry.getValue())));
            }
            nbAlias++;
        }

        if (warning) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No alias to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
            return;
        }

        final int finalNbpage = nbpage;
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent imsgbody = new ChatComponentText("");
            for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                imsgbody.appendSibling(iChatComponentFuture.get()).appendText("\n");
            }
            ChatUtil.printIChatList(
                    "Alias list",
                    imsgbody,
                    displaypage,
                    finalNbpage,
                    getCommandUsage(null) + " list",
                    EnumChatFormatting.GREEN,
                    null,
                    null
            );
            return null;
        });

    }

    private static class ListAliasLineTask implements Callable<IChatComponent> {

        private final String uuid;
        private final String alias;

        public ListAliasLineTask(String uuid, String alias) {
            this.uuid = uuid;
            this.alias = alias;
        }

        @Override
        public IChatComponent call() {
            if (uuid.length() <= 16) {
                return new ChatComponentText(EnumChatFormatting.GOLD + uuid + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
            }
            try {
                final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                final LoginData logindata = new LoginData(playerdata.getPlayerData());
                return new ChatComponentText(EnumChatFormatting.GOLD + logindata.getFormattedName() + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
            } catch (ApiException e) {
                return new ChatComponentText(EnumChatFormatting.GOLD + uuid + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
            }
        }

    }

    private void listAliasInLobby() {
        boolean found = false;
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (AliasData.getAlias(netInfo.getGameProfile().getId(), netInfo.getGameProfile().getName()) != null) {
                if (!found) {
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "In this lobby :\n");
                    found = true;
                }
                ChatUtil.addChatMessage(NameFormatter.getFormattedName(netInfo));
            }
        }
        if (found) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
        } else {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No one in this lobby has an alias");
        }
    }

    private void addAlias(String[] args) {
        final String playername = args[0];
        final String alias = args[1];
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (NameFormatter.isRealPlayer(netInfo.getGameProfile().getId())) {
                    this.addAlias(
                            netInfo.getGameProfile().getId(),
                            netInfo.getGameProfile().getName(),
                            alias,
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                } else if (NameFormatter.isNickedPlayer(netInfo.getGameProfile().getId())) {
                    this.addAlias(
                            null,
                            netInfo.getGameProfile().getName(),
                            alias,
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                }
                return;
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final IPlayerUUID playerID = MojangNameToUUID.getPlayerUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                        if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                            // real player
                            mc.addScheduledTask(() -> this.addAlias(playerID.getId(), playerID.getName(), alias, loginData.getFormattedName()));
                            return;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> this.addAlias(null, playername, alias, null));
        });
    }

    private void addAlias(UUID uuid, String playername, String alias, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (uuid == null) {
            AliasData.putAlias(null, playername, alias);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for the " + EnumChatFormatting.DARK_PURPLE + "nicked " + EnumChatFormatting.GREEN + "player "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        } else {
            AliasData.putAlias(uuid, null, alias);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        }
    }

    private void removeAlias(String playername) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (NameFormatter.isRealPlayer(netInfo.getGameProfile().getId())) {
                    this.removeAlias(
                            netInfo.getGameProfile().getId(),
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                } else if (NameFormatter.isNickedPlayer(netInfo.getGameProfile().getId())) {
                    this.removeAlias(
                            null,
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                }
                return;
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final IPlayerUUID playerID = MojangNameToUUID.getPlayerUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                        if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                            // real player
                            mc.addScheduledTask(() -> this.removeAlias(playerID.getId(), playerID.getName(), loginData.getFormattedName()));
                            return;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> this.removeAlias(null, playername, null));
        });
    }

    private void removeAlias(UUID uuid, String playername, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (!AliasData.removeAlias(uuid, playername)) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Player does not have an alias.");
            return;
        }
        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + formattedName);
    }

}
