package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.commands.CommandScanGame;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocrawListener {

    private static final Pattern LOCRAW_PATTERN = Pattern.compile("^\\{\"server\":\"(\\w+)\",\"gametype\":\"\\w+\"(?:|,\"lobbyname\":\"\\w+\")(?:|,\"mode\":\"\\w+\")(?:|,\"map\":\"([a-zA-Z0-9_ ]+)\")\\}$");
    private final LocrawAction action;
    private int ticks;

    private LocrawListener(LocrawAction action) {
        this.action = action;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            this.ticks++;
            if (ticks > 100) {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type == 0) {
            final String msg = StringUtil.removeFormattingCodes(event.message.getUnformattedText());
            final Matcher locrawMatcher = LOCRAW_PATTERN.matcher(msg);
            if (locrawMatcher.matches()) {
                final String gameId = locrawMatcher.group(1).replace("mega", "M");
                final String map = locrawMatcher.group(2);
                if (action == LocrawAction.RUNSCANGAME) {
                    CommandScanGame.handleScangameCommand(gameId);
                }
                GuiManager.baseLocationHUD.setCurrentMap(map); // can't hurt to set the map everytime
                event.setCanceled(true);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    public static void runScangame() {
        sendLocraw(LocrawAction.RUNSCANGAME);
    }

    public static void setMegaWallsMap() {
        sendLocraw(LocrawAction.SETMEGAWALLSMAP);
    }

    private static void sendLocraw(LocrawAction action) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
            MinecraftForge.EVENT_BUS.register(new LocrawListener(action));
        }
    }

    private enum LocrawAction {
        RUNSCANGAME, SETMEGAWALLSMAP
    }

}
