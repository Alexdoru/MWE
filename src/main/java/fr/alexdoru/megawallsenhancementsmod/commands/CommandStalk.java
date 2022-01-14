package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerStatus;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

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
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { // api key not setup
            addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
            return;
        }

        int nbcores = Math.min(args.length, Runtime.getRuntime().availableProcessors());
        ExecutorService service = Executors.newFixedThreadPool(nbcores);

        for (String name : args) {
            service.submit(new StalkTask(name));
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
                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + "This player never joined Hypixel, it might be a nick."));
                return null;
            }
            if (apistatus.isOnline()) { // player is online

                if (apistatus.getGamemode().equals("Mega Walls")) { // player is in MW, display currrent class and skin

                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    addChatMessage(new ChatComponentText(getTagMW()
                            + EnumChatFormatting.YELLOW + logindata.getFormattedName() + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))
                            + EnumChatFormatting.GREEN + " playing "
                            + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                            + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                    ));

                } else { // player isn't in MW
                    addChatMessage(new ChatComponentText(getTagMW()
                            + EnumChatFormatting.YELLOW + logindata.getFormattedName() + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))));
                }

                return null;

            } else { // player is offline or blocking their API, stalk the playerdata info

                String formattedname = logindata.getFormattedName();

                if (playerdata.getPlayerData() == null) { // Failed to contact hypixel's API

                    addChatMessage(new ChatComponentText(getTagMW()
                            + EnumChatFormatting.RED + "Failed to retrieve information from Hypixel's api for : " + playername + EnumChatFormatting.RED + "."));
                    return null;

                } else if (logindata.hasNeverJoinedHypixel()) { // player never joined hypixel

                    addChatMessage(new ChatComponentText(getTagMW()
                            + EnumChatFormatting.YELLOW + playername + EnumChatFormatting.RED + " has never joined Hypixel."));
                    return null;

                } else if (logindata.isStaffonHypixel()) { // player is a staff member

                    addChatMessage(new ChatComponentText(getTagMW()
                            + formattedname + EnumChatFormatting.RED + " is completely hiding their online status from the API."
                            + EnumChatFormatting.DARK_GRAY + " It happens for staff members."));
                    return null;

                } else if (logindata.isHidingFromAPI()) { // player is blocking their API
                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    addChatMessage(new ChatComponentText(getTagMW()
                            + formattedname + EnumChatFormatting.RED + " is blocking their API."
                            + EnumChatFormatting.GREEN + " Selected class : "
                            + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                            + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                    ));
                    return null;

                } else if (logindata.isOnline()) { // player is online but hiding their session, that doesn't work anymore

                    if (logindata.getMostRecentGameType().equals("Mega Walls")) { // online and in MW

                        MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                        addChatMessage(new ChatComponentText(getTagMW()
                                + formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                + EnumChatFormatting.GREEN + " playing "
                                + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                                + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                                + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)" + "\n"
                        ));

                    } else { // online not in MW
                        addChatMessage(new ChatComponentText(getTagMW()
                                + formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType()
                                + EnumChatFormatting.GREEN + "." + EnumChatFormatting.DARK_GRAY + " (This player hides their session.)"));
                    }
                    return null;

                } else { // offline
                    String offlinesince = DateUtil.timeSince(logindata.getLastLogout());
                    addChatMessage(new ChatComponentText(getTagMW()
                            + formattedname + EnumChatFormatting.RED + " has been offline for " + EnumChatFormatting.YELLOW + offlinesince
                            + EnumChatFormatting.RED + "." + (logindata.getMostRecentGameType().equals("?") ? "" : EnumChatFormatting.RED + " Last seen in : " + EnumChatFormatting.YELLOW + logindata.getMostRecentGameType())));
                    return null;
                }

            }
        } catch (ApiException e) {
            e.printStackTrace();
            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
        }

        return null;
    }

}
