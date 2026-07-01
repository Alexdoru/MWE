package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.events.KillCounterEvent;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.gui.MWERenderers;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.MapUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FinalKillCounter {

    private static final Pattern MESSAGE_START_PATTERN = Pattern.compile("^(\\w{1,16}) ");
    private static final String[] KILL_MESSAGES = {
            /*Banana messages, put those messages at the top to not conflict with the other pattern (\w{1,16}) was killed by (\w{1,16})*/
            "^(\\w{1,16}) got banana pistol'd by (\\w{1,16})",
            "^(\\w{1,16}) was peeled by (\\w{1,16})",
            "^(\\w{1,16}) was mushed by (\\w{1,16})",
            "^(\\w{1,16}) was hit by a banana split from (\\w{1,16})",
            "^(\\w{1,16}) was killed by an explosive banana from (\\w{1,16})",
            "^(\\w{1,16}) was killed by a magic banana from (\\w{1,16})",
            "^(\\w{1,16}) was turned into mush by (\\w{1,16})",
            /*Default messages*/
            "^(\\w{1,16}) was shot and killed by (\\w{1,16})",
            "^(\\w{1,16}) was snowballed to death by (\\w{1,16})",
            "^(\\w{1,16}) was killed by (\\w{1,16})",
            "^(\\w{1,16}) was killed with a potion by (\\w{1,16})",
            "^(\\w{1,16}) was killed with an explosion by (\\w{1,16})",
            "^(\\w{1,16}) was killed with magic by (\\w{1,16})",
            /*Digital messages*/
            "^(\\w{1,16}) was blocked by (\\w{1,16})",
            "^(\\w{1,16}) was put into cold storage by (\\w{1,16})",
            "^(\\w{1,16}) was deleted by (\\w{1,16})",
            "^(\\w{1,16}) was purged by an antivirus owned by (\\w{1,16})",
            "^(\\w{1,16}) accidentally closed the game while fighting (\\w{1,16})",
            "^(\\w{1,16}) had their computer switched off by (\\w{1,16})",
            "^(\\w{1,16}) had their computer fried by (\\w{1,16})",
            /*Western messages*/
            "^(\\w{1,16}) was filled full of lead by (\\w{1,16})",
            "^(\\w{1,16}) was iced by (\\w{1,16})",
            "^(\\w{1,16}) met their end by (\\w{1,16})",
            "^(\\w{1,16}) lost a drinking contest with (\\w{1,16})",
            "^(\\w{1,16}) was killed with dynamite by (\\w{1,16})",
            "^(\\w{1,16}) lost the draw to (\\w{1,16})",
            /*Fire messages*/
            "^(\\w{1,16}) was struck down by (\\w{1,16})",
            "^(\\w{1,16}) was turned to dust by (\\w{1,16})",
            "^(\\w{1,16}) was turned to ash by (\\w{1,16})",
            "^(\\w{1,16}) was melted by (\\w{1,16})",
            "^(\\w{1,16}) was incinerated by (\\w{1,16})",
            "^(\\w{1,16}) was vaporized by (\\w{1,16})",
            /*Love messages*/
            "^(\\w{1,16}) was struck with Cupid's arrow by (\\w{1,16})",
            "^(\\w{1,16}) was given the cold shoulder by (\\w{1,16})",
            "^(\\w{1,16}) was hugged too hard by (\\w{1,16})",
            "^(\\w{1,16}) drank a love potion from (\\w{1,16})",
            "^(\\w{1,16}) was hit by a love bomb from (\\w{1,16})",
            "^(\\w{1,16}) was no match for (\\w{1,16})",
            /*Paladin messages*/
            "^(\\w{1,16}) was smote from afar by (\\w{1,16})",
            "^(\\w{1,16}) was justly ended by (\\w{1,16})",
            "^(\\w{1,16}) was purified by (\\w{1,16})",
            "^(\\w{1,16}) was killed with holy water by (\\w{1,16})",
            "^(\\w{1,16}) was dealt vengeful justice by (\\w{1,16})",
            "^(\\w{1,16}) was returned to dust by (\\w{1,16})",
            /*Pirate messages*/
            "^(\\w{1,16}) be shot and killed by (\\w{1,16})",
            "^(\\w{1,16}) be snowballed to death by (\\w{1,16})",
            "^(\\w{1,16}) be sent to Davy Jones' locker by (\\w{1,16})",
            "^(\\w{1,16}) be killed with rum by (\\w{1,16})",
            "^(\\w{1,16}) be shot with cannon by (\\w{1,16})",
            "^(\\w{1,16}) be killed with magic by (\\w{1,16})",
            /*BBQ messages*/
            "^(\\w{1,16}) was glazed in BBQ sauce by (\\w{1,16})",
            "^(\\w{1,16}) was sprinkled with chilli powder by (\\w{1,16})",
            "^(\\w{1,16}) was sliced up by (\\w{1,16})",
            "^(\\w{1,16}) was overcooked by (\\w{1,16})",
            "^(\\w{1,16}) was deep fried by (\\w{1,16})",
            "^(\\w{1,16}) was boiled by (\\w{1,16})",
            /*Squeak messages*/
            "^(\\w{1,16}) was squeaked from a distance by (\\w{1,16})",
            "^(\\w{1,16}) was hit by frozen cheese from (\\w{1,16})",
            "^(\\w{1,16}) was chewed up by (\\w{1,16})",
            "^(\\w{1,16}) was chemically cheesed by (\\w{1,16})",
            "^(\\w{1,16}) was turned into cheese whiz by (\\w{1,16})",
            "^(\\w{1,16}) was magically squeaked by (\\w{1,16})",
            /*Bunny messages*/
            "^(\\w{1,16}) was hit by a flying bunny by (\\w{1,16})",
            "^(\\w{1,16}) was hit by a bunny thrown by (\\w{1,16})",
            "^(\\w{1,16}) was turned into a carrot by (\\w{1,16})",
            "^(\\w{1,16}) was hit by a carrot from (\\w{1,16})",
            "^(\\w{1,16}) was bitten by a bunny from (\\w{1,16})",
            "^(\\w{1,16}) was magically turned into a bunny by (\\w{1,16})",
            "^(\\w{1,16}) was fed to a bunny by (\\w{1,16})",
            /*Natural deaths messages*/
            "^(\\w{1,16}) starved to death\\.",
            "^(\\w{1,16}) hit the ground too hard\\.",
            "^(\\w{1,16}) blew up\\.",
            "^(\\w{1,16}) exploded\\.",
            "^(\\w{1,16}) tried to swim in lava\\.",
            "^(\\w{1,16}) went up in flames\\.",
            "^(\\w{1,16}) burned to death\\.",
            "^(\\w{1,16}) suffocated in a wall\\.",
            "^(\\w{1,16}) suffocated\\.",
            "^(\\w{1,16}) fell out of the world\\.",
            "^(\\w{1,16}) had a block fall on them\\.",
            "^(\\w{1,16}) drowned\\.",
            "^(\\w{1,16}) died from a cactus\\."
    };
    private static final Pattern[] KILL_PATTERNS;
    private static final Map<MWTeam, Character> DEFAULT_PREFIXES = new EnumMap<>(MWTeam.class);

    @NotNull
    private final String serverID;
    private final Map<MWTeam, Character> COLOR_PREFIXES;
    private final Map<MWTeam, Map<String, Integer>> KILLS_MAP = new EnumMap<>(MWTeam.class);
    private final Map<String, Integer> allPlayerKills = new HashMap<>();
    private final Set<String> deadPlayers = new HashSet<>();
    /** Used to save the names of players present in the game for tab completion & reporting suggestions */
    private final Set<String> playersPresentInGame = new HashSet<>();

    static {
        KILL_PATTERNS = new Pattern[KILL_MESSAGES.length];
        for (int i = 0; i < KILL_MESSAGES.length; i++) {
            KILL_PATTERNS[i] = Pattern.compile(KILL_MESSAGES[i]);
        }
        DEFAULT_PREFIXES.put(MWTeam.BLUE, '9');
        DEFAULT_PREFIXES.put(MWTeam.GREEN, 'a');
        DEFAULT_PREFIXES.put(MWTeam.RED, 'c');
        DEFAULT_PREFIXES.put(MWTeam.YELLOW, 'e');
    }

    FinalKillCounter(@NotNull String serverID) {
        this.serverID = serverID;
        this.COLOR_PREFIXES = new EnumMap<>(DEFAULT_PREFIXES);
        for (final MWTeam team : MWTeam.values()) {
            KILLS_MAP.put(team, new HashMap<>());
        }
        Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().forEach(netInfo -> {
            if (netInfo instanceof NetworkPlayerInfoAccessor) {
                ((NetworkPlayerInfoAccessor) netInfo).setFinalKills(0);
            }
        });
        MWERenderers.fkCounterHUD.updateDisplayText();
    }

    public boolean processMessage(ClientChatReceivedEvent event, String formattedText, String unformattedText) {

        if (!MESSAGE_START_PATTERN.matcher(unformattedText).find()) {
            return false;
        }

        for (final Pattern pattern : KILL_PATTERNS) {

            final Matcher matcher = pattern.matcher(unformattedText);

            if (matcher.find()) {

                // "player1 killed by player 2" format
                if (matcher.groupCount() == 2) {
                    final String victim = matcher.group(1);
                    final String killer = matcher.group(2);
                    final char victimTeamColor = StringUtil.getLastColorCharBefore(formattedText, victim);
                    // need to replace first in case the name of the killer contains the name of the killed player
                    final char killerTeamColor = StringUtil.getLastColorCharBefore(formattedText.replaceFirst(victim, ""), killer);
                    final MWTeam victimTeam = getTeamFromColor(victimTeamColor);
                    final MWTeam killerTeam = getTeamFromColor(killerTeamColor);
                    int killsOfVictim = 0;
                    if (victimTeam != null && killerTeam != null) {
                        killsOfVictim = tryRemoveKilledPlayer(victim, victimTeam);
                        final boolean victimIsFinalKill = killsOfVictim != -1;
                        if (victimIsFinalKill) {
                            playersPresentInGame.add(victim);
                            playersPresentInGame.add(killer);
                            if (tryAddKill(killer, killerTeam)) {
                                MinecraftForge.EVENT_BUS.post(new KillCounterEvent.FinalKill(
                                        victim,
                                        victimTeam,
                                        killer,
                                        killerTeam
                                ));
                            }
                            MWERenderers.fkCounterHUD.updateDisplayText();
                        } else {
                            MinecraftForge.EVENT_BUS.post(new KillCounterEvent.NormalKill(
                                    victim,
                                    victimTeam,
                                    killer,
                                    killerTeam
                            ));
                        }
                    }
                    final String s = formattedText.replace(killer, SquadHandler.getSquadname(killer))
                            .replace(victim, SquadHandler.getSquadname(victim))
                            + getKillDiffString(killsOfVictim, victimTeamColor);
                    event.message = new ChatComponentText(s);
                    ChatUtil.addSkinToComponent(event.message, victim);
                    return true;
                }

                // "player1 died on his own" format
                if (matcher.groupCount() == 1) {
                    final String victim = matcher.group(1);
                    final char victimTeamColor = StringUtil.getLastColorCharBefore(formattedText, victim);
                    final MWTeam victimTeam = getTeamFromColor(victimTeamColor);
                    int killsOfVictim = 0;
                    if (victimTeam != null) {
                        killsOfVictim = tryRemoveKilledPlayer(victim, victimTeam);
                        final boolean victimIsFinalKill = killsOfVictim != -1;
                        if (victimIsFinalKill) {
                            playersPresentInGame.add(victim);
                            MinecraftForge.EVENT_BUS.post(new KillCounterEvent.FinalDeath(
                                    victim,
                                    victimTeam
                            ));
                            MWERenderers.fkCounterHUD.updateDisplayText();
                        } else {
                            MinecraftForge.EVENT_BUS.post(new KillCounterEvent.NormalDeath(
                                    victim,
                                    victimTeam
                            ));
                        }
                    }
                    final String s = formattedText.replace(victim, SquadHandler.getSquadname(victim))
                            + getKillDiffString(killsOfVictim, victimTeamColor);
                    event.message = new ChatComponentText(s);
                    ChatUtil.addSkinToComponent(event.message, victim);
                    return true;
                }

            }

        }

        return false;

    }

    private static String getKillDiffString(int killsOfVictim, char victimTeamColor) {
        if (!MWEConfig.showKillDiffInChat || killsOfVictim < 1) {
            return "";
        }
        return EnumChatFormatting.WHITE + " (" + '§' + victimTeamColor + "-" + killsOfVictim + EnumChatFormatting.WHITE + ")";
    }

    @NotNull
    public String getServerID() {
        return serverID;
    }

    public int getKillsOfTeam(MWTeam team) {
        int teamKills = 0;
        for (final int kills : KILLS_MAP.get(team).values()) {
            teamKills += kills;
        }
        return teamKills;
    }

    @NotNull
    public Map<String, Integer> getKillMapOfTeam(MWTeam team) {
        return Collections.unmodifiableMap(KILLS_MAP.get(team));
    }

    /**
     * Returns a sorted list of entries where the Keys are the Team, and the values are the amounts of finals for that team
     */
    public List<Map.Entry<MWTeam, Integer>> getSortedTeamKillsList() {
        final Map<MWTeam, Integer> map = new EnumMap<>(MWTeam.class);
        map.put(MWTeam.BLUE, getKillsOfTeam(MWTeam.BLUE));
        map.put(MWTeam.GREEN, getKillsOfTeam(MWTeam.GREEN));
        map.put(MWTeam.RED, getKillsOfTeam(MWTeam.RED));
        map.put(MWTeam.YELLOW, getKillsOfTeam(MWTeam.YELLOW));
        return MapUtil.sortByValueReversed(map);
    }

    /**
     * Detects the color codes you are using in your mega walls settings by looking at the scoreboard/sidebartext
     */
    void setTeamPrefixes() {
        final String[] WITHER_PREFIXES = {"[B]", "[G]", "[R]", "[Y]"};
        final Map<String, MWTeam> prefixToTeam = new HashMap<>();
        prefixToTeam.put(WITHER_PREFIXES[0], MWTeam.BLUE);
        prefixToTeam.put(WITHER_PREFIXES[1], MWTeam.GREEN);
        prefixToTeam.put(WITHER_PREFIXES[2], MWTeam.RED);
        prefixToTeam.put(WITHER_PREFIXES[3], MWTeam.YELLOW);
        for (final String line : ScoreboardUtils.getFormattedSidebarText()) {
            for (final String prefix : WITHER_PREFIXES) {
                if (line.contains(prefix)) {
                    final MWTeam team = prefixToTeam.get(prefix);
                    final char color = StringUtil.getLastColorCharBefore(line, prefix);
                    COLOR_PREFIXES.put(team, color);
                }
            }
        }
        MWERenderers.fkCounterHUD.updateDisplayText();
    }

    /**
     * Tries to remove a player from the fkcounter if the player has no wither
     * and is not already killed, and returns the amount of finals the
     * player had when successfull, returns -1 otherwise.
     */
    public int tryRemoveKilledPlayer(String player, @NotNull MWTeam team) {
        if (!ScoreboardTracker.getParser().isWitherAlive(String.valueOf(COLOR_PREFIXES.get(team)))) {
            final Integer kills = KILLS_MAP.get(team).remove(player);
            allPlayerKills.remove(player);
            deadPlayers.add(player);
            updateNetworkPlayerinfo(player, 0);
            return kills == null ? 0 : kills;
        }
        return -1;
    }

    /**
     * Returns true if the kill was added
     */
    private boolean tryAddKill(String killer, @NotNull MWTeam killerTeam) {
        if (deadPlayers.contains(killer)) {
            return false;
        }
        final Integer finals = KILLS_MAP.get(killerTeam).merge(killer, 1, Integer::sum);
        allPlayerKills.merge(killer, 1, Integer::sum);
        updateNetworkPlayerinfo(killer, finals);
        return true;
    }

    public int getKillsOfPlayer(String playername) {
        final Integer finals = allPlayerKills.get(playername);
        return finals == null ? 0 : finals;
    }

    private static void updateNetworkPlayerinfo(String playername, int finals) {
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
        if (netInfo instanceof NetworkPlayerInfoAccessor) {
            ((NetworkPlayerInfoAccessor) netInfo).setFinalKills(finals);
        }
    }

    @Nullable
    private MWTeam getTeamFromColor(char color) {
        for (final Map.Entry<MWTeam, Character> entry : COLOR_PREFIXES.entrySet()) {
            if (entry.getValue() == color) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getDefaultColorPrefix(MWTeam team) {
        return "§" + DEFAULT_PREFIXES.get(team);
    }

    public String getColorPrefixOfTeam(MWTeam team) {
        return "§" + COLOR_PREFIXES.get(team);
    }

    /**
     * Used by the report suggestion system
     */
    public boolean wasPlayerInThisGame(String playername) {
        for (final String name : playersPresentInGame) {
            if (name.equalsIgnoreCase(playername)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getPlayersInThisGame() {
        return new ArrayList<>(playersPresentInGame);
    }

}
