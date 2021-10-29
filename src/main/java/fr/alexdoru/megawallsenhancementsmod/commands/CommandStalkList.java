package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerStatus;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandStalkList extends CommandBase {

    @Override
    public String getCommandName() {
        return "stalklist";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stalklist";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { // api key not setup
            addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
            return;
        }

        if (args.length >= 1 && (args[0].equals("list") | args[0].equals("l"))) {

            if (args.length >= 2) {

                String listkey = args[1].contains("stalk") ? args[1] : "stalk" + args[1];
                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.GREEN + "Making player list for : " + listkey + "..."));

                if (args.length == 2) {
                    (new Thread(() -> {
                        try {
                            listThePlayers(listkey, 1);
                        } catch (ApiException e) {
                            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                            e.printStackTrace();
                        }
                    })).start();
                } else {
                    (new Thread(() -> {
                        try {
                            listThePlayers(listkey, parseInt(args[2]));
                        } catch (NumberInvalidException e) {
                            e.printStackTrace();
                        } catch (ApiException e) {
                            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
                            e.printStackTrace();
                        }
                    })).start();
                }

            } else {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " list <listname> <page(optional)>"));
            }
            return;

        } else if (args.length >= 1 && args[0].equals("move")) {

            if (args.length != 3) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender) + " move <uuid> <newlistname>"));
            } else {
                movePlayerTo(args[1], args[2]);
            }
            return;

        }

        String keyword = "stalk";

        if (args.length > 0) {
            keyword += args[0];
        }

        int i = 0;
        int nbcores = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(nbcores);

        for (Entry<String, WDR> entry : WdredPlayers.getWdredMap().entrySet()) {

            String uuid = entry.getKey();
            WDR wdr = entry.getValue();

            if (wdr.hacks.contains(keyword)) {
                i++;
                service.submit(new StalkListTask(uuid));
            }

        }
        addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.GREEN + "Using : \"" + keyword + "\", Stalking " + i + " players..."));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private void listThePlayers(String listkey, int displaypage) throws ApiException {

        int nbstalk = 1; // pour compter le nb de report et en afficher que 8 par page
        int nbpage = 1;

        IChatComponent imsgbody = new ChatComponentText("");

        for (Entry<String, WDR> entry : WdredPlayers.getWdredMap().entrySet()) {
            if (entry.getValue().hacks.contains(listkey)) {

                if (nbstalk == 9) {
                    nbstalk = 1;
                    nbpage++;
                }

                if (nbpage == displaypage) {
                    HypixelPlayerData playerdata = new HypixelPlayerData(entry.getKey(), HypixelApiKeyUtil.getApiKey());
                    LoginData logindata = new LoginData(playerdata.getPlayerData());

                    imsgbody.appendSibling(new ChatComponentText(logindata.getFormattedName() + "             "));
                    imsgbody.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Move" + "\n")
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to move this player to another list")))
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/stalklist move " + entry.getKey() + " "))));

                }

                nbstalk++;

            }

        }

        addChatMessage(makeiChatList("Players in " + listkey, imsgbody, displaypage, nbpage, "/stalklist list " + listkey));

    }

    private void movePlayerTo(String uuid, String newlist) {

        WDR wdr = WdredPlayers.getWdredMap().get(uuid);

        if (wdr == null) {
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This uuid is not report list"));
        } else {
            String newlistname = newlist.contains("stalk") ? newlist : "stalk" + newlist;
            ArrayList<String> newHacksArray = new ArrayList<>();
            for (String arg : wdr.hacks) {
                if (arg.contains("stalk")) {
                    newHacksArray.add(newlistname);
                } else {
                    newHacksArray.add(arg);
                }
            }
            WdredPlayers.getWdredMap().put(uuid, new WDR(wdr.timestamp, newHacksArray));
            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.GREEN + "Successfully moved this player to : " + EnumChatFormatting.GOLD + newlistname));
        }

    }

}

class StalkListTask implements Callable<String> {

    final String uuid;

    public StalkListTask(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String call() {
        // les mecs qui sont nick se font pas ban assez vite par ce qu'ils se font pas assez report,
        // TODO faire un truc pour proposer de report si le mec est stalk + des cheats ?
        try {

            HypixelPlayerStatus apistatus = new HypixelPlayerStatus(uuid, HypixelApiKeyUtil.getApiKey());
            IChatComponent imsg;

            if (apistatus.isOnline()) { // player is online, send api status request to fetch the map, game and class

                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                LoginData logindata = new LoginData(playerdata.getPlayerData());
                String formattedname = logindata.getFormattedName();

                if (apistatus.getGamemode().equals("Mega Walls")) { // player is in MW, display currrent class and skin

                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    imsg = new ChatComponentText(getTagMW()
                            + formattedname + EnumChatFormatting.GREEN + " is in " + EnumChatFormatting.YELLOW + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GREEN + " on " + EnumChatFormatting.YELLOW + apistatus.getMap()))
                            + EnumChatFormatting.GREEN + " playing "
                            + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass())
                            + EnumChatFormatting.GREEN + " with the " + EnumChatFormatting.YELLOW + (mwclassskindata.getCurrentmwskin() == null ? (mwclassskindata.getCurrentmwclass() == null ? "?" : mwclassskindata.getCurrentmwclass()) : mwclassskindata.getCurrentmwskin()) + EnumChatFormatting.GREEN + " skin."
                    );

                } else { // player isn't in MW
                    imsg = new ChatComponentText(getTagMW()
                            + formattedname + EnumChatFormatting.GRAY + " is in " + apistatus.getGamemode() + " " + apistatus.getMode() +
                            (apistatus.getMap() == null ? "" : (EnumChatFormatting.GRAY + " on " + EnumChatFormatting.GRAY + apistatus.getMap())));

                }

                if (!logindata.isMVPPlusPlus()) {
                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + " Move")
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to move this player to another list")))
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/stalklist move " + this.uuid + " "))));
                }

                addChatMessage(imsg);

            }

        } catch (ApiException e) {
            addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.RED + e.getMessage()));
        }

        return null;

    }

}

