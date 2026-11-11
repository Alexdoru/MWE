package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreeperPrimedTntHUD extends SimpleTextRenderer {

    private static final Pattern CREEPER_FISSION_HEART_PATTERN = Pattern.compile("^§a§lFISSION HEART §c§l([0-9])s");
    private long timeStartRender;
    private long renderDuration;
    private String lastCountdownNum = "";
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;

    public CreeperPrimedTntHUD() {
        super(MWEConfig.creeperTNTHUDPosition);
        this.setCentered();
        this.setDummyText(EnumChatFormatting.GREEN + "Tnt " + EnumChatFormatting.RED + "3.0s");
    }

    @Override
    protected void updateText() {
        final long msLeft = timeStartRender + renderDuration - System.currentTimeMillis();
        final String timeLeft = String.format("%.1f", (float) msLeft / 1000);
        this.setText(EnumChatFormatting.GREEN + "Tnt " + colorPrefix + timeLeft + "s");
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return timeStartRender + renderDuration - currentTimeMillis > 0;
    }

    public boolean processMessage(String fmsg) {
        if (this.rendererPosition.isEnabled()) {
            final Matcher creeperMatcher = CREEPER_FISSION_HEART_PATTERN.matcher(fmsg);
            if (creeperMatcher.find()) {
                final String cooldown = creeperMatcher.group(1);
                if (!lastCountdownNum.equals(cooldown)) {
                    timeStartRender = System.currentTimeMillis();
                    renderDuration = 1000L * Integer.parseInt(cooldown) + 1000L;
                }
                switch (cooldown) {
                    case "3":
                        colorPrefix = EnumChatFormatting.GREEN;
                        break;
                    case "2":
                        colorPrefix = EnumChatFormatting.YELLOW;
                        break;
                    case "1":
                        colorPrefix = EnumChatFormatting.GOLD;
                        break;
                    case "0":
                        colorPrefix = EnumChatFormatting.RED;
                        break;
                }
                lastCountdownNum = cooldown;
                return true;
            }
        }
        return false;
    }

}
