package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.utils.MinecraftUtils;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class ChatEvents {

    private static final String HUNTER_STRENGTH_MESSAGE = "F.O.N. (Strength) 10";
    private static final String GENERAL_START_MESSAGE = "The game starts in 1 second!";
    private static final String OWN_WITHER_DEATH_MESSAGE = "Your wither has died. You can no longer respawn!";
    private static final Pattern SHOUT_PATTERN1 = Pattern.compile("^\\[SHOUT\\].+?(\\w+) is b?hop?ping.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHOUT_PATTERN2 = Pattern.compile("^\\[SHOUT\\].+?(?:wdr|report) (\\w+) (\\w+).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern COINS_PATTERN = Pattern.compile("^\\+\\d+ coins!( \\((?:Active Booster, |)\\w+'s Network Booster\\)).*");
    private static final Pattern API_KEY_PATTERN = Pattern.compile("^Your new API key is ([a-zA-Z0-9-]+)");

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {

        String msg = event.message.getUnformattedText();
        String fmsg = event.message.getFormattedText();

        /*normal chat messages*/
        if (event.type == 0) {

            /*
             *  cancels hunger message in mega walls
             */
            if (msg.equals("Get to the middle to stop the hunger!")) {
                event.setCanceled(true);
                return;
            }

            if (msg.equals(GENERAL_START_MESSAGE)) {
                GameInfoGrabber.saveinfoOnGameStart();
                SquadEvent.formSquad();
                return;
            }

            if (msg.equals(OWN_WITHER_DEATH_MESSAGE)) {
                KillCooldownGui.hideGUI();
                return;
            }

            /*
             * shortens the coins messages removing the booster info
             */
            if (MWEnConfigHandler.shortencoinmessage) {
                Matcher matchercoins = COINS_PATTERN.matcher(msg);
                if (matchercoins.matches()) {
                    event.message = new ChatComponentText(fmsg.replace(matchercoins.group(1), ""));
                    return;
                }
            }

            if (KillCounter.processMessage(fmsg, msg)) {
                return;
            }
            if (MWEnConfigHandler.show_ArrowHitGui && ArrowHitGui.processMessage(msg)) {
                return;
            }
            if (MWEnConfigHandler.reportsuggestions && parseReportMessage(msg)) {
                return;
            }
            if (MWGameStatsEvent.processMessage(msg)) {
                return;
            }
            if (parseAPIKey(msg)) {
                return;
            }

            /*Status messages*/
        } else if (MWEnConfigHandler.hunterStrengthSound && event.type == 2) {
            if (msg.contains(HUNTER_STRENGTH_MESSAGE)) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.wolf.growl"), 1.0F));
            }
        }

    }

    /*
     * automatically sets up the api key on hypixel when you type /api new
     */
    private boolean parseAPIKey(String msg) {
        Matcher matcherapikey = API_KEY_PATTERN.matcher(msg);
        if (matcherapikey.matches()) {
            if (!MinecraftUtils.isHypixel()) {
                return false;
            }
            String api_key = matcherapikey.group(1);
            HypixelApiKeyUtil.setApiKey(api_key);
            return true;
        }
        return false;
    }

    private static boolean parseReportMessage(String msgIn) {

        Matcher matcher1 = SHOUT_PATTERN1.matcher(msgIn);
        Matcher matcher2 = SHOUT_PATTERN2.matcher(msgIn);

        if (matcher1.matches()) {
            String name = matcher1.group(1);
            String cheat = "bhop";
            if (isAValidName(name)) {
                printReportSuggestion(name, cheat);
            }
            return true;
        } else if (matcher2.matches()) {
            String name = matcher2.group(1);
            String cheat = matcher2.group(2);
            if (isAValidCheat(cheat) || isAValidName(name)) {
                printReportSuggestion(name, cheat);
            }
            return true;
        }

        return false;

    }

    private static void printReportSuggestion(String playername, String cheat) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.successful_hit"), 1.0F));
        IChatComponent imsg = new ChatComponentText(getTagMW() + EnumChatFormatting.DARK_RED + "Command suggestion : ")
                .appendSibling(makeReportButtons(playername, cheat, ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Action.SUGGEST_COMMAND));
        addChatMessage(imsg);
    }

    private static boolean isAValidName(String playername) {

        for (NetworkPlayerInfo networkplayerinfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {

            if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAValidCheat(String cheat) {
        return CommandReport.cheatsList.contains(cheat);
    }

}
