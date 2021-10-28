package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class CancelHungerTitleMWMixin extends Gui {
	
	@Inject(method = "displayTitle", at = @At("HEAD") , cancellable = true )
	public void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_, CallbackInfo ci) {
		if(p_175178_2_ != null && p_175178_2_.contains("Get to the middle to stop the hunger!")) {
			ci.cancel();
		}
	}	
	
}