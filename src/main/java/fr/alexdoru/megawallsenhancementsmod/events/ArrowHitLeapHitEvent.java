package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowHitLeapHitEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String GOLD = "FFAA00";
    private static final String GREEN = "55FF55";
    private static final String DARK_GREEN = "00AA00";
    private static final String YELLOW = "FFFF55";
    private static final String RED = "FF5555";
    private static final String DARK_RED = "AA0000";
    private static String HPvalue;
    private static long lasthittime;
    private static long renhittime;
    private static String Color;
    private static int arrowspinned;

    private static final Pattern HIT_PATTERN0 = Pattern.compile("^\\w+ is on 0 HP!");
    private static final Pattern HIT_PATTERN1 = Pattern.compile("^\\w+ is on (\\d+) HP!");
    private static final Pattern HIT_PATTERN2 = Pattern.compile("^\\w+ is on (\\d+\\.\\d+) HP!");
    private static final Pattern HIT_PATTERN3 = Pattern.compile("^\\w+ is on (\\d+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern HIT_PATTERN4 = Pattern.compile("^\\w+ is on (\\d+\\.\\d+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern HIT_PATTERN5 = Pattern.compile("^You took (\\d+\\.\\d+) recoil damage after traveling \\d+.\\d+ blocks!");
    private static final Pattern HIT_PATTERN6 = Pattern.compile("^You landed a direct hit against \\w+, taking (\\d+\\.\\d+) recoil damage after traveling \\d+\\.\\d+ blocks!");

    public static boolean processMessage(String rawMessage) {

        Matcher hitMatcher0 = HIT_PATTERN0.matcher(rawMessage);
        Matcher hitMatcher1 = HIT_PATTERN1.matcher(rawMessage);
        Matcher hitMatcher2 = HIT_PATTERN2.matcher(rawMessage);
        Matcher hitMatcher3 = HIT_PATTERN3.matcher(rawMessage);// renegade
        Matcher hitMatcher4 = HIT_PATTERN4.matcher(rawMessage);// renegade
        Matcher HitLeapMatcher = HIT_PATTERN5.matcher(rawMessage);//leap
        Matcher DirectHitLeapMatcher = HIT_PATTERN6.matcher(rawMessage);//leap

        if (hitMatcher0.matches()) {

            HPvalue = "Kill";
            lasthittime = System.currentTimeMillis();
            Color = GOLD;
            return true;

        } else if (hitMatcher1.matches()) {

            HPvalue = hitMatcher1.group(1);
            lasthittime = System.currentTimeMillis();
            setColor(HPvalue);
            return true;

        } else if (hitMatcher2.matches()) {

            HPvalue = hitMatcher2.group(1);
            lasthittime = System.currentTimeMillis();
            setColor(HPvalue);
            return true;

        } else if (hitMatcher3.matches()) { // renegade hits

            HPvalue = hitMatcher3.group(1);
            arrowspinned = Integer.parseInt(hitMatcher3.group(2));
            renhittime = System.currentTimeMillis();
            setColor(HPvalue);
            return true;

        } else if (hitMatcher4.matches()) { // renegade hits

            HPvalue = hitMatcher4.group(1);
            arrowspinned = Integer.parseInt(hitMatcher4.group(2));
            renhittime = System.currentTimeMillis();
            setColor(HPvalue);
            return true;

        } else if (HitLeapMatcher.matches()) {

            HPvalue = "-" + 2f * Float.parseFloat(HitLeapMatcher.group(1));
            lasthittime = System.currentTimeMillis() + 1000L;
            Color = GREEN;
            return true;

        } else if (DirectHitLeapMatcher.matches()) {

            HPvalue = "-" + 2f * Float.parseFloat(DirectHitLeapMatcher.group(1));
            lasthittime = System.currentTimeMillis() + 1000L;
            Color = GREEN;
            return true;

        }

        return false;

    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {

        if (event.type != ElementType.EXPERIENCE) {
            return;
        }

        long time = System.currentTimeMillis();

        if (time - lasthittime < 1000L) {

            new ArrowHitGui(mc, HPvalue, Color);

        } else if (time - renhittime < 1000L) {

            new ArrowHitGui(mc, HPvalue, Color, arrowspinned);

        }

    }

    /**
     * Sets the HP color depending on the HP input
     */
    private static void setColor(String hpvalue) {

        float maxhealth = mc.thePlayer.getMaxHealth();
        float floathpvalue = Float.parseFloat(hpvalue);

        if (floathpvalue > maxhealth) {
            Color = DARK_GREEN;
        } else if (floathpvalue > maxhealth * 3 / 4) {
            Color = GREEN;
        } else if (floathpvalue > maxhealth / 2) {
            Color = YELLOW;
        } else if (floathpvalue > maxhealth / 4) {
            Color = RED;
        } else {
            Color = DARK_RED;
        }

    }

}
