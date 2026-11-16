package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarcryHUD extends SimpleTextRenderer {

    private static final String WARCRY_AVAILABLE_MESSAGE = "Your warcry is now available!";
    private static final Pattern WARCRY_COOLDOWN_PATTERN = Pattern.compile("^You can use your warcry again in (\\d+)m (\\d+)s!$");
    private static final Pattern WARCRY_ACTIVATION_PATTERN = Pattern.compile("^You performed the (?:\\w+ )+warcry!$");
    private long activationTime;

    public WarcryHUD() {
        super(MWEConfig.warcryHUDPosition);
        this.setDummyText(EnumChatFormatting.RED + "4m 15s");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMWEvent(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START || event.type == Type.DEATHMATCH_START) {
            this.activationTime = 0;
        }
    }

    @SubscribeEvent
    public void onGuiScreen(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.gui instanceof GuiGameOver) {
            this.activationTime = 0;
        }
    }

    @Override
    protected void updateText() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.activationTime + 30L * 1000L > currentTimeMillis) {
            this.setText(EnumChatFormatting.GREEN + formatTime(this.activationTime + 30L * 1000L - currentTimeMillis));
        } else {
            this.setText(EnumChatFormatting.RED + formatTime(this.activationTime + 5L * 60L * 1000L - currentTimeMillis));
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && this.parser.isInMwGame() && this.activationTime + 5L * 60L * 1000L > currentTimeMillis;
    }

    private static String formatTime(long millisec) {
        int sec = (int) (millisec / 1000L);
        final int min = sec / 60;
        sec = sec % 60;
        return sec < 10 ? min + "m 0" + sec + "s" : min + "m " + sec + "s";
    }

    public boolean processMessage(String msg) {
        if (WARCRY_AVAILABLE_MESSAGE.equals(msg)) {
            this.activationTime = 0;
            return true;
        }
        final Matcher matcher = WARCRY_COOLDOWN_PATTERN.matcher(msg);
        if (matcher.matches()) {
            final String min = matcher.group(1);
            final String sec = matcher.group(2);
            final int secLeft = Integer.parseInt(min) * 60 + Integer.parseInt(sec);
            this.activationTime = System.currentTimeMillis() - (5L * 60L * 1000L - secLeft * 1000L);
            return true;
        } else if (WARCRY_ACTIVATION_PATTERN.matcher(msg).matches()) {
            this.activationTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
