package fr.alexdoru.fkcountermod.events;

import fr.alexdoru.fkcountermod.utils.MinecraftUtils;
import fr.alexdoru.fkcountermod.utils.ScoreboardParser;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ScoreboardEvent {

	private static ScoreboardParser mwScoreboardParser = new ScoreboardParser(null);
	private static String prevGameId = null;
	private static boolean prevHasGameEnded = false;
	private static boolean isHypixel = false;

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {

		Minecraft mc = Minecraft.getMinecraft();

		if (mc.theWorld == null || !isHypixel) { 
			return;
		}
		
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		if (scoreboard == null) {
			return;
		}

		mwScoreboardParser = new ScoreboardParser(scoreboard);

		String gameId = mwScoreboardParser.getGameId();
		boolean hasgameended = mwScoreboardParser.hasGameEnded();
		
		if (gameId == null) {

			if (prevGameId != null) {
				MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.DISCONNECT));
			}

		} else if (!gameId.equals(prevGameId)) {
			MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.CONNECT));
		}
				
		if(hasgameended) {
			if(!prevHasGameEnded) {
				MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.GAME_END));
			}
		}

		prevGameId = gameId;
		prevHasGameEnded = hasgameended;
	}
	
	@SubscribeEvent
	public void onConnection(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		isHypixel = MinecraftUtils.isHypixel();
	}
	
	@SubscribeEvent
	public void onDisconnection(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		isHypixel = false;
	}

	public static ScoreboardParser getMwScoreboardParser() {
		return mwScoreboardParser;
	}

}
