package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.asm.transformers.EntityRendererTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.GuiPlayerTabOverlayTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.NetHandlerPlayClientTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.RenderManagerTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            return (new EntityRendererTransformer()).transform(name, transformedName, basicClass);
        }

        if (transformedName.equals("net.minecraft.client.network.NetHandlerPlayClient")) {
            return (new NetHandlerPlayClientTransformer()).transform(name, transformedName, basicClass);
        }

        if (transformedName.equals("net.minecraft.client.gui.GuiPlayerTabOverlay")) {
            return (new GuiPlayerTabOverlayTransformer()).transform(name, transformedName, basicClass);
        }

        if (transformedName.equals("net.minecraft.client.renderer.entity.RenderManager")) {
            return (new RenderManagerTransformer()).transform(name, transformedName, basicClass);
        }

        return basicClass;
    }

}
