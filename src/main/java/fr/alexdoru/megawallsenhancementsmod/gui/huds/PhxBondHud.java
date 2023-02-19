package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class PhxBondHud extends MyCachedHUD {

    public static PhxBondHud instance;

    private static HashMap<String, Float> playersAndHeals = new HashMap<>();
    private static HashMap<String, Float> DUMMYplayersAndHeals = new HashMap<>();

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

    public void drawLine(String name, float heal, int index, int[] absolutePos) {



        String lineText = getHealColor(heal) + "" + heal + "\u2764" + EnumChatFormatting.DARK_GRAY + " - " + name;
        drawString(frObj, lineText, absolutePos[0], absolutePos[1] + ((index + 1) * frObj.FONT_HEIGHT), 0);

    }


    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);

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
        return ConfigHandler.showPhxBondHUD;
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
        List<Map.Entry<String, Float> > list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        HashMap<String, Float> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }







}
