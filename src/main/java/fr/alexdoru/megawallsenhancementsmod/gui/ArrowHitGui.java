package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.RenderPlayerHook;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowHitGui extends MyCachedGui {

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "20.0";
    private static final Pattern PATTERN_ARROW_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP!");
    private static final Pattern PATTERN_RENEGADE_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern PATTERN_LEAP_HIT = Pattern.compile("^You took ([0-9]*[.]?[0-9]+) recoil damage after traveling \\d+.\\d+ blocks!");
    private static final Pattern PATTERN_LEAP_DIRECT_HIT = Pattern.compile("^You landed a direct hit against (\\w+), taking ([0-9]*[.]?[0-9]+) recoil damage after traveling [0-9]*[.]?[0-9]+ blocks!");
    private static final Pattern PATTERN_REND = Pattern.compile("^Your Rend dealt [0-9]*[.]?[0-9]+ damage to \\w+.*");
    private static final Pattern PATTERN_REND2 = Pattern.compile("([0-9]*[.]?[0-9]+) damage to (\\w+)");
    public static ArrowHitGui instance;
    private static String HPvalue;
    private static long hittime;
    private static boolean normalhit = true;
    private static EnumChatFormatting Color;
    private static String arrowspinned;

    public ArrowHitGui() {
        super(ConfigHandler.arrowHitHUDPosition);
        instance = this;
    }

    public static boolean processMessage(String msg, String fmsg) {

        final Matcher matcherArrowHit = PATTERN_ARROW_HIT.matcher(msg);

        if (matcherArrowHit.matches()) {
            HPvalue = matcherArrowHit.group(2);
            if (HPvalue.equals("0")) {
                HPvalue = "Kill";
                Color = EnumChatFormatting.GOLD;
            } else {
                setColor(HPvalue);
            }
            normalhit = true;
            hittime = System.currentTimeMillis();
            instance.updateDisplayText();
            String playername = matcherArrowHit.group(1);
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherRenegadeHit = PATTERN_RENEGADE_HIT.matcher(msg);

        if (matcherRenegadeHit.matches()) {
            String playername = matcherRenegadeHit.group(1);
            HPvalue = matcherRenegadeHit.group(2);
            arrowspinned = matcherRenegadeHit.group(3);
            hittime = System.currentTimeMillis();
            RenderPlayerHook.addArrowOnPlayer(playername, hittime, Integer.parseInt(arrowspinned));
            normalhit = false;
            setColor(HPvalue);
            instance.updateDisplayText();
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherLeapHit = PATTERN_LEAP_HIT.matcher(msg);

        if (matcherLeapHit.matches()) {
            HPvalue = "-" + 2f * Float.parseFloat(matcherLeapHit.group(1));
            hittime = System.currentTimeMillis() + 1000L;
            normalhit = true;
            Color = EnumChatFormatting.GREEN;
            instance.updateDisplayText();
            ChatUtil.addChatMessage(fmsg);
            return true;
        }

        final Matcher matcherLeapDirectHit = PATTERN_LEAP_DIRECT_HIT.matcher(msg);

        if (matcherLeapDirectHit.matches()) {
            HPvalue = "-" + 2f * Float.parseFloat(matcherLeapDirectHit.group(2));
            hittime = System.currentTimeMillis() + 1000L;
            normalhit = true;
            Color = EnumChatFormatting.GREEN;
            instance.updateDisplayText();
            String playername = matcherLeapDirectHit.group(1);
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherRend = PATTERN_REND.matcher(msg);

        if (matcherRend.matches()) {
            final Matcher matcherRend2 = PATTERN_REND2.matcher(msg);
            float totalDamage = 0f;
            while (matcherRend2.find()) {
                final float damage = Float.parseFloat(matcherRend2.group(1));
                final String playername = matcherRend2.group(2);
                totalDamage += damage;
                RenderPlayerHook.removeArrowsFrom(playername, (int) (damage / 2));
                fmsg = FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg;
            }
            HPvalue = "-" + totalDamage;
            hittime = System.currentTimeMillis() + 1000L;
            normalhit = true;
            Color = EnumChatFormatting.GREEN;
            instance.updateDisplayText();
            ChatUtil.addChatMessage(fmsg);
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
    public void render(ScaledResolution resolution) {
        int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1] - frObj.FONT_HEIGHT, 0);
    }

    @Override
    public void renderDummy() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1] - frObj.FONT_HEIGHT, 0);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandler.show_ArrowHitHUD && System.currentTimeMillis() - hittime < 1000L;
    }

}
