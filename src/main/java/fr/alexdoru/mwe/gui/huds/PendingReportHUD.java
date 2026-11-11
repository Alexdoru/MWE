package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import net.minecraft.util.EnumChatFormatting;

public class PendingReportHUD extends SimpleTextRenderer {

    public PendingReportHUD() {
        super(MWEConfig.reportHUDPosition);
        this.setDummyText(EnumChatFormatting.RED + "1 report to send..");
        this.setColor(0xFFFF55);
    }

    @Override
    protected void updateText() {
        if (ReportQueue.INSTANCE.getStandStillCounter() == 0) {
            final int nbReports = ReportQueue.INSTANCE.queueList.size();
            final long l = System.currentTimeMillis() % 3000L + 1000L;
            final StringBuilder str = new StringBuilder();
            str.append(EnumChatFormatting.RED).append(nbReports).append(" report");
            if (nbReports > 1) str.append("s");
            str.append(" to send");
            for (int i = 0; i < l / 1000L; i++) {
                str.append('.');
            }
            this.setText(str.toString());
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
            this.setText(str.toString());
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled()
                && !ReportQueue.INSTANCE.queueList.isEmpty()
                && !(MWEConfig.showReportHUDonlyInChat && ReportQueue.INSTANCE.getStandStillCounter() == 0);
    }

}
