//package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;
//
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiIngame;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(GuiIngame.class)
//public class GuiIngameMixin extends Gui {
//
//    @Inject(method = "displayTitle", at = @At("HEAD"), cancellable = true)
//    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut, CallbackInfo ci) {
//        if (subTitle != null && subTitle.contains("Get to the middle to stop the hunger!")) {
//            ci.cancel();
//        }
//    }
//
//}