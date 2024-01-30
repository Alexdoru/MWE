package fr.alexdoru.megawallsenhancementsmod.hackerdetector.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class PacketHUD {

    public static final PacketHUD INSTANCE = new PacketHUD();
    private final List<TextLine> textLines = new ArrayList<>(100);

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && !textLines.isEmpty()) {
            GlStateManager.pushMatrix();
            {
                final int x = event.resolution.getScaledWidth() / 6;
                int y = event.resolution.getScaledHeight() / 6 * 5;
                final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
                for (final TextLine line : textLines) {
                    fr.drawStringWithShadow(line.getLine(), x, y, 0xFFFFFFFF);
                    y -= fr.FONT_HEIGHT;
                    if (y < 0) break;
                }
            }
            GlStateManager.popMatrix();
        }
    }

    public void logPacket(Packet<?> packet) {
        try {
            final String time = ServerPacketLogger.formatTime();
            Minecraft.getMinecraft().addScheduledTask(() -> this.addLine(time + ServerPacketLogger.packetToString(packet)));
        } catch (Throwable t) {
            Minecraft.getMinecraft().addScheduledTask(()-> {
                if (packet == null) {
                    this.addLine(EnumChatFormatting.RED + "Exception caught for NULL packet");
                } else {
                    this.addLine(EnumChatFormatting.RED + "Exception caught for " + packet.getClass().getName());
                }
            });
        }
    }

    private void addLine(String text) {
        if (!textLines.isEmpty() && textLines.get(0).text.equals(text)) {
            textLines.get(0).count++;
            return;
        }
        while (textLines.size() >= 100) {
            textLines.remove(textLines.size() - 1);
        }
        textLines.add(0, new TextLine(text));
    }

    private static class TextLine {
        public int count = 1;
        public final String text;

        public TextLine(String text) {
            this.text = text;
        }

        public String getLine() {
            if (count < 2) return text;
            return EnumChatFormatting.GRAY + " (" + count + ")";
        }
    }


}
