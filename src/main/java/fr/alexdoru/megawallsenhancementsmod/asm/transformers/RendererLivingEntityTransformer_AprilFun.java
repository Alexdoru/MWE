package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RendererLivingEntityTransformer_AprilFun implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RendererLivingEntity";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERERLIVINGENTITY$ROTATECORPSE)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ASTORE, 5)) {
                        final InsnList list = new InsnList();
                        list.add(new InsnNode(POP));
                        list.add(new InsnNode(ACONST_NULL));
                        methodNode.instructions.insertBefore(insnNode, list);
                        final InsnList list2 = new InsnList();
                        list2.add(new VarInsnNode(ALOAD, 1));
                        list2.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_AprilFun"), "doFunny", "(L" + ClassMapping.ENTITYLIVINGBASE + ";)V", false));
                        methodNode.instructions.insert(insnNode, list2);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
