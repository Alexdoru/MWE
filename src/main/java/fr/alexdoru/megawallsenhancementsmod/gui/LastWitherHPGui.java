package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LastWitherHPGui extends MyCachedGui {

    public static LastWitherHPGui instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Wither dies in 148s";
    private static long lastupdate = 0;
    private static long thirdWitherDeathTime = 0;
    private static String color;

    public LastWitherHPGui() {
        instance = this;
        guiPosition = ConfigHandler.lastWitherHUDPosition;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void updateDisplayText() {
        int timeToDie = ScoreboardEvent.getMwScoreboardParser().getWitherHP() * 5 / 8 + (int) Math.max(0, (thirdWitherDeathTime + 60000L - System.currentTimeMillis()) / 1000L);
        displayText = color + "Wither dies in " + timeToDie + "s";
    }

    @SubscribeEvent
    public void onMWEvent(MwGameEvent event) {
        if (event.getType() == MwGameEvent.EventType.THIRD_WITHER_DEATH) {
            thirdWitherDeathTime = System.currentTimeMillis();
            color = "\u00a7" + ScoreboardEvent.getMwScoreboardParser().getAliveWithers().get(0);
        }
    }

    @Override
    public void render() {
        final long time = System.currentTimeMillis();
        if (time - lastupdate >= 1000L) {
            updateDisplayText();
            lastupdate = time;
        }
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        frObj.drawStringWithShadow(DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandler.show_lastWitherHUD && FKCounterMod.isInMwGame() && ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive();
    }

}
