package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerStatus;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.utils.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.concurrent.Callable;

public class CommandStalk extends CommandBase {

    @Override
    public String getCommandName() {
        return "stalk";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stalk <playernames>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender));
            return;
        }

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }

        for (String name : args) {
            Multithreading.addTaskToQueue(new StalkTask(name));
        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

}

class StalkTask implements Callable<String> {

    final String name;

    public StalkTask(String name) {
        this.name = name;
    }

    @Override
    public String call() {

        try {
            MojangPlayernameToUUID apiname = new MojangPlayernameToUUID(name);
            String uuid = apiname.getUuid();

            // player found on mojang's api

            String playername = apiname.getName();
            HypixelPlayerStatus apistatus = new HypixelPlayerStatus(uuid, HypixelApiKeyUtil.getApiKey());
            HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
            LoginData logindata = new LoginData(playerdata.getPlayerData());
            if (!playername.equals(logindata.getdisplayname())) {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "This player never joined Hypixel, it might be a nick.");
                return null;
            }
            String formattedName = logindata.getFormattedName();
            if (apistatus.isOnline()) { // player is online

                if (apistatus.getGamemode().equals("Mega Walls")) { // stalked player is in MW, display currrent class and skin

                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    ChatUtil.addChatMessage(
                            new ChatComponentText(ChatUtil.getTagMW())
                                    .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                                    .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))
                                            + EnumChatFormatting.GREEN + " playing "
                                            + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                                            + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."))
                    );

                } else { // player isn't in MW
                    ChatUtil.addChatMessage(
                            new ChatComponentText(ChatUtil.getTagMW())
                                    .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                                    .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))))
                    );
                }

                return null;

            } else { // player is offline or blocking their API, stalk the playerdata info

                if (playerdata.getPlayerData() == null) { // Failed to contact hypixel's API

                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "Failed to retrieve information from Hypixel's api for : " + playername + EnumChatFormatting.RED + ".");
                    return null;

                } else if (logindata.hasNeverJoinedHypixel()) { // player never joined hypixel

                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " has never joined Hypixel.");
                    return null;

                } else if (logindata.isStaffonHypixel()) { // player is a staff member

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                            .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                            .appendSibling(new ChatComponentText(EnumChatFormatting.RED + " is completely hiding their online status from the API."
                                    + EnumChatFormatting.DARK_GRAY + " It happens for staff members.")));
                    return null;

                } else if (logindata.isHidingFromAPI()) { // player is blocking their API

                    logindata.parseLatestActivity(playerdata.getPlayerData());
                    long latestActivityTime = logindata.getLatestActivityTime();
                    String latestActivity = logindata.getLatestActivity();

                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW())
                            .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                            .appendSibling(new ChatComponentText(EnumChatFormatting.RED + " is blocking their API."));

                    if (latestActivityTime != 0 && latestActivity != null) {
                        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + " Latest activity : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(latestActivityTime) + EnumChatFormatting.GRAY + " ago " + latestActivity + EnumChatFormatting.RED + "."));
                    }

                    if (FKCounterMod.isMWEnvironement) {
                        imsg.appendSibling(new ChatComponentText(
                                EnumChatFormatting.GREEN + " Selected class : "
                                        + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                                        + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                        ));
                    }

                    ChatUtil.addChatMessage(imsg);
                    return null;

                } else if (logindata.isOnline()) { // player is online but hiding their session, that doesn't work anymore

                    if (FKCounterMod.isMWEnvironement && logindata.getMostRecentGameType().equals("Mega Walls")) { // online and in MW

                        MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                                .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                                .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                        + EnumChatFormatting.GREEN + " playing "
                                        + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                                        + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                                        + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)" + "\n"
                                )));

                    } else { // online not in MW
                        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                                .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                                .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                        + EnumChatFormatting.GREEN + "." + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)")));
                    }
                    return null;

                } else { // offline
                    String offlinesince = DateUtil.timeSince(logindata.getLastLogout());
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                            .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                            .appendSibling(new ChatComponentText(EnumChatFormatting.RED + " has been offline for " + EnumChatFormatting.YELLOW + offlinesince
                                    + EnumChatFormatting.RED + "." + (logindata.getMostRecentGameType().equals("?") ? "" : EnumChatFormatting.RED + " Last seen in : " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()))));
                    return null;
                }

            }

        } catch (ApiException e) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + e.getMessage());
        }

        return null;
    }

}
