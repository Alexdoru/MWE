package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.api.MWECommandBase;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.data.AliasDataManager;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.data.PlayerDataManager;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.cache.CachedHypixelPlayerData;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.http.requests.MojangNameToUUID;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.NetInfoOrdering;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CommandAddAlias extends MWECommandBase {

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
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                this.removeAlias(args[1]);
            } else {
                this.addAlias(args[0], args[1]);
            }
        } else {
            this.printCommandHelp();
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getPlayers());
        }
        final List<String> onlinePlayersByName = TabCompletionUtil.getPlayers();
        onlinePlayersByName.addAll(Arrays.asList("list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ad");
    }

    @Override
    protected void printCommandHelp() {
        final String slashCommand = '/' + getCommandName();
        printCommandHelpBlock(EnumChatFormatting.GREEN, "AddAlias Help", new String[][]{
                {slashCommand, "Lists the alias in the lobby"},
                {slashCommand + " <player> <alias>", "Adds an alias for the player"},
                {slashCommand + " <remove> <player>", "Removes the alias for the player"},
                {slashCommand + " list", "Prints list of alias"}
        });
    }

    private void listAlias(String[] args) {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
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

        final List<Map.Entry<String, String>> entryList = AliasDataManager.getEntries();

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
        for (final NetworkPlayerInfo netInfo : NetInfoOrdering.vanillaSortingCopyOf(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())) {
            if (AliasDataManager.getAlias(netInfo.getGameProfile().getId(), netInfo.getGameProfile().getName()) != null) {
                if (!found) {
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
                    ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "In this lobby :\n");
                    found = true;
                }
                ChatUtil.addChatMessage(NameFormatter.getTablistName(netInfo));
            }
        }
        if (found) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + ChatUtil.bar());
        } else {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No one in this lobby has an alias");
        }
    }

    private void addAlias(String playername, String alias) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
                if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                    this.addAlias(
                            null,
                            netInfo.getGameProfile().getName(),
                            alias,
                            NameFormatter.getVanillaName(netInfo)
                    );
                    return;
                }
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final IPlayerUUID playerID = MojangNameToUUID.getPlayerUUID(playername);
                String name = null;
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                    if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                        name = loginData.getFormattedName();
                    }
                }
                final String formattedName = name;
                mc.addScheduledTask(() -> this.addAlias(playerID.getId(), playerID.getName(), alias, formattedName));
            } catch (ApiException ignored) {}
        });
    }

    private void addAlias(UUID uuid, String playername, String alias, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (uuid == null) {
            AliasDataManager.putAlias(null, playername, alias);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for the " + EnumChatFormatting.DARK_PURPLE + "nicked " + EnumChatFormatting.GREEN + "player "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        } else {
            AliasDataManager.putAlias(uuid, null, alias);
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Added alias for "
                    + EnumChatFormatting.GOLD + formattedName + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GOLD + alias + EnumChatFormatting.WHITE + ")");
        }
    }

    private void removeAlias(String playername) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (PlayerDataManager.isNickedPlayer(netInfo.getGameProfile().getId())) {
                if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                    if (PlayerDataManager.isRealPlayer(netInfo.getGameProfile().getId())) {
                        this.removeAlias(
                                null,
                                netInfo.getGameProfile().getName(),
                                NameFormatter.getVanillaName(netInfo)
                        );
                        return;
                    }
                }
            }
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final IPlayerUUID playerID = MojangNameToUUID.getPlayerUUID(playername);
                String formattedName = null;
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
                        if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                            formattedName = loginData.getFormattedName();
                        }
                    } catch (ApiException ignored) {}
                }
                final String finalFormattedName = formattedName;
                mc.addScheduledTask(() -> this.removeAlias(playerID.getId(), playerID.getName(), finalFormattedName));
                return;
            } catch (ApiException ignored) {}
            // nicked player
            mc.addScheduledTask(() -> this.removeAlias(null, playername, null));
        });
    }

    private void removeAlias(UUID uuid, String playername, String formattedName) {
        if (formattedName == null) {
            formattedName = playername;
        }
        if (!AliasDataManager.removeAlias(uuid, playername)) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Player does not have an alias.");
            return;
        }
        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + formattedName);
    }

}
