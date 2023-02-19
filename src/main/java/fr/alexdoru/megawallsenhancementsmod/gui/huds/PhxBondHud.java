package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhxBondHud extends MyCachedHUD {

    public static PhxBondHud instance;

    private static final Pattern INCOMING_BOND_PATTERN = Pattern.compile("You were healed by \\w+'s Spirit Bond for (\\d+\\.?\\d*)[\u2764\u2665]\\.");
    private static final Pattern OUTGOING_BOND_PATTERN = Pattern.compile("(\\w+)\\sfor\\s([0-9.]+)[\u2764\u2665]");
    private static final Pattern SELF_HEAL_PATTERN = Pattern.compile("You are healed for\\s+(\\d+(?:\\.\\d+)?)[\u2764\u2665]");


    private static HashMap<String, Float> playersAndHeals = new HashMap<>();
    private static HashMap<String, Float> DUMMYplayersAndHeals = new HashMap<>();

    private long bondtime;

    static {
        DUMMYplayersAndHeals.put("\u00a76[\u00a72S\u00a76] \u00a7a\u00a7lexample1 \u00a77[END]", 3.5f);
        DUMMYplayersAndHeals.put("\u00a7a\u00a7lexample2 \u00a77[SPI]", 1f);
        DUMMYplayersAndHeals.put("\u00a76[\u00a72S\u00a76] \u00a7a\u00a7lexample3 \u00a77[PIR]", 6f);
        DUMMYplayersAndHeals.put("\u00a7a\u00a7lexample4 \u00a77[HUN]", 9.5f);
    }


    public PhxBondHud() {
        super(ConfigHandler.phxBondHUDPosition);
        instance = this;
    }

    public boolean processMessage(String msg) {

        final Matcher matcherOutgoing = OUTGOING_BOND_PATTERN.matcher(msg);
        final Matcher matcherSelf = SELF_HEAL_PATTERN.matcher(msg);

        while (matcherOutgoing.find()) {
            bondtime = System.currentTimeMillis();
            final String playerName = matcherOutgoing.group(1);
            final float heal = Float.parseFloat(matcherOutgoing.group(2));
            if (!"healed".equalsIgnoreCase(playerName)) {
                playersAndHeals.put(playerName, heal);
            }
        }

        if (matcherSelf.find()) {
            bondtime = System.currentTimeMillis();
            final float heal = Float.parseFloat(matcherSelf.group(1));
            playersAndHeals.put("$self", heal);
        }

        final Matcher matcherIncoming = INCOMING_BOND_PATTERN.matcher(msg);

        if (matcherIncoming.find()) {
            bondtime = System.currentTimeMillis();
            final float heal = Float.parseFloat(matcherIncoming.group(1));
            playersAndHeals.put("$self", heal);
            return true;
        }

        return false;
    }

    public void drawLine(String name, float heal, int index, int[] absolutePos) {

        if (name.equals("$self")) {
            name = "\u00a7d\u00a7lYou";
        } else {
            name = NameUtil.getFormattedName(name);
        }

        String lineText = getHealColor(heal) + "" + heal + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + name;
        drawString(frObj, lineText, absolutePos[0], absolutePos[1] + ((index + 1) * frObj.FONT_HEIGHT), 0);

    }


    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        int index = 0;
        Map<String, Float> sortedMap = sortByValue(playersAndHeals);
        for (Map.Entry<String, Float> entry : sortedMap.entrySet()) {
            drawLine(entry.getKey(), entry.getValue(), index, absolutePos);
            index++;
        }
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int index = 0;
        Map<String, Float> sortedMap = sortByValue(DUMMYplayersAndHeals);
        for (Map.Entry<String, Float> entry : sortedMap.entrySet()) {
            drawLine(entry.getKey(), entry.getValue(), index, absolutePos);
            index++;
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        final boolean time = bondtime + 6000L > currentTimeMillis;
        if (!time && !playersAndHeals.isEmpty()) {
            playersAndHeals.clear();
        }
        return ConfigHandler.showPhxBondHUD && time;
    }

    private EnumChatFormatting getHealColor(float heal) {
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



public static HashMap<String, Float> sortByValue(HashMap<String, Float> hm) {
    List<Map.Entry<String, Float>> list = new LinkedList<>(hm.entrySet());

    // sort the entries by value in descending order
    Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

    // create a new LinkedHashMap to hold the sorted entries
    LinkedHashMap<String, Float> sortedMap = new LinkedHashMap<>();

    // loop through the sorted entries and put them into the new map
    for (Map.Entry<String, Float> entry : list) {
        if (entry.getKey().equals("$self")) {
            // add the $self entry to the beginning of the map
            sortedMap.put(entry.getKey(), entry.getValue());
        } else {
            // add all other entries to the end of the map
            sortedMap.put(entry.getKey(), entry.getValue());
        }
    }

    return sortedMap;
}







}
