package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.NoCheatersConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.ClientCommandHandler;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandNocheaters extends CommandBase {

    private static HashMap<String, WDR> sortedmap = new HashMap<>();

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 0) {

            List<IChatComponent> list = NoCheatersEvents.getReportMessagesforWorld();

            if (list.isEmpty()) {
                addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.GREEN + "No reported player here !"));
            } else {
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.YELLOW + "Reported players : ")
                        .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "Report ALL"))
                        .setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to report again all the reported players in your world")))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nocheaters reportworld"))));
                for (IChatComponent report : list) {
                    addChatMessage(report);
                }

            }

            return;

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reportworld")) {

            reportWorld();

        } else if (args.length == 1 && args[0].equalsIgnoreCase("config")) {

            new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new NoCheatersConfigGuiScreen()), 1);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {

            if (args[1].equalsIgnoreCase("icons")) {

                if (ConfigHandler.toggleicons) {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.RED + "Icons disabled"));
                } else {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.GREEN + "Icons enabled"));
                }
                ConfigHandler.toggleIcons();
                ConfigHandler.saveConfig();

            } else if (args[1].equalsIgnoreCase("autoreport")) {

                if (ConfigHandler.toggleautoreport) {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.RED + "Autoreports disabled"));
                } else {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.GREEN + "Autoreports enabled"));
                }
                ConfigHandler.toggleautoreport = !ConfigHandler.toggleautoreport;
                ConfigHandler.saveConfig();

            } else if (args[1].equalsIgnoreCase("warnings")) {

                if (ConfigHandler.togglewarnings) {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.RED + "Warnings disabled"));
                } else {
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.GREEN + "Warnings enabled"));
                }
                ConfigHandler.togglewarnings = !ConfigHandler.togglewarnings;
                ConfigHandler.saveConfig();

            }

        } else if (args.length == 1 && args[0].equalsIgnoreCase("getgameid")) {

            addChatMessage(new ChatComponentText(getTagNoCheaters() +
                    EnumChatFormatting.GREEN + "Current server : " + EnumChatFormatting.DARK_GRAY + GameInfoGrabber.getGameIDfromscoreboard()));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("getstoreddata")) {

            addChatMessage(new ChatComponentText(getTagNoCheaters() +
                    EnumChatFormatting.GREEN + "ServerID stored : " + EnumChatFormatting.DARK_GRAY + GameInfoGrabber.getstoredGameID() + "\n"
                    + EnumChatFormatting.GREEN + "Timestamp stored (local) : " + EnumChatFormatting.DARK_GRAY + DateUtil.localformatTimestamp(GameInfoGrabber.getstoredTimestamp()) + "\n"
                    + EnumChatFormatting.GREEN + "Timestamp stored (EST) : " + EnumChatFormatting.DARK_GRAY + DateUtil.ESTformatTimestamp(GameInfoGrabber.getstoredTimestamp())));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("isitprepphase")) {

            addChatMessage(new ChatComponentText(getTagNoCheaters() +
                    EnumChatFormatting.GREEN + "Preparation phase : " + EnumChatFormatting.DARK_GRAY + (FKCounterMod.isitPrepPhase() ? "True" : "False")));

        } else if (args.length == 1 && args[0].equalsIgnoreCase("getscoreboard")) {

            GameInfoGrabber.debugGetScoreboard();

        } else if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("stalkreportlist")) {

            boolean doStalk = args[0].equalsIgnoreCase("stalkreportlist");

            if (doStalk) {

                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { //api key not setup
                    addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
                    return;
                }

                addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Processing command..."));

            }

            (new Thread(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                StringBuilder messagebody = new StringBuilder();

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Not a valid number"));
                        e.printStackTrace();
                    }
                }

                if (args.length == 1 || sortedmap.isEmpty()) {

                    // fill a new hashmap with only timestamped reports
                    HashMap<String, WDR> timestampedmap = new HashMap<>();
                    for (Entry<String, WDR> entry : WdredPlayers.getWdredMap().entrySet()) {
                        if (entry.getValue().hacks.get(0).charAt(0) == '-') {
                            timestampedmap.put(entry.getKey(), entry.getValue());
                        }
                    }

                    // sorts the map
                    sortedmap = sortByValue(timestampedmap);
                }

                for (Entry<String, WDR> entry : sortedmap.entrySet()) {

                    String uuid = entry.getKey();
                    WDR wdr = entry.getValue();

                    if (nbreport == 9) {
                        nbreport = 1;
                        nbpage++;
                    }

                    if (nbpage == displaypage) {
                        try {
                            messagebody.append(createReportLine(uuid, wdr, doStalk)).append(",{\"text\":\"\\n\"}");
                        } catch (ApiException e) {
                            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                        }
                    }

                    nbreport++;

                }

                if (!messagebody.toString().equals("")) {
                    addChatMessage(IChatComponent.Serializer.jsonToComponent(makeChatList("Timestamped Reports", messagebody.toString(), displaypage, nbpage, (doStalk ? "/nocheaters stalkreportlist" : "/nocheaters reportlist"))));
                } else {
                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
                }

            })).start();

        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {

            addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "/nocheaters : scans the world and warns you about previously reported players \n" +
                            EnumChatFormatting.RED + "/nocheaters toggle warnings : toggles the warning messages on or off \n" +
                            EnumChatFormatting.RED + "/nocheaters reportlist : prints the reports with timestamps"));

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] arguments = {"config", "help", "toggle", "reportlist", "reportworld"};
        String[] toggleargs = {"autoreport", "icons", "warnings"};
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, arguments) : ((args.length == 2 && args[0].equals("toggle")) ? getListOfStringsMatchingLastWord(args, toggleargs) : null);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * Builds a chat line with the name of the player, the date when you reported him and his online status
     *
     * @param doStalk - do you want to stalk the reported player
     */
    public static String createReportLine(String uuid, WDR wdr, boolean doStalk) throws ApiException {
        // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
        String playername = wdr.hacks.get(2);
        String message = NoCheatersEvents.createPlayerTimestampedMsg(playername, wdr, "red")[0]

                + ",{\"text\":\" reported on : \",\"color\":\"dark_gray\"}"

                + ",{\"text\":\"" + (new SimpleDateFormat("dd/MM")).format((Long.parseLong(wdr.hacks.get(3)))) + "\",\"color\":\"yellow\"}";

        if (doStalk) {
            if (!wdr.hacks.contains("nick")) {

                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                LoginData logindata = new LoginData(playerdata.getPlayerData());

                if (logindata.isHidingFromAPI()) {

                    message = message + ",{\"text\":\" API blocked\",\"color\":\"red\"}";

                } else if (logindata.isOnline()) { // player is online

                    message = message + ",{\"text\":\" Online\",\"color\":\"green\"}";

                } else { // print lastlogout

                    message = message + ",{\"text\":\" Lastlogout \",\"color\":\"dark_gray\"}" + ",{\"text\":\"" + DateUtil.timeSince(logindata.getLastLogout()) + "\",\"color\":\"yellow\"}";

                }

            } else {

                message = message + ",{\"text\":\" Nick\",\"color\":\"dark_purple\"}";

            }
        }

        return message;

    }

    /**
     * Returns a sorted hashmap of timestamped reports from high to low
     */
    private static HashMap<String, WDR> sortByValue(HashMap<String, WDR> hashmapIn) {
        List<Map.Entry<String, WDR>> list = new LinkedList<>(hashmapIn.entrySet());
        list.sort((o1, o2) -> (o1.getValue()).compareToInvert(o2.getValue()));
        HashMap<String, WDR> temp = new LinkedHashMap<>();
        for (Map.Entry<String, WDR> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void reportWorld() {

        long timenow = (new Date()).getTime();
        int nbreport = 0;

        for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {

            String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
            String playerName = networkPlayerInfo.getGameProfile().getName();
            WDR wdr = WdredPlayers.getWdredMap().get(uuid);
            boolean isaNick = false;

            if (wdr == null) {
                wdr = WdredPlayers.getWdredMap().get(playerName);
                if (wdr != null) {
                    isaNick = true;
                }
            }

            if (wdr == null) {
                continue;
            }

            if (timenow - wdr.timestamp > ConfigHandler.timeBetweenReports) {

                if (isaNick) {

                    new DelayedTask(() -> {
                        if (Minecraft.getMinecraft().thePlayer != null) {
                            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/sendreportagain " + playerName + " " + playerName);
                        }
                    }, 1 + 10 * nbreport);

                } else {

                    new DelayedTask(() -> {
                        if (Minecraft.getMinecraft().thePlayer != null) {
                            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/sendreportagain " + uuid + " " + playerName);
                        }
                    }, 1 + 10 * nbreport);

                }

                nbreport++;
            }

        }

    }

}
