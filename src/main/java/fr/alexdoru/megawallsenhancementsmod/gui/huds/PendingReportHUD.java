package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class PendingReportHUD extends AbstractRenderer {

    public static PendingReportHUD INSTANCE;

    public PendingReportHUD() {
        super(ConfigHandler.reportHUDPosition);
        INSTANCE = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final String text;
        if (ReportQueue.INSTANCE.standingStillCounter == 0) {
            if (ConfigHandler.showReportHUDonlyInChat) return;
            final int nbReports = ReportQueue.INSTANCE.queueList.size();
            final long l = System.currentTimeMillis() % 3000L + 1000L;
            final StringBuilder str = new StringBuilder();
            str.append(EnumChatFormatting.RED).append(nbReports).append(" report");
            if (nbReports > 1) str.append("s");
            str.append(" to send");
            for (int i = 0; i < l / 1000L; i++) {
                str.append('.');
            }
            text = str.toString();
        } else {
            final float progress = ReportQueue.INSTANCE.standingStillCounter / (float) ReportQueue.INSTANCE.standingStillLimit;
            final String playername = ReportQueue.INSTANCE.queueList.get(0);
            final StringBuilder str = new StringBuilder();
            str.append("/wdr ").append(playername);
            final int msgLength = str.length();
            int offset = (int) (progress * (msgLength)) + 1;
            offset = Math.max(0, offset);
            offset = Math.min(offset, msgLength);
            str.insert(offset, EnumChatFormatting.WHITE);
            text = str.toString();
        }
        this.getGuiPosition().updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(text), mc.fontRendererObj.FONT_HEIGHT);
        final int x = this.getGuiPosition().getAbsoluteRenderX();
        final int y = this.getGuiPosition().getAbsoluteRenderY();
        mc.fontRendererObj.drawStringWithShadow(text, x + 1, y, 0xFFFF55);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.RED + "1 report to send..", this.getGuiPosition().getAbsoluteRenderX() + 1, this.getGuiPosition().getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showReportHUD && !ReportQueue.INSTANCE.queueList.isEmpty();
    }

}
