package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.misc.NameModifier;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin {

    @Shadow
    private IChatComponent displayName;

    @Final
    @Shadow
    private GameProfile gameProfile;

    /*
     * Adds a hook
     */
    @Inject(method = "<init>(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", at = @At("RETURN"))
    public void NetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData p_i46295_1_, CallbackInfo ci) {
        this.displayName = NameModifier.getTransformedDisplayName(this.gameProfile, this.displayName);
    }

}
