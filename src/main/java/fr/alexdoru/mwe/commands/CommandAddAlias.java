package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.mwe.api.requests.HypixelPlayerData;
import fr.alexdoru.mwe.api.requests.MojangNameToUUID;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.AliasData;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
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
        if (args.length < 1) {
            this.printCommandHelp();
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
                        + EnumChatFormatting.YELLOW + "/addalias <player> <alias>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Adds an alias for the player\n"
                        + EnumChatFormatting.YELLOW + "/addalias <remove> <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Removes the alias for the player\n"
                        + EnumChatFormatting.YELLOW + "/addalias list" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Prints list of alias\n"
                        + EnumChatFormatting.GREEN + ChatUtil.bar()
        );
    }

    private void listAlias(String[] args) {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            this.listAliasInLobby();
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

        final ArrayList<Map.Entry<String, String>> entryList = new ArrayList<>(AliasData.getAliasMap().entrySet());
        Collections.reverse(entryList);

        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbAlias = 1;
        int nbpage = 1;
        boolean warning = true;

        for (final Map.Entry<String, String> entry : entryList) {
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
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "In this lobby :\n");
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            final String key;
            if (NameUtil.isRealPlayer(netInfo.getGameProfile().getId())) {
                key = netInfo.getGameProfile().getId().toString().replace("-", "");
            } else {
                key = netInfo.getGameProfile().getName();
            }
            if (AliasData.getAlias(key) != null) {
                ChatUtil.addChatMessage(NameUtil.getFormattedName(netInfo));
            }
        }
    }

    private void addAlias(String[] args) {
        final String playername = args[0];
        final String alias = args[1];
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (NameUtil.isRealPlayer(netInfo.getGameProfile().getId())) {
                    this.addAlias(
                            netInfo.getGameProfile().getId().toString(),
                            netInfo.getGameProfile().getName(),
                            alias,
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                } else if (NameUtil.isNickedPlayer(netInfo.getGameProfile().getId())) {
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
                final MojangNameToUUID mojangReq = new MojangNameToUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(mojangReq.getUuid()));
                        if (!loginData.hasNeverJoinedHypixel() && mojangReq.getName().equals(loginData.getdisplayname())) {
                            // real player
                            mc.addScheduledTask(() -> this.addAlias(mojangReq.getUuid(), mojangReq.getName(), alias, loginData.getFormattedName()));
                            return null;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> this.addAlias(null, playername, alias, null));
            return null;
        });
    }

    private void addAlias(String uuid, String playername, String alias, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (uuid == null) {
            AliasData.putAlias(playername, alias);
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Added alias for the " + EnumChatFormatting.DARK_PURPLE + "nicked " + EnumChatFormatting.GREEN + "player "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        } else {
            AliasData.putAlias(uuid.replace("-", ""), alias);
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Added alias for "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        }
        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
    }

    private void removeAlias(String playername) {
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (NameUtil.isRealPlayer(netInfo.getGameProfile().getId())) {
                    this.removeAlias(
                            netInfo.getGameProfile().getId().toString(),
                            netInfo.getGameProfile().getName(),
                            ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), netInfo.getGameProfile().getName()));
                } else if (NameUtil.isNickedPlayer(netInfo.getGameProfile().getId())) {
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
                final MojangNameToUUID mojangReq = new MojangNameToUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(mojangReq.getUuid()));
                        if (!loginData.hasNeverJoinedHypixel() && mojangReq.getName().equals(loginData.getdisplayname())) {
                            // real player
                            mc.addScheduledTask(() -> this.removeAlias(mojangReq.getUuid(), mojangReq.getName(), loginData.getFormattedName()));
                            return null;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> this.removeAlias(null, playername, null));
            return null;
        });
    }

    private void removeAlias(String uuid, String playername, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (uuid == null) {
            AliasData.removeAlias(playername);
        } else {
            AliasData.removeAlias(uuid.replace("-", ""));
        }
        NameUtil.updateMWPlayerDataAndEntityData(playername, false);
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + formattedName);
    }

}
