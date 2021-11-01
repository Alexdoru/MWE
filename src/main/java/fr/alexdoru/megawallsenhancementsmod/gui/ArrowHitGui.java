package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowHitGui extends MyCachedGui {

    public static ArrowHitGui instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "20.0";
    private static String HPvalue;
    private static long hittime;
    private static boolean normalhit = true;
    private static EnumChatFormatting Color;
    private static String arrowspinned;

    private static final Pattern HIT_PATTERN0 = Pattern.compile("^\\w+ is on 0 HP!");
    private static final Pattern HIT_PATTERN1 = Pattern.compile("^\\w+ is on (\\d+) HP!"); // TODO utiliser de la meilleur regex
    private static final Pattern HIT_PATTERN2 = Pattern.compile("^\\w+ is on (\\d+\\.\\d+) HP!");
    private static final Pattern HIT_PATTERN3 = Pattern.compile("^\\w+ is on (\\d+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern HIT_PATTERN4 = Pattern.compile("^\\w+ is on (\\d+\\.\\d+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern HIT_PATTERN5 = Pattern.compile("^You took (\\d+\\.\\d+) recoil damage after traveling \\d+.\\d+ blocks!");
    private static final Pattern HIT_PATTERN6 = Pattern.compile("^You landed a direct hit against \\w+, taking (\\d+\\.\\d+) recoil damage after traveling \\d+\\.\\d+ blocks!");

    public ArrowHitGui() {
        instance = this;
        guiPosition = MWEnConfigHandler.arrowHitHUDPosition;
    }

    @Override
    public void updateDisplayText() {
        if (normalhit) {
            displayText = Color + HPvalue;
        } else {
            boolean bool = Float.parseFloat(HPvalue) > (Float.parseFloat(arrowspinned)) * 2.0f;
            displayText = Color + HPvalue + EnumChatFormatting.GRAY + " (" + (bool ? EnumChatFormatting.GREEN : EnumChatFormatting.GOLD) + arrowspinned + EnumChatFormatting.GRAY + ")";
        }
    }

    @Override
    public void render() {
        super.render();
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        drawCenteredString(frObj, displayText, x, y - 4, 0);
    }

    @Override
    public void renderDummy() {
        super.renderDummy();
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        drawCenteredString(frObj, DUMMY_TEXT, x, y - 4, 0);
    }

    @Override
    public boolean isEnabled() {
        return System.currentTimeMillis() - hittime < 1000L;
    }

    @Override
    public void save() {
        MWEnConfigHandler.saveConfig();
    }

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
            hittime = System.currentTimeMillis();
            normalhit = true;
            Color = EnumChatFormatting.GOLD;
            instance.updateDisplayText();
            return true;

        } else if (hitMatcher1.matches()) {

            HPvalue = hitMatcher1.group(1);
            hittime = System.currentTimeMillis();
            normalhit = true;
            setColor(HPvalue);
            instance.updateDisplayText();
            return true;

        } else if (hitMatcher2.matches()) {

            HPvalue = hitMatcher2.group(1);
            hittime = System.currentTimeMillis();
            normalhit = true;
            setColor(HPvalue);
            instance.updateDisplayText();
            return true;

        } else if (hitMatcher3.matches()) { // renegade hits

            HPvalue = hitMatcher3.group(1);
            arrowspinned = hitMatcher3.group(2);
            hittime = System.currentTimeMillis();
            normalhit = false;
            setColor(HPvalue);
            instance.updateDisplayText();
            return true;

        } else if (hitMatcher4.matches()) { // renegade hits

            HPvalue = hitMatcher4.group(1);
            arrowspinned = hitMatcher4.group(2);
            hittime = System.currentTimeMillis();
            normalhit = false;
            setColor(HPvalue);
            instance.updateDisplayText();
            return true;

        } else if (HitLeapMatcher.matches()) {

            HPvalue = "-" + 2f * Float.parseFloat(HitLeapMatcher.group(1));
            hittime = System.currentTimeMillis() + 1000L;
            normalhit = true;
            Color = EnumChatFormatting.GREEN;
            instance.updateDisplayText();
            return true;

        } else if (DirectHitLeapMatcher.matches()) {

            HPvalue = "-" + 2f * Float.parseFloat(DirectHitLeapMatcher.group(1));
            hittime = System.currentTimeMillis() + 1000L;
            normalhit = true;
            Color = EnumChatFormatting.GREEN;
            instance.updateDisplayText();
            return true;

        }

        return false;

    }

    /**
     * Sets the HP color depending on the HP input
     */
    private static void setColor(String hpvalue) {

        float maxhealth = mc.thePlayer.getMaxHealth();
        float floathpvalue = Float.parseFloat(hpvalue);

        if (floathpvalue > maxhealth) {
            Color = EnumChatFormatting.DARK_GREEN;
        } else if (floathpvalue > maxhealth * 3 / 4) {
            Color = EnumChatFormatting.GREEN;
        } else if (floathpvalue > maxhealth / 2) {
            Color = EnumChatFormatting.YELLOW;
        } else if (floathpvalue > maxhealth / 4) {
            Color = EnumChatFormatting.RED;
        } else {
            Color = EnumChatFormatting.DARK_RED;
        }

    }

}
