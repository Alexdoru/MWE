package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.EnumChatFormatting.*;

public class StrengthHUD extends AbstractRenderer {

    private static final Pattern DREADLORD_STRENGTH_PATTERN = Pattern.compile("§4§lSOUL SIPHON §c§l85% ([0-9])s");
    private static final Pattern HEROBRINE_STRENGTH_PATTERN = Pattern.compile("§e§lPOWER §c§l85% ([0-9])s");
    private static final Pattern HUNTER_PRE_STRENGTH_PATTERN = Pattern.compile("§a§lF\\.O\\.N\\. §7\\(§l§c§lStrength§7\\) §e§l([0-9]+)");
    private static final Pattern ZOMBIE_STRENGTH_PATTERN = Pattern.compile("§2§lBERSERK §c§l75% ([0-9])s");
    private static final String DUMMY_TEXT = GRAY + "(" + RED + BOLD + "Strength" + GRAY + ")" + YELLOW + BOLD + " in 10";
    private static final String PRE_STRENGTH_TEXT = GRAY + "(" + RED + BOLD + "Strength" + GRAY + ")" + YELLOW + BOLD + " in ";
    private static final String STRENGTH_TEXT = RED.toString() + BOLD + "Strength " + YELLOW + BOLD;
    private long timeStartRender;
    private long renderDuration;
    private boolean isStrengthRender;
    private final TimerUtil timerStrength = new TimerUtil(11000L);

    public StrengthHUD() {
        super(MWEConfig.strengthHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition(resolution);
        final int timeLeft = (int) ((timeStartRender + renderDuration - System.currentTimeMillis()) / 1000L);
        final String displayText = (isStrengthRender ? STRENGTH_TEXT : PRE_STRENGTH_TEXT) + timeLeft;
        drawCenteredString(mc.fontRendererObj, displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        drawCenteredString(mc.fontRendererObj, DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return timeStartRender + renderDuration - currentTimeMillis > 0;
    }

    public boolean processMessage(String fmsg) {
        if (!MWEConfig.showStrengthHUD) return false;
        final Matcher dreadStrenghtMatcher = DREADLORD_STRENGTH_PATTERN.matcher(fmsg);
        if (dreadStrenghtMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(dreadStrenghtMatcher.group(1)) * 1000L);
            return true;
        }
        final Matcher preStrengthMatcher = HUNTER_PRE_STRENGTH_PATTERN.matcher(fmsg);
        if (preStrengthMatcher.find()) {
            if (timerStrength.update()) {
                SoundUtil.playStrengthSound();
            }
            final String preStrengthTimer = preStrengthMatcher.group(1);
            this.setPreStrengthTime(preStrengthTimer);
            return true;
        }
        final Matcher herobrineStrenghtMatcher = HEROBRINE_STRENGTH_PATTERN.matcher(fmsg);
        if (herobrineStrenghtMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(herobrineStrenghtMatcher.group(1)) * 1000L);
            return true;
        }
        final Matcher zombieStrenghtMatcher = ZOMBIE_STRENGTH_PATTERN.matcher(fmsg);
        if (zombieStrenghtMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(zombieStrenghtMatcher.group(1)) * 1000L);
        }
        return false;
    }

    private void setPreStrengthTime(String preStrengthTimer) {
        isStrengthRender = false;
        timeStartRender = System.currentTimeMillis();
        renderDuration = 1000L * Integer.parseInt(preStrengthTimer) + 1000L;
    }

    public void setStrengthRenderStart(long duration) {
        isStrengthRender = true;
        timeStartRender = System.currentTimeMillis();
        renderDuration = duration;
    }

}
