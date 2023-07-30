package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RendererLivingEntity_HitColor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(4);
        int ordinal = 0;
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERERLIVINGENTITY$SETBRIGHTNESS)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkFieldInsnNode(secondNode, GETFIELD, FieldMapping.RENDERERLIVINGENTITY$BRIGHTNESSBUFFER)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            final AbstractInsnNode fourthNode = thirdNode.getNext();
                            if (checkMethodInsnNode(fourthNode, MethodMapping.FLOATBUFFER$PUT)) {
                                if (checkInsnNode(thirdNode, FCONST_1)) {
                                    final InsnList list = new InsnList();
                                    list.add(new VarInsnNode(ALOAD, 1));
                                    list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_HitColor"), "getRed", "(FL" + ClassMapping.ENTITYLIVINGBASE + ";)F", false));
                                    methodNode.instructions.insert(thirdNode, list);
                                    status.addInjection();
                                } else if (checkInsnNode(thirdNode, FCONST_0)) {
                                    if (ordinal == 0) {
                                        methodNode.instructions.insert(thirdNode, new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_HitColor"), "getGreen", "(F)F", false));
                                        status.addInjection();
                                        ordinal++;
                                    } else if (ordinal == 1) {
                                        methodNode.instructions.insert(thirdNode, new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_HitColor"), "getBlue", "(F)F", false));
                                        status.addInjection();
                                        ordinal++;
                                    }
                                } else if (checkLdcInsnNode(thirdNode, new Float("0.3"))) {
                                    methodNode.instructions.insert(thirdNode, new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_HitColor"), "getAlpha", "(F)F", false));
                                    status.addInjection();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
