package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.nocheatersmod.dataobjects.WDR;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin {

	@Shadow
	private IChatComponent displayName;

	@Shadow
	private GameProfile gameProfile;

	/*
	 * replaces the names of squadmates
	 * adds a tag to squadmates
	 * adds a tag to reported players
	 * unscrambles the tablist
	 */
	@Inject(method = "<init>(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", at = @At("RETURN"))
	public void NetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData p_i46295_1_, CallbackInfo ci)
	{

		if(this.displayName != null) {

			String formattedname = this.displayName.getFormattedText();	
			if(formattedname.contains("\u00a7k")) {return;}

			String username = this.gameProfile.getName();
			String squadname = SquadEvent.getSquad().get(username);
			String extraprefix = "";
			boolean issquadmate = false;
			boolean needtochange = false;

			if(squadname != null) {
				extraprefix = SquadEvent.getprefix();
				issquadmate = true;
				needtochange = true;
			}

			String uuid = this.gameProfile.getId().toString().replace("-", "");
			WDR wdr = NoCheatersEvents.wdred.get(uuid);

			if(wdr != null) {

				if(wdr.hacks.contains("bhop")) {
					extraprefix += NoCheatersEvents.prefix_bhop; 
				} else {
					extraprefix += NoCheatersEvents.prefix;
				}

				needtochange = true;
			} else {

				IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

				if(imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
					extraprefix += CommandScanGame.prefix;
					needtochange = true;
				}

			}

			if(needtochange) {
				this.displayName = (IChatComponent) new ChatComponentText(extraprefix).appendSibling(new ChatComponentText((issquadmate?formattedname.replace(username, squadname):formattedname)));
			} else {
				return;
			}

		} else {
			String username = this.gameProfile.getName();
			ScorePlayerTeam team = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(username);
			if(team != null && team.getColorPrefix().contains("\u00a7k")) {return;}		
			String squadname = SquadEvent.getSquad().get(username);
			String extraprefix = "";
			boolean issquadmate = false;
			boolean needtochange = false;

			if(squadname != null) {
				extraprefix = SquadEvent.getprefix();
				issquadmate = true;
				needtochange = true;
			}

			String uuid = this.gameProfile.getId().toString().replace("-", "");
			WDR wdr = NoCheatersEvents.wdred.get(uuid);

			if(wdr != null) {

				if(wdr.hacks.contains("bhop")) {
					extraprefix += NoCheatersEvents.prefix_bhop; 
				} else {
					extraprefix += NoCheatersEvents.prefix;
				}

				needtochange = true;
			} else {

				IChatComponent imsg = CommandScanGame.getScanmap().get(uuid);

				if(imsg != null && !imsg.equals(CommandScanGame.nomatch)) {
					extraprefix += CommandScanGame.prefix;
					needtochange = true;
				}

			}

			if(team != null) {
				if(needtochange) {
					this.displayName = (IChatComponent) new ChatComponentText(extraprefix + team.getColorPrefix() + (issquadmate ? squadname : username) + team.getColorSuffix());
					return;
				}
			}
		}	

	}

}
