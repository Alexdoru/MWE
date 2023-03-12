package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.MapUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhxBondHud extends MyCachedHUD {

    public static PhxBondHud instance;

    private static final Pattern INCOMING_BOND_PATTERN = Pattern.compile("^You were healed by (\\w{1,16})'s Spirit Bond for ([0-9]*[.]?[0-9]+)[\u2764\u2665]");
    private static final Pattern BOND_USED_PATTERN = Pattern.compile("^Your Spirit Bond healed");
    private static final Pattern PLAYERS_HEALED_PATTERN = Pattern.compile("(\\w{1,16})\\sfor\\s([0-9]*[.]?[0-9]+)[\u2764\u2665]");
    private static final Pattern SELF_HEALED_PATTERN = Pattern.compile("You are healed for\\s([0-9]*[.]?[0-9]+)[\u2764\u2665]");

    private static final List<String> dummyTextToRender = Arrays.asList(
            getHealColor(9.5f) + "9.5" + EnumChatFormatting.RED + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + EnumChatFormatting.GREEN + "Player1" + EnumChatFormatting.GRAY + " [HUN]",
            getHealColor(6f) + "6.0" + EnumChatFormatting.RED + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + EnumChatFormatting.GREEN + "Player2" + EnumChatFormatting.GRAY + " [PIR]",
            getHealColor(3.5f) + "3.5" + EnumChatFormatting.RED + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + EnumChatFormatting.GREEN + "Player3" + EnumChatFormatting.GRAY + " [END]",
            getHealColor(1f) + "1.0" + EnumChatFormatting.RED + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + EnumChatFormatting.GREEN + "Player4" + EnumChatFormatting.GRAY + " [SPI]");

    private final List<String> textToRender = new ArrayList<>();
    private long timeStartRender;

    public PhxBondHud() {
        super(ConfigHandler.phxBondHUDPosition);
        instance = this;
    }

    public boolean processMessage(String msg) {

        if (!FKCounterMod.isInMwGame) {
            return false;
        }

        final Matcher incomingBondMatcher = INCOMING_BOND_PATTERN.matcher(msg);

        if (incomingBondMatcher.find()) {
            textToRender.clear();
            timeStartRender = System.currentTimeMillis();
            final String playerHealingYou = incomingBondMatcher.group(1);
            final String amountHealed = incomingBondMatcher.group(2);
            textToRender.add(getLine(playerHealingYou, amountHealed));
            return true;
        }

        if (BOND_USED_PATTERN.matcher(msg).find()) {
            textToRender.clear();
            timeStartRender = System.currentTimeMillis();
            final Matcher selfHealedMatcher = SELF_HEALED_PATTERN.matcher(msg);
            if (selfHealedMatcher.find()) {
                final String amountHealed = selfHealedMatcher.group(1);
                textToRender.add(getLine(null, amountHealed));
                // to avoid matching "healed for x.x" as if "healed" was a playername
                msg = msg.replace(selfHealedMatcher.group(), "");
            }
            final Matcher playerHealedMatcher = PLAYERS_HEALED_PATTERN.matcher(msg);
            final Map<String, Float> map = new HashMap<>();
            while (playerHealedMatcher.find()) {
                final String playerName = playerHealedMatcher.group(1);
                final String amountHealed = playerHealedMatcher.group(2);
                map.put(playerName, Float.parseFloat(amountHealed));
            }
            final Map<String, Float> sortedMap = MapUtil.sortByDecreasingValue(map);
            for (final Map.Entry<String, Float> entry : sortedMap.entrySet()) {
                this.textToRender.add(getLine(entry.getKey(), entry.getValue().toString()));
            }
            return true;
        }

        return false;

    }

    private String getLine(String playername, String amountHealed) {
        final String formattedName;
        if (playername == null) { // myself
            formattedName = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "You";
        } else {
            formattedName = NameUtil.getFormattedNameWithoutIcons(playername);
        }
        return getHealColor(Float.parseFloat(amountHealed)) + amountHealed + EnumChatFormatting.RED + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + formattedName;
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition();
        drawStringList(textToRender, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), true, true);
    }

    @Override
    public void renderDummy() {
        drawStringList(dummyTextToRender, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), true, true);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showPhxBondHUD && timeStartRender + 5000L > currentTimeMillis;
    }

    private static EnumChatFormatting getHealColor(float heal) {
        if (heal > 7.5) {
            return EnumChatFormatting.GREEN;
        } else if (heal > 4.5) {
            return EnumChatFormatting.YELLOW;
        } else if (heal > 2) {
            return EnumChatFormatting.GOLD;
        } else {
            return EnumChatFormatting.RED;
        }
    }

}
