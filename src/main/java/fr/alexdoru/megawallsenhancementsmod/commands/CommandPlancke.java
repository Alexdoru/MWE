package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.*;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelGuild;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class CommandPlancke extends MyAbstractCommand {

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
            ChatUtil.addChatMessage(getCommandHelp());
            return;
        }

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }

        MultithreadingUtil.addTaskToQueue(() -> {

            final MojangPlayernameToUUID apiname;
            try {
                apiname = new MojangPlayernameToUUID(args[0]);
            } catch (ApiException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + e.getMessage());
                return null;
            }

            final String uuid = apiname.getUuid();
            final String playername = apiname.getName();
            final JsonObject playerdata;
            final GeneralInfo generalstats;
            try {
                playerdata = CachedHypixelPlayerData.getPlayerData(uuid);
                generalstats = new GeneralInfo(playerdata);
                if (!playername.equals(generalstats.getdisplayname())) {
                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "This player never joined Hypixel, it might be a nick.");
                    return null;
                }
            } catch (ApiException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + e.getMessage());
                return null;
            }

            final String formattedName = generalstats.getFormattedName();

            if (generalstats.hasNeverJoinedHypixel()) { // player never joined hypixel

                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + args[0] + EnumChatFormatting.RED + " has never joined Hypixel.");
                return null;
            }

            if (args.length == 1) {

                final HypixelGuild hypixelGuild = new HypixelGuild(uuid);
                final String guildTag = hypixelGuild.getFormattedGuildTag();
                ChatUtil.addChatMessage(generalstats.getFormattedMessage(formattedName + (guildTag == null ? "" : guildTag), hypixelGuild.getGuildName()));

            } else {

                if (args[1].equalsIgnoreCase("bw") || args[1].equalsIgnoreCase("bedwars")) { // general stats for bedwars

                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP bedwars");

                } else if (args[1].equalsIgnoreCase("bsg") || args[1].equalsIgnoreCase("blitz")) { // general stats for blitz survival games

                    final BlitzStats bsgstats = new BlitzStats(playerdata);
                    ChatUtil.addChatMessage(bsgstats.getFormattedMessage(formattedName, playername));

                } else if (args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) { // general stats for duels

                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP duels");

                } else if (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) { // stats for mega walls

                    if (args.length == 2) {

                        final MegaWallsStats mwstats = new MegaWallsStats(playerdata);
                        ChatUtil.addChatMessage(mwstats.getGeneralStatsMessage(formattedName, playername));

                    } else {

                        if (args[2].equalsIgnoreCase("cp") || args[2].equalsIgnoreCase("classpoint") || args[2].equalsIgnoreCase("classpoints")) {

                            final MegaWallsStats mwstats = new MegaWallsStats(playerdata);
                            ChatUtil.addChatMessage(mwstats.getClassPointsMessage(formattedName, playername));

                        } else if (args[2].equalsIgnoreCase("leg") || args[2].equalsIgnoreCase("legendary") || args[2].equalsIgnoreCase("legendaries")) {

                            final MegaWallsStats mwstats = new MegaWallsStats(playerdata);
                            ChatUtil.addChatMessage(mwstats.getLegendaryMessage(formattedName, playername));

                        } else {

                            final MWClass mwclass = MWClass.fromTagOrName(args[2]);
                            if (mwclass == null) {
                                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.RED + " isn't a valid mega walls class name.");
                                return null;
                            }
                            final MegaWallsClassStats mwclassstats = new MegaWallsClassStats(playerdata, mwclass.className);
                            ChatUtil.addChatMessage(mwclassstats.getFormattedMessage(formattedName, playername));

                        }

                    }

                } else if (args[1].equalsIgnoreCase("sw") || args[1].equalsIgnoreCase("skywars")) { // general stats for skywars

                    final SkywarsStats skywarsStats = new SkywarsStats(playerdata);
                    ChatUtil.addChatMessage(skywarsStats.getFormattedMessage(formattedName, playername));

                } else if (args[1].equalsIgnoreCase("tnt") || args[1].equalsIgnoreCase("tntgames")) { // general stats for tnt games

                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP tntgames");

                } else if (args[1].equalsIgnoreCase("uhc")) { // general stats for UHC champions

                    final UHCStats uhcStats = new UHCStats(playerdata);
                    ChatUtil.addChatMessage(uhcStats.getFormattedMessage(formattedName, playername));

                } else {

                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + args[1] + EnumChatFormatting.RED + " isn't a valid/supported game name.");

                }

            }

            return null;

        });

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
            final String[] mwargs = {"arcanist", "assassin", "automaton", "blaze", "classpoint", "cow", "creeper", "dreadlord", "enderman", "golem", "herobrine", "hunter", "legendary", "moleman", "phoenix", "pirate", "renegade", "shaman", "shark", "skeleton", "snowman", "spider", "squid", "pigman", "werewolf", "zombie"};
            return getListOfStringsMatchingLastWord(args, mwargs);
        }
        return null;
    }

    private IChatComponent getCommandHelp() {
        return new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n"
                + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Plancke Help\n\n")
                + EnumChatFormatting.YELLOW + "/plancke <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Hypixel stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> bsg" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Blitz stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> sw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Skywars stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> uhc" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "UHC stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Mega Walls stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw classname" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Class specific Mega Walls stats\n"
                + EnumChatFormatting.YELLOW + "/plancke <player> mw cp" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Mega Walls classpoints\n"
                + EnumChatFormatting.AQUA + ChatUtil.bar()
        );
    }

}
