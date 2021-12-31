//package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;
//
//import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
//import net.minecraft.scoreboard.ScorePlayerTeam;
//import net.minecraft.scoreboard.Scoreboard;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Slice;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(Scoreboard.class)
//public class ScoreboardMixin {
//
//    @Inject(
//            method = "addPlayerToTeam",
//            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;getMembershipCollection()Ljava/util/Collection;")),
//            at = @At(value = "RETURN")
//    )
//    public void addPlayerToTeam(String player, String newTeam, CallbackInfoReturnable<Boolean> ci) {
//        if (ci.getReturnValueZ()) NameUtil.transformNameTablist(player);
//    }
//
//    @Inject(
//            method = "removePlayerFromTeam",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Ljava/util/Collection;remove(Ljava/lang/Object;)Z",
//                    shift = At.Shift.AFTER
//            ))
//    public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_, CallbackInfo ci) {
//        NameUtil.transformNameTablist(p_96512_1_);
//    }
//
//}
