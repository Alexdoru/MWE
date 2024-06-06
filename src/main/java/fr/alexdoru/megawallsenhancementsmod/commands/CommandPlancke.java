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
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class CommandPlancke extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "plancke";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            this.printCommandHelp();
            return;
        }
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangPlayernameToUUID apiname = new MojangPlayernameToUUID(args[0]);
                final JsonObject playerdata = CachedHypixelPlayerData.getPlayerData(apiname.getUuid());
                final HypixelGuild hypixelGuild = args.length == 1 ? new HypixelGuild(apiname.getUuid()) : null;
                mc.addScheduledTask(() -> this.plancke(args, apiname, playerdata, hypixelGuild));
            } catch (ApiException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + e.getMessage());
                return null;
            }
            return null;
        });
    }

    private void plancke(String[] args, MojangPlayernameToUUID apiname, JsonObject playerdata, HypixelGuild hypixelGuild) {

        final LoginData loginData = new LoginData(playerdata);

        if (!apiname.getName().equals(loginData.getdisplayname()) || loginData.hasNeverJoinedHypixel()) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "This player never joined Hypixel, it might be a nick.");
            return;
        }

        final String playername = apiname.getName();
        final String formattedName = loginData.getFormattedName();

        if (args.length == 1) {
            final String guildTag = hypixelGuild.getFormattedGuildTag();
            new GeneralInfo(playerdata).printMessage(formattedName + (guildTag == null ? "" : guildTag), hypixelGuild.getGuildName());
            return;
        }

        if (args[1].equalsIgnoreCase("bw") || args[1].equalsIgnoreCase("bedwars")) { // general stats for bedwars

            ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP bedwars");

        } else if (args[1].equalsIgnoreCase("bsg") || args[1].equalsIgnoreCase("blitz")) { // general stats for blitz survival games

            new BlitzStats(playerdata).printMessage(formattedName, playername);

        } else if (args[1].equalsIgnoreCase("duel") || args[1].equalsIgnoreCase("duels")) { // general stats for duels

            ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP duels");

        } else if (args[1].equalsIgnoreCase("mw") || args[1].equalsIgnoreCase("megawalls")) { // stats for mega walls

            if (args.length == 2) {

                new MegaWallsStats(playerdata).printGeneralStatsMessage(formattedName, playername);

            } else {

                if (args[2].equalsIgnoreCase("cp") || args[2].equalsIgnoreCase("classpoint") || args[2].equalsIgnoreCase("classpoints")) {

                    new MegaWallsStats(playerdata).printClassPointsMessage(formattedName, playername);

                } else if (args[2].equalsIgnoreCase("leg") || args[2].equalsIgnoreCase("legendary") || args[2].equalsIgnoreCase("legendaries")) {

                    new MegaWallsStats(playerdata).printLegendaryMessage(formattedName, playername);

                } else {

                    final MWClass mwclass = MWClass.fromTagOrName(args[2]);
                    if (mwclass == null) {
                        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + args[2] + EnumChatFormatting.RED + " isn't a valid mega walls class name.");
                        return;
                    }
                    new MegaWallsClassStats(playerdata, mwclass.className).printMessage(formattedName, playername);

                }

            }

        } else if (args[1].equalsIgnoreCase("sw") || args[1].equalsIgnoreCase("skywars")) { // general stats for skywars

            new SkywarsStats(playerdata).printMessage(formattedName, playername);

        } else if (args[1].equalsIgnoreCase("tnt") || args[1].equalsIgnoreCase("tntgames")) { // general stats for tnt games

            ChatUtil.addChatMessage(EnumChatFormatting.RED + "WIP tntgames");

        } else if (args[1].equalsIgnoreCase("uhc")) { // general stats for UHC champions

            new UHCStats(playerdata).printMessage(formattedName, playername);

        } else {

            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + args[1] + EnumChatFormatting.RED + " isn't a valid/supported game name.");

        }

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
            final List<String> list = new ArrayList<>();
            for (final MWClass mwClass : MWClass.values()) {
                list.add(mwClass.className.toLowerCase());
            }
            list.add("classpoint");
            list.add("legendary");
            return getListOfStringsMatchingLastWord(args, list);
        }
        return null;
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                EnumChatFormatting.AQUA + ChatUtil.bar() + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GOLD + "Plancke Help\n\n")
                        + EnumChatFormatting.YELLOW + "/plancke <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Hypixel stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> bsg" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Blitz stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> sw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Skywars stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> uhc" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "UHC stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> mw" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "General Mega Walls stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> mw classname" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Class specific Mega Walls stats\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> mw cp" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Mega Walls classpoints\n"
                        + EnumChatFormatting.YELLOW + "/plancke <player> mw leg" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "Mega Walls legendary skins\n"
                        + EnumChatFormatting.AQUA + ChatUtil.bar()
        );
    }

}
