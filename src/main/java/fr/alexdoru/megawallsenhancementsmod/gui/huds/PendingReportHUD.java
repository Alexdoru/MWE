package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class PendingReportHUD implements IRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    public static PendingReportHUD instance = new PendingReportHUD();

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
            final int index = ReportQueue.INSTANCE.getIndexOfNextReportToSend();
            final ReportQueue.ReportInQueue reportInQueue = ReportQueue.INSTANCE.queueList.get(index == -1 ? 0 : index);
            final StringBuilder str = new StringBuilder();
            str.append("/wdr ").append(reportInQueue.reportedPlayer);
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
        mc.fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFF55);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.RED + "1 report to send..", this.getGuiPosition().getAbsoluteRenderX(), this.getGuiPosition().getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showReportHUD && !ReportQueue.INSTANCE.queueList.isEmpty();
    }

    @Override
    public GuiPosition getGuiPosition() {
        return ConfigHandler.reportHUDPosition;
    }

}
