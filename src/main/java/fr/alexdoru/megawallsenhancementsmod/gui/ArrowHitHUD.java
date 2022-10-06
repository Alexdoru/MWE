package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GuiNewChatAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.RenderPlayerHook;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowHitHUD extends MyCachedHUD {

    public static ArrowHitHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "20.0";
    private static final Pattern PATTERN_ARROW_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP!");
    private static final Pattern PATTERN_RENEGADE_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern PATTERN_LEAP_HIT = Pattern.compile("^You took ([0-9]*[.]?[0-9]+) recoil damage after traveling \\d+.\\d+ blocks!");
    private static final Pattern PATTERN_LEAP_DIRECT_HIT = Pattern.compile("^You landed a direct hit against (\\w+), taking ([0-9]*[.]?[0-9]+) recoil damage after traveling [0-9]*[.]?[0-9]+ blocks!");
    private static final Pattern PATTERN_REND = Pattern.compile("^Your Rend dealt [0-9]*[.]?[0-9]+ damage to \\w+.*");
    private static final Pattern PATTERN_REND_2 = Pattern.compile("([0-9]*[.]?[0-9]+) damage to (\\w+)");

    private long hittime;

    public ArrowHitHUD() {
        super(ConfigHandler.arrowHitHUDPosition);
        instance = this;
    }

    public boolean processMessage(String msg, String fmsg) {

        final Matcher matcherArrowHit = PATTERN_ARROW_HIT.matcher(msg);

        if (matcherArrowHit.matches()) {
            hittime = System.currentTimeMillis();
            final String playername = matcherArrowHit.group(1);
            final String hitValue = matcherArrowHit.group(2);
            if (isDeadFromShot(playername, hitValue)) {
                displayText = EnumChatFormatting.GOLD + "Kill";
            } else {
                displayText = getColor(hitValue) + hitValue;
            }
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherRenegadeHit = PATTERN_RENEGADE_HIT.matcher(msg);

        if (matcherRenegadeHit.matches()) {
            hittime = System.currentTimeMillis();
            final String playername = matcherRenegadeHit.group(1);
            final String hitValue = matcherRenegadeHit.group(2);
            final String arrowsPinned = matcherRenegadeHit.group(3);
            RenderPlayerHook.addArrowOnPlayer(playername, hittime, Integer.parseInt(arrowsPinned));
            final boolean bool = Float.parseFloat(hitValue) > (Float.parseFloat(arrowsPinned)) * 2.0f;
            displayText = getColor(hitValue) + hitValue + EnumChatFormatting.GRAY + " (" + (bool ? EnumChatFormatting.GREEN : EnumChatFormatting.GOLD) + arrowsPinned + EnumChatFormatting.GRAY + ")";
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherLeapHit = PATTERN_LEAP_HIT.matcher(msg);

        if (matcherLeapHit.matches()) {
            hittime = System.currentTimeMillis() + 1000L;
            displayText = EnumChatFormatting.GREEN + "-" + 2f * Float.parseFloat(matcherLeapHit.group(1));
            ChatUtil.addChatMessage(fmsg);
            return true;
        }

        final Matcher matcherLeapDirectHit = PATTERN_LEAP_DIRECT_HIT.matcher(msg);

        if (matcherLeapDirectHit.matches()) {
            hittime = System.currentTimeMillis() + 1000L;
            final String playername = matcherLeapDirectHit.group(1);
            displayText = EnumChatFormatting.GREEN + "-" + 2f * Float.parseFloat(matcherLeapDirectHit.group(2));
            ChatUtil.addChatMessage(FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg);
            return true;
        }

        final Matcher matcherRend = PATTERN_REND.matcher(msg);

        if (matcherRend.matches()) {
            hittime = System.currentTimeMillis() + 1000L;
            final Matcher matcherRend2 = PATTERN_REND_2.matcher(msg);
            float totalDamage = 0f;
            while (matcherRend2.find()) {
                final float damage = Float.parseFloat(matcherRend2.group(1));
                final String playername = matcherRend2.group(2);
                totalDamage += damage;
                RenderPlayerHook.removeArrowsFrom(playername, (int) (damage / 2));
                fmsg = FKCounterMod.isInMwGame ? fmsg.replaceFirst(playername, NameUtil.getFormattedName(playername)) : fmsg;
            }
            displayText = EnumChatFormatting.GREEN + "-" + totalDamage;
            ChatUtil.addChatMessage(fmsg);
            return true;
        }

        return false;

    }

    private boolean isDeadFromShot(String playername, String hpValue) {
        if (!hpValue.equals("0")) {
            return false;
        }
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        if (chatLines.isEmpty()) {
            return false;
        }
        final ChatLine chatLine = chatLines.get(0);
        final String chatMessage = EnumChatFormatting.getTextWithoutFormattingCodes(chatLine.getChatComponent().getUnformattedText());
        final int updatedCounter = chatLine.getUpdatedCounter();
        final int currentUpdateCounter = mc.ingameGUI.getUpdateCounter();
        final int counterDiff = Math.abs(currentUpdateCounter - updatedCounter);
        return chatMessage.startsWith(playername) && counterDiff <= 4;
    }

    /**
     * Sets the HP color depending on the HP input
     */
    private EnumChatFormatting getColor(String hpValue) {
        final float maxhealth = mc.thePlayer.getMaxHealth();
        final float floathpvalue = Float.parseFloat(hpValue);
        if (floathpvalue > maxhealth) {
            return EnumChatFormatting.DARK_GREEN;
        } else if (floathpvalue > maxhealth * 3 / 4) {
            return EnumChatFormatting.GREEN;
        } else if (floathpvalue > maxhealth / 2) {
            return EnumChatFormatting.YELLOW;
        } else if (floathpvalue > maxhealth / 4) {
            return EnumChatFormatting.RED;
        } else {
            return EnumChatFormatting.DARK_RED;
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1] - frObj.FONT_HEIGHT, 0);
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1] - frObj.FONT_HEIGHT, 0);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandler.show_ArrowHitHUD && System.currentTimeMillis() - hittime < 1000L;
    }

}
