package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class PendingReportHUD extends AbstractRenderer {

    public PendingReportHUD() {
        super(MWEConfig.reportHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final String text;
        if (ReportQueue.INSTANCE.getStandStillCounter() == 0) {
            if (MWEConfig.showReportHUDonlyInChat) return;
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
            final float progress = ReportQueue.INSTANCE.getStandStillCounter() / (float) ReportQueue.INSTANCE.getStandStillLimit();
            final String playername = ReportQueue.INSTANCE.queueList.get(0).name;
            final StringBuilder str = new StringBuilder();
            str.append("/wdr ").append(playername);
            final int msgLength = str.length();
            int offset = (int) (progress * (msgLength)) + 1;
            offset = Math.max(0, offset);
            offset = Math.min(offset, msgLength);
            str.insert(offset, EnumChatFormatting.WHITE);
            text = str.toString();
        }
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(text), mc.fontRendererObj.FONT_HEIGHT);
        final int x = this.guiPosition.getAbsoluteRenderX();
        final int y = this.guiPosition.getAbsoluteRenderY();
        mc.fontRendererObj.drawStringWithShadow(text, x + 1, y, 0xFFFF55);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.RED + "1 report to send..", this.guiPosition.getAbsoluteRenderX() + 1, this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled() && !ReportQueue.INSTANCE.queueList.isEmpty();
    }

}
