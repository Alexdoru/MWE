package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            return (new EntityRendererTransformer()).transform(name, transformedName, basicClass);
        }

        if (transformedName.equals("net.minecraft.client.network.NetHandlerPlayClient")) {
            MixinLoader.logger.info("------------ found class NetHandlerPlayClient");
            return (new NetHandlerPlayClientTransformer()).transform(name, transformedName, basicClass);
        }

        return basicClass;
    }

}
