package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LayerArmorBaseTransformer_HitColor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.LAYERARMORBASE$SHOULDCOMBINETEXTURES)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, IRETURN)) {
                        methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("mc/render/LayerArmorBaseHook_HitColor"),
                                "shouldCombineTextures",
                                "(Z)Z",
                                false
                        ));
                        status.addInjection();
                    }
                }
            }
        }
    }

}
