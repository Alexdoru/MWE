package fr.alexdoru.megawallsenhancementsmod.misc;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class LogGlitching {

	public void fastlogglitch() {

		if(FKCounterMod.isInMwGame()) {

			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Emergency exit"));
			(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/l mm");
			new DelayedTask(() -> (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/rejoin"), 3);
			new DelayedTask(() -> (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/l mw"), 6);			
			new DelayedTask(() -> (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/rejoin"), 60);
		} else {
			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The emergency exit isn't available right now"));
		}

	}
	
	private boolean islogglitching = false;
	private int logcounter = 0;

	public void logglitch() {

		if(FKCounterMod.isInMwGame()) {
			islogglitching = true;
			logcounter = 0;
			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() +
					EnumChatFormatting.GREEN + "Emergency exit"));
			(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/l mm");
			new DelayedTask(() -> (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/rejoin"), 60);
			MinecraftForge.EVENT_BUS.register(this);

		} else {

			addChatMessage(new ChatComponentText(ChatUtil.getTagMW() +
					EnumChatFormatting.RED + "The emergency exit isn't available right now"));

		}

	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		
		if(!islogglitching)
			return;
		
		if(logcounter==0)
			logcounter=1;
		
		if(logcounter==3)
			logcounter=4;
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		
		if(!islogglitching)
			return;
		
		if(logcounter==1)
			logcounter=2;
		
		if(logcounter==4)
			logcounter=5;
		
	}
	
	@SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
		
		if(!islogglitching)
			return;
		
		if(logcounter == 2 && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer!=null) {
			logcounter = 3;
			(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/rejoin");
			
		}
		
		if(logcounter == 5 && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer!=null) {
			logcounter = 0;
			islogglitching = false;
			(Minecraft.getMinecraft()).thePlayer.sendChatMessage("/l mw");
			MinecraftForge.EVENT_BUS.unregister(this);
		}
				
	}

}
