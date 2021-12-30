package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.GeneralInfo;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsStats;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandPlancke extends CommandBase {

    @Override
    public String getCommandName() {
        return "plancke";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/plancke <playername> <args(optional)>";
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

        (new Thread(() -> {

            CachedMojangUUID apiname;
            try {
                apiname = new CachedMojangUUID(args[0]);
            } catch (ApiException e) {
                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                return;
            }

            String uuid = apiname.getUuid();

            CachedHypixelPlayerData playerdata;
            try {
                playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
            } catch (ApiException e) {
                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                return;
            }

            GeneralInfo generalstats = new GeneralInfo(playerdata.getPlayerData());
            String formattedname = generalstats.getFormattedName();

            if (generalstats.hasNeverJoinedHypixel()) { // player never joined hypixel

                addChatMessage(new ChatComponentText(getTagMW()
                        + EnumChatFormatting.YELLOW + args[0] + EnumChatFormatting.RED + " has never joined Hypixel."));
                return;
            }

            if (args.length == 1) {

                addChatMessage(generalstats.getFormattedMessage(formattedname));

            } else {

                if (args[1].equalsIgnoreCase("bw") || args[1].equalsIgnoreCase("bedwars")) { // general stats for bedwars

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP bedwars"));

                } else if (args[1].equalsIgnoreCase("bsg") || args[1].equalsIgnoreCase("blitz")) { // general stats for blitz survival games

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP blitz"));

                } else if (args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) { // general stats for duels

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP duels"));

                } else if (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) { // stats for mega walls

                    if (args.length == 2) {

                        MegaWallsStats mwstats = new MegaWallsStats(playerdata.getPlayerData());
                        addChatMessage(mwstats.getFormattedMessage(formattedname, apiname.getName()));

                    } else if (args.length == 3) {

                        if (args[2].equals("cp") || args[2].equals("classpoint") || args[2].equals("classpoints")) {

                            MegaWallsStats mwstats = new MegaWallsStats(playerdata.getPlayerData());
                            addChatMessage(mwstats.getClassPointsMessage(formattedname, apiname.getName()));

                        } else {

                            MWClass mwclass = MWClass.fromTagOrName(args[2]);

                            if (mwclass == null) { // not a valid mw class
                                addChatMessage(new ChatComponentText(getTagMW()
                                        + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.RED + " isn't a valid mega walls class name."));
                                return;
                            }    // print mw stats for a certain class

                            MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwclass.className);
                            addChatMessage(mwclassstats.getFormattedMessage(formattedname, apiname.getName()));

                        }

                    }

                } else if (args[1].equalsIgnoreCase("sw") || args[1].equalsIgnoreCase("skywars")) { // general stats for skywars

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP skywars"));

                } else if (args[1].equalsIgnoreCase("tnt") || args[1].equalsIgnoreCase("tntgames")) { // general stats for tnt games

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP tntgames"));

                } else if (args[1].equalsIgnoreCase("uhc")) { // general stats for UHC champions

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP uhc"));

                } else {

                    addChatMessage(new ChatComponentText(getTagMW()
                            + EnumChatFormatting.YELLOW + args[1] + EnumChatFormatting.RED + " isn't a valid/supported game name."));

                }

            }

        })).start();

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {

        String[] games = {"megawalls"};
        String[] mwargs = {"arcanist", "assassin", "automaton", "blaze", "classpoint", "cow", "creeper", "dreadlord", "enderman", "golem", "herobrine", "hunter", "moleman", "phoenix", "pirate", "renegade", "shaman", "shark", "skeleton", "snowman", "spider", "squid", "pigman", "werewolf", "zombie"};

        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());

        if (args.length == 2)
            return getListOfStringsMatchingLastWord(args, games);

        if (args.length == 3 && (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")))
            return getListOfStringsMatchingLastWord(args, mwargs);

        return null;

    }

}
