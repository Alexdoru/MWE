package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.MegaWallsClassStats;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagMW;

public class MWGameStatsEvent {

    private static final Pattern RANDOM_CLASS_PATTERN = Pattern.compile("^Random class: (\\w+)*");
    private static String chosen_class;
    private static boolean isRandom = false;
    /*Data downloaded at the start of the game*/
    private static MegaWallsClassStats MWclassStats;
    /*Stats of the last game*/
    private static MegaWallsClassStats gameStats;
    private static String formattedname;

    @SubscribeEvent
    public void onMwGame(MwGameEvent event) {
        if (event.getType() == MwGameEvent.EventType.GAME_START) {
            onGameStart();
        }
        if (event.getType() == MwGameEvent.EventType.GAME_END) {
            new DelayedTask(MWGameStatsEvent::onGameEnd, 300);
        }
    }

    private static void onGameStart() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }
        Multithreading.addTaskToQueue(() -> {
            String uuid = Minecraft.getMinecraft().thePlayer.getUniqueID().toString().replace("-", "");
            try {
                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                if (formattedname == null) {
                    LoginData logindata = new LoginData(playerdata.getPlayerData());
                    formattedname = logindata.getFormattedName();
                }
                if (!isRandom) {
                    MegaWallsClassSkinData mwclassskindata = new MegaWallsClassSkinData(playerdata.getPlayerData());
                    chosen_class = mwclassskindata.getCurrentmwclass().toLowerCase();
                }
                MWclassStats = new MegaWallsClassStats(playerdata.getPlayerData(), chosen_class);
            } catch (ApiException ignored) {
            }
            isRandom = false;
            return null;
        });
    }

    private static void onGameEnd() {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return;
        }
        Multithreading.addTaskToQueue(() -> {
            String uuid = Minecraft.getMinecraft().thePlayer.getUniqueID().toString().replace("-", "");
            try {
                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                gameStats = new MegaWallsClassStats(playerdata.getPlayerData(), chosen_class);
                if (MWclassStats == null) {
                    return null;
                }
                gameStats.minus(MWclassStats);
                addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.YELLOW + "Click to view the stats of your " + EnumChatFormatting.AQUA + "Mega Walls " + EnumChatFormatting.YELLOW + "game!")
                        .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.MW_GAME_END_STATS))));
            } catch (ApiException | IllegalArgumentException | IllegalAccessException ignored) {}
            return null;
        });
    }

    public static MegaWallsClassStats getGameStats() {
        return gameStats;
    }

    public static String getFormattedname() {
        return formattedname;
    }

    public static boolean processMessage(String msg) {
        Matcher matcher = RANDOM_CLASS_PATTERN.matcher(msg);
        if (matcher.matches()) {
            chosen_class = matcher.group(1).toLowerCase();
            isRandom = true;
            return true;
        }
        return false;
    }

}
