package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.EnumChatFormatting.*;

public class ClassSpecificHUD extends SimpleTextRenderer {

    private static final Pattern DREADLORD_STRENGTH_PATTERN = Pattern.compile("§4§lSOUL SIPHON §c§l85% ([0-9])s");
    private static final Pattern HEROBRINE_STRENGTH_PATTERN = Pattern.compile("§e§lPOWER §c§l85% ([0-9])s");
    private static final Pattern HUNTER_PRE_STRENGTH_PATTERN = Pattern.compile("§a§lF\\.O\\.N\\. §7\\(§l§c§lStrength§7\\) §e§l([0-9]+)");
    private static final Pattern ZOMBIE_STRENGTH_PATTERN = Pattern.compile("§2§lBERSERK §c§l75% ([0-9])s");
    private static final Pattern CREEPER_FISSION_HEART_PATTERN = Pattern.compile("^§a§lFISSION HEART §c§l([0-9])s");
    private static final String HUNTER_STRENGTH_MESSAGE = "Your Force of Nature gave you a 5 second Strength I buff.";
    private static final String PRE_STRENGTH_TEXT = GRAY + "(" + RED + BOLD + "Strength" + GRAY + ")" + YELLOW + BOLD + " in ";
    private static final String STRENGTH_TEXT = RED.toString() + BOLD + "Strength " + YELLOW + BOLD;
    private static final String TNT_TEXT = GREEN + "Tnt ";

    private enum Mode {PRE_STRENGTH, STRENGTH, CREEPER_TNT}

    private final TimerUtil timerStrength = new TimerUtil(11000L);
    private Mode mode = Mode.STRENGTH;
    private long timeStartRender;
    private long renderDuration;
    private String lastTntCountdown = "";
    private EnumChatFormatting tntColor = GREEN;

    public ClassSpecificHUD() {
        super(MWEConfig.classSpecificHUDPosition);
        this.setDummyText(RED + "Class Specific Info");
    }

    @Override
    protected void updateText() {
        final long msLeft = timeStartRender + renderDuration - System.currentTimeMillis();
        switch (mode) {
            case PRE_STRENGTH:
                this.setText(PRE_STRENGTH_TEXT + msLeft / 1000L);
                break;
            case STRENGTH:
                this.setText(STRENGTH_TEXT + msLeft / 1000L);
                break;
            case CREEPER_TNT:
                this.setText(TNT_TEXT + tntColor + String.format("%.1f", (float) msLeft / 1000) + "s");
                break;
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return timeStartRender + renderDuration - currentTimeMillis > 0;
    }

    public boolean processChatMessage(String msg) {
        if (this.rendererPosition.isEnabled() && MWEConfig.strengthHUD && msg.equals(HUNTER_STRENGTH_MESSAGE)) {
            this.setStrengthRenderStart(5000L);
            return true;
        }
        return false;
    }

    public boolean processStatusMessage(String fmsg) {
        if (!this.rendererPosition.isEnabled()) return false;
        if (MWEConfig.strengthHUD && this.processStrengthMessage(fmsg)) return true;
        return MWEConfig.creeperTNTHUD && this.processCreeperMessage(fmsg);
    }

    private boolean processStrengthMessage(String fmsg) {
        final Matcher dreadlordMatcher = DREADLORD_STRENGTH_PATTERN.matcher(fmsg);
        if (dreadlordMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(dreadlordMatcher.group(1)) * 1000L);
            return true;
        }
        final Matcher preStrengthMatcher = HUNTER_PRE_STRENGTH_PATTERN.matcher(fmsg);
        if (preStrengthMatcher.find()) {
            if (timerStrength.update()) {
                SoundUtil.playStrengthSound();
            }
            this.startRender(Mode.PRE_STRENGTH, 1000L * Integer.parseInt(preStrengthMatcher.group(1)) + 1000L);
            return true;
        }
        final Matcher herobrineMatcher = HEROBRINE_STRENGTH_PATTERN.matcher(fmsg);
        if (herobrineMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(herobrineMatcher.group(1)) * 1000L);
            return true;
        }
        final Matcher zombieMatcher = ZOMBIE_STRENGTH_PATTERN.matcher(fmsg);
        if (zombieMatcher.find()) {
            this.setStrengthRenderStart(Long.parseLong(zombieMatcher.group(1)) * 1000L);
            return true;
        }
        return false;
    }

    private boolean processCreeperMessage(String fmsg) {
        final Matcher creeperMatcher = CREEPER_FISSION_HEART_PATTERN.matcher(fmsg);
        if (!creeperMatcher.find()) return false;
        final String cooldown = creeperMatcher.group(1);
        if (!lastTntCountdown.equals(cooldown)) {
            this.startRender(Mode.CREEPER_TNT, 1000L * Integer.parseInt(cooldown) + 1000L);
        }
        switch (cooldown) {
            case "3":
                tntColor = GREEN;
                break;
            case "2":
                tntColor = YELLOW;
                break;
            case "1":
                tntColor = GOLD;
                break;
            case "0":
                tntColor = RED;
                break;
        }
        lastTntCountdown = cooldown;
        return true;
    }

    private void setStrengthRenderStart(long duration) {
        this.startRender(Mode.STRENGTH, duration);
    }

    private void startRender(Mode mode, long duration) {
        this.mode = mode;
        this.timeStartRender = System.currentTimeMillis();
        this.renderDuration = duration;
    }

}
