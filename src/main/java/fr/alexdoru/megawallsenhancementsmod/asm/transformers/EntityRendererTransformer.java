package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityRendererTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.EntityRenderer";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "g" : "updateLightmap") && methodNode.desc.equals("(F)V")
                    || methodNode.name.equals(ASMLoadingPlugin.isObf ? "i" : "updateFogColor") && methodNode.desc.equals("(F)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == GETSTATIC && insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "r" : "nightVision")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == INVOKEVIRTUAL && nextNode instanceof MethodInsnNode && ((MethodInsnNode) nextNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "isPotionActive")) {
                            AbstractInsnNode secondNode = nextNode.getNext();
                            if (secondNode.getOpcode() == IFEQ && secondNode instanceof JumpInsnNode) {
                                LabelNode labelNode = ((JumpInsnNode) secondNode).label;
                                InsnList list = new InsnList();
                                list.add(new JumpInsnNode(IFEQ, labelNode));
                                list.add(new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", "keepNightVisionEffect", "Z"));
                                methodNode.instructions.insertBefore(secondNode, list);
                            }
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
