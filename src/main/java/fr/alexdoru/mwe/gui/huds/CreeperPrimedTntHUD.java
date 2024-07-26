package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreeperPrimedTntHUD extends AbstractRenderer {

    private static final Pattern CREEPER_FISSION_HEART_PATTERN = Pattern.compile("^§a§lFISSION HEART §c§l([0-9])s");
    private long timeStartRender;
    private long renderDuration;
    private String lastCountdownNum = "";
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;

    public CreeperPrimedTntHUD() {
        super(MWEConfig.creeperTNTHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition(resolution);
        final long temp = (timeStartRender + renderDuration - System.currentTimeMillis());
        final String timeLeft = String.format("%.1f", (float) temp / 1000);
        final String displayText = EnumChatFormatting.GREEN + "Tnt " + (colorPrefix + timeLeft + "s");
        drawCenteredString(mc.fontRendererObj, displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        drawCenteredString(mc.fontRendererObj, EnumChatFormatting.GREEN + "Tnt " + EnumChatFormatting.RED + "3.0s", this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return timeStartRender + renderDuration - currentTimeMillis > 0;
    }

    public boolean processMessage(String fmsg) {
        if (MWEConfig.showPrimedTNTHUD) {
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
