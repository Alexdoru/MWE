package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class LayerArrowTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.layers.LayerArrow";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "doRenderLayer") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lpr;FFFFFFF)V" : "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ASTORE && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 10) {
                        /*
                         * Injects after line 32 :
                         * entity.isPinnedToPlayer = true;
                         */
                        InsnList list = new InsnList();
                        list.add(new InsnNode(DUP));
                        list.add(new InsnNode(ICONST_1));
                        list.add(new FieldInsnNode(PUTFIELD, ASMLoadingPlugin.isObf ? "wq" : "net/minecraft/entity/projectile/EntityArrow", "isPinnedToPlayer", "Z"));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

}
