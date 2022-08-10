package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.*;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelGuild;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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
            addChatMessage(getCommandHelp());
            return;
        }

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            printApikeySetupInfo();
            return;
        }

        Multithreading.addTaskToQueue(() -> {

            CachedMojangUUID apiname;
            try {
                apiname = new CachedMojangUUID(args[0]);
            } catch (ApiException e) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e.getMessage()));
                return null;
            }

            String uuid = apiname.getUuid();
            String playername = apiname.getName();
            CachedHypixelPlayerData playerdata;
            GeneralInfo generalstats;
            try {
                playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                generalstats = new GeneralInfo(playerdata.getPlayerData());
                if (!playername.equals(generalstats.getdisplayname())) {
                    addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + "This player never joined Hypixel, it might be a nick."));
                    return null;
                }
            } catch (ApiException e) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e.getMessage()));
                return null;
            }

            String formattedName = generalstats.getFormattedName();

            if (generalstats.hasNeverJoinedHypixel()) { // player never joined hypixel

                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.YELLOW + args[0] + EnumChatFormatting.RED + " has never joined Hypixel."));
                return null;
            }

            if (args.length == 1) {

                HypixelGuild hypixelGuild = new HypixelGuild(uuid, HypixelApiKeyUtil.getApiKey());
                String guildTag = hypixelGuild.getFormattedGuildTag();
                addChatMessage(generalstats.getFormattedMessage(formattedName + (guildTag == null ? "" : guildTag), hypixelGuild.getGuildName()));

            } else {

                if (args[1].equalsIgnoreCase("bw") || args[1].equalsIgnoreCase("bedwars")) { // general stats for bedwars

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP bedwars"));

                } else if (args[1].equalsIgnoreCase("bsg") || args[1].equalsIgnoreCase("blitz")) { // general stats for blitz survival games

                    final BlitzStats bsgstats = new BlitzStats(playerdata.getPlayerData());
                    addChatMessage(bsgstats.getFormattedMessage(formattedName, playername));

                } else if (args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) { // general stats for duels

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP duels"));

                } else if (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) { // stats for mega walls

                    if (args.length == 2) {

                        MegaWallsStats mwstats = new MegaWallsStats(playerdata.getPlayerData());
                        addChatMessage(mwstats.getFormattedMessage(formattedName, playername));

                    } else {

                        if (args[2].equals("cp") || args[2].equals("classpoint") || args[2].equals("classpoints")) {

                            MegaWallsStats mwstats = new MegaWallsStats(playerdata.getPlayerData());
                            addChatMessage(mwstats.getClassPointsMessage(formattedName, playername));

                        } else {

                            MWClass mwclass = MWClass.fromTagOrName(args[2]);
                            if (mwclass == null) {
                                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.RED + " isn't a valid mega walls class name."));
                                return null;
                            }
                            MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata.getPlayerData(), mwclass.className);
                            addChatMessage(mwclassstats.getFormattedMessage(formattedName, playername));

                        }

                    }

                } else if (args[1].equalsIgnoreCase("sw") || args[1].equalsIgnoreCase("skywars")) { // general stats for skywars

                    SkywarsStats skywarsStats = new SkywarsStats(playerdata.getPlayerData());
                    addChatMessage(skywarsStats.getFormattedMessage(formattedName, playername));

                } else if (args[1].equalsIgnoreCase("tnt") || args[1].equalsIgnoreCase("tntgames")) { // general stats for tnt games

                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "WIP tntgames"));

                } else if (args[1].equalsIgnoreCase("uhc")) { // general stats for UHC champions

                    UHCStats uhcStats = new UHCStats(playerdata.getPlayerData());
                    addChatMessage(uhcStats.getFormattedMessage(formattedName, playername));

                } else {

                    addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.YELLOW + args[1] + EnumChatFormatting.RED + " isn't a valid/supported game name."));

                }

            }

            return null;

        });

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        if (args.length == 2) {
            final String[] games = {"blitz", "megawalls", "skywars", "uhc"};
            return getListOfStringsMatchingLastWord(args, games);
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls"))) {
            final String[] mwargs = {"arcanist", "assassin", "automaton", "blaze", "classpoint", "cow", "creeper", "dreadlord", "enderman", "golem", "herobrine", "hunter", "moleman", "phoenix", "pirate", "renegade", "shaman", "shark", "skeleton", "snowman", "spider", "squid", "pigman", "werewolf", "zombie"};
            return getListOfStringsMatchingLastWord(args, mwargs);
        }
        return null;
    }

    private IChatComponent getCommandHelp() {
        return new ChatComponentText(EnumChatFormatting.AQUA + bar() + "\n"
                + centerLine(EnumChatFormatting.GOLD + "Plancke Help\n\n")
                + EnumChatFormatting.YELLOW + "/plancke <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Hypixel stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> bsg" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Blitz stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> sw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Skywars stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> uhc" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "UHC stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Mega Walls stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw classname" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Class specific Mega Walls stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw cp" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Mega Walls classpoints\n"
                + EnumChatFormatting.AQUA + bar()
        );
    }

}
