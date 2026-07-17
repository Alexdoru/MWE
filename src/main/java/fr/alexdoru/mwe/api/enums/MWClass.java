package fr.alexdoru.mwe.api.enums;

import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum MWClass {

    ANGEL("ANG", 147),
    ARCANIST("ARC", 401),
    ASSASSIN("ASN", 95),
    AUTOMATON("ATN", 347),
    BLAZE("BLA", 377),
    COW("COW", 335),
    CREEPER("CRE", 46),
    DRAGON("DRG", 122),
    DREADLORD("DRE", 405),
    ENDERMAN("END", 368),
    GOLEM("GOL", 307),
    HEROBRINE("HBR", 276),
    HUNTER("HUN", 261),
    MOLEMAN("MOL", 284),
    PHOENIX("PHX", 289),
    PIGMAN("PIG", 320),
    PIRATE("PIR", 275),
    RENEGADE("REN", 262),
    SHAMAN("SHA", 116),
    SHARK("SRK", 326),
    SHEEP("SHP", 35),
    SKELETON("SKE", 352),
    SNOWMAN("SNO", 332),
    SPIDER("SPI", 30),
    SQUID("SQU", 351),
    WEREWOLF("WER", 364),
    ZOMBIE("ZOM", 367);

    private static final Map<String, MWClass> FROM_TAG_MAP = new HashMap<>();
    private static final Map<String, MWClass> FROM_NAME_MAP = new HashMap<>();
    private static final Map<Item, MWClass> FROM_ITEM_MAP = new HashMap<>();

    static {
        for (final MWClass value : values()) {
            FROM_TAG_MAP.put(value.TAG.toLowerCase(), value);
            FROM_NAME_MAP.put(value.name().toLowerCase(), value);
            FROM_ITEM_MAP.put(value.selectorItem, value);
        }
    }

    public final String TAG;
    public final String className;
    public final Item selectorItem;

    MWClass(String TAG, int itemId) {
        this.TAG = TAG;
        this.className = StringUtil.uppercaseFirstLetter(this.name().toLowerCase());
        this.selectorItem = Item.getItemById(itemId);
        if (this.selectorItem == null) {
            throw new IllegalArgumentException();
        }
    }

    @Nullable
    public static MWClass fromTag(@NotNull String tag) {
        return FROM_TAG_MAP.get(tag.toLowerCase());
    }

    @Nullable
    public static MWClass fromItem(Item item) {
        return FROM_ITEM_MAP.get(item);
    }

    @Nullable
    public static MWClass fromName(@Nonnull String name) {
        return FROM_NAME_MAP.get(name.toLowerCase());
    }

    @Nullable
    public static MWClass fromTagOrName(@NotNull String s) {
        final MWClass mwClass = MWClass.fromTag(s);
        if (mwClass != null) return mwClass;
        return MWClass.fromName(s);
    }

    @Nullable
    public static MWClass fromTeamTag(@NotNull String teamTag) {
        return MWClass.fromTag(StringUtil.removeFormattingCodes(teamTag).replaceAll("[\\[\\]\\s]", ""));
    }

    /**
     * Returns the MWClass of a player, might be bull
     */
    @Nullable
    public static MWClass ofPlayer(@NotNull String playername) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return null;
        final ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(playername);
        if (team == null) return null;
        return MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? team.getColorPrefix() : team.getColorSuffix());
    }

    /**
     * Returns the MWClass of a player, might be bull
     */
    @Nullable
    public static MWClass ofPlayer(@NotNull UUID uuid) {
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return null;
        final ScorePlayerTeam team = netHandler.getPlayerInfo(uuid).getPlayerTeam();
        if (team == null) return null;
        return MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? team.getColorPrefix() : team.getColorSuffix());
    }

}
