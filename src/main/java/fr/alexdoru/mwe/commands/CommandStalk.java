package fr.alexdoru.mwe.commands;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.hypixelplayerdataparser.LoginData;
import fr.alexdoru.mwe.http.hypixelplayerdataparser.MegaWallsClassSkinData;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.http.requests.HypixelPlayerStatus;
import fr.alexdoru.mwe.http.requests.MojangNameToUUID;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class CommandStalk extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "stalk";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtil.addChatMessage(RED + "Usage : " + getCommandUsage(sender) + " <playernames...>");
            return;
        }
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }
        for (final String name : args) {
            this.stalkPlayer(name);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
    }

    private void stalkPlayer(String name) {
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangNameToUUID apiname = new MojangNameToUUID(name);
                final HypixelPlayerData playerData = new HypixelPlayerData(apiname.getUuid());
                final LoginData loginData = new LoginData(playerData.getPlayerData());
                if (!apiname.getName().equals(loginData.getdisplayname()) || loginData.hasNeverJoinedHypixel()) {
                    ChatUtil.addChatMessage(ChatUtil.getTagMW() + RED + "This player never joined Hypixel, it might be a nick.");
                    return null;
                }
                final HypixelPlayerStatus playerStatus = loginData.isHidingFromAPI() ? null : new HypixelPlayerStatus(apiname.getUuid());
                Minecraft.getMinecraft().addScheduledTask(() -> this.stalkPlayer(playerData, loginData, playerStatus));
            } catch (ApiException e) {
                ChatUtil.addChatMessage(RED + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void stalkPlayer(HypixelPlayerData playerData, LoginData loginData, HypixelPlayerStatus playerStatus) {

        final String playername = loginData.getdisplayname();
        final String formattedName = loginData.getFormattedName();

        if (loginData.isStaffonHypixel()) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                    .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                    .appendText(RED + " is completely hiding their online status from the API." + DARK_GRAY + " It happens for staff members."));
            return;
        }

        if (playerStatus != null) {
            if (playerStatus.isOnline()) {
                final IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW())
                        .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                        .appendText(GREEN + " is in " + YELLOW + playerStatus.getGamemode() + " " + playerStatus.getMode()
                                + (playerStatus.getMap() == null ? "" : (GREEN + " on " + YELLOW + playerStatus.getMap()))
                                + GREEN + ".");
                if ("Mega Walls".equals(playerStatus.getGamemode())) {
                    this.appendSelectedClass(imsg, playerData);
                }
                ChatUtil.addChatMessage(imsg);
            } else { // offline
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW())
                        .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                        .appendText(RED + " has been offline for " + YELLOW + DateUtil.timeSince(loginData.getLastLogout())
                                + RED + "." + (loginData.getMostRecentGameType().equals("?") ? "" : RED + " Last seen in : " + YELLOW + loginData.getMostRecentGameType())));
            }
            return;
        }

        // player is offline or blocking their API, stalk the playerdata info
        loginData.parseLatestActivity(playerData.getPlayerData());
        final long latestActivityTime = loginData.getLatestActivityTime();
        final String latestActivity = loginData.getLatestActivity();

        final IChatComponent imsg = new ChatComponentText(ChatUtil.getTagMW())
                .appendSibling(ChatUtil.formattedNameWithReportButton(playername, formattedName))
                .appendText(RED + " is blocking their API.");

        if (latestActivityTime != 0 && latestActivity != null) {
            imsg.appendText(RED + " Latest activity : " + YELLOW + DateUtil.timeSince(latestActivityTime) + GRAY + " ago " + latestActivity + RED + ".");
        }

        if (ScoreboardTracker.isMWEnvironement()) {
            this.appendSelectedClass(imsg, playerData);
        }

        ChatUtil.addChatMessage(imsg);

    }

    private void appendSelectedClass(IChatComponent imsg, HypixelPlayerData playerData) {
        final MegaWallsClassSkinData skinData = new MegaWallsClassSkinData(playerData.getPlayerData());
        imsg.appendText(GREEN + " Selected class : "
                + YELLOW + (skinData.getCurrentmwclass() == null ? "?" : skinData.getCurrentmwclass())
                + GREEN + " with the " + YELLOW + (skinData.getCurrentmwskin() == null ? "?" : skinData.getCurrentmwskin()) + GREEN + " skin.");
    }

}
