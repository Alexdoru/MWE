package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RenderManagerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderManager";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(MixinLoader.isObf ? "b" : "renderDebugBoundingBox") && methodNode.desc.equals(MixinLoader.isObf ? "(Lpk;DDDFF)V" : "(Lnet/minecraft/entity/Entity;DDDFF)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst.equals(new Double("2.0"))) {
                        methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(new Double("3.0")));
                        methodNode.instructions.remove(insnNode); // TODO add config by calling a method instead of pushing a 3.0d ?
                    }
                }
            }
        }
        return classNode;
    }

}
