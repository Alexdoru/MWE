package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.gui.NoCheatersConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.*;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandNocheaters extends CommandBase {

    private static HashMap<String, WDR> sortedmap = new HashMap<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

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

            new DelayedTask(() -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen()), 1);

        } else if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("stalkreportlist")) {

            boolean doStalk = args[0].equalsIgnoreCase("stalkreportlist");

            if (doStalk) {
                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { //api key not setup
                    addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
                    return;
                }
                addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Processing command..."));
            }

            Multithreading.addTaskToQueue(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Not a valid number"));
                        return null;
                    }
                }

                if (args.length == 1 || sortedmap.isEmpty()) {
                    HashMap<String, WDR> newmap = new HashMap<>(WdredPlayers.getWdredMap());
                    sortedmap = sortByValue(newmap);
                }

                IChatComponent imsgbody = new ChatComponentText("");
                boolean warning = true;

                for (Map.Entry<String, WDR> entry : sortedmap.entrySet()) {

                    String uuid = entry.getKey();
                    WDR wdr = entry.getValue();

                    if (nbreport == 9) {
                        nbreport = 1;
                        nbpage++;
                    }

                    if (nbpage == displaypage) {
                        try {
                            imsgbody.appendSibling((createReportLine(uuid, wdr, doStalk))).appendSibling(new ChatComponentText("\n"));
                            warning = false;
                        } catch (ApiException e) {
                            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                        }
                    }

                    nbreport++;

                }

                if (warning) {
                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
                } else {

                    IChatComponent imsg = new ChatComponentText(EnumChatFormatting.RED + ChatUtil.bar() + "\n" + "             ");
                    String command = doStalk ? "/nocheaters stalkreportlist" : "/nocheaters reportlist";

                    if (displaypage > 1) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " <<")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage - 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));
                    } else {
                        imsg.appendSibling(new ChatComponentText("   "));
                    }

                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " Report list (Page " + displaypage + " of " + nbpage + ")"));

                    if (displaypage < nbpage) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " >>")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage + 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));
                    }

                    imsg.appendSibling(new ChatComponentText("\n"))
                            .appendSibling(imsgbody)
                            .appendSibling(new ChatComponentText(EnumChatFormatting.RED + ChatUtil.bar()));

                    addChatMessage(imsg);

                }

                return null;

            });

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] arguments = {"config", "reportlist", "reportworld", "stalkreportlist"};
        return getListOfStringsMatchingLastWord(args, arguments);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * This gets called after you typed /nocheaters if you click on the button Report World
     */
    private void reportWorld() {
        long datenow = (new Date()).getTime();
        int nbreport = 0;
        for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            String playerName = networkPlayerInfo.getGameProfile().getName();
            MWPlayerData mwPlayerData = ((GameProfileAccessor) networkPlayerInfo.getGameProfile()).getMWPlayerData();
            if (mwPlayerData == null) {
                continue;
            }
            WDR wdr = mwPlayerData.wdr;
            if (wdr == null) {
                continue;
            }
            if (wdr.canBeReported(datenow)) {
                wdr.timestamp = datenow;
                new DelayedTask(() -> {
                    if (mc.thePlayer != null) {
                        mc.thePlayer.sendChatMessage("/wdr " + playerName + " cheating");
                    }
                }, 30 * nbreport);
                nbreport++;
            }
        }
    }

    /**
     * Returns a sorted hashmap of reports from high to low
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

    private IChatComponent createReportLine(String uuid, WDR wdr, boolean doStalk) throws ApiException {

        IChatComponent imsg = new ChatComponentText("");

        if (doStalk) {

            if (!wdr.hacks.contains("nick")) {

                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                LoginData logindata = new LoginData(playerdata.getPlayerData());
                imsg.appendSibling(new ChatComponentText(logindata.getFormattedName()).setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to run : /stalk " + logindata.getdisplayname())))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + logindata.getdisplayname()))));

                if (logindata.isHidingFromAPI()) {

                    logindata.parseLatestActivity(playerdata.getPlayerData());
                    long latestActivityTime = logindata.getLatestActivityTime();
                    String latestActivity = logindata.getLatestActivity();
                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + " " + latestActivity + " " + EnumChatFormatting.YELLOW + DateUtil.timeSince(latestActivityTime)));

                } else if (logindata.isOnline()) { // player is online

                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " Online"));

                } else { // print lastlogout

                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + " Lastlogout " + EnumChatFormatting.YELLOW + DateUtil.timeSince(logindata.getLastLogout())));

                }

            } else {

                imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + " Nick"));

            }

        } else {

            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + uuid + EnumChatFormatting.GRAY + " reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.timestamp)));

        }

        return imsg;

    }

}
