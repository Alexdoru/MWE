package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityRendererTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.EntityRenderer";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.UPDATELIGHTMAP) || checkMethodNode(methodNode, MethodMapping.UPDATEFOGCOLOR)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, GETSTATIC, FieldMapping.POTION$NIGHTVISION)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (secondNode instanceof MethodInsnNode && secondNode.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) secondNode).name.equals(MethodMapping.ISPOTIONACTIVE.name)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFEQ)) {
                                final LabelNode labelNode = ((JumpInsnNode) thirdNode).label;
                                final InsnList list = new InsnList();
                                list.add(new JumpInsnNode(IFEQ, labelNode));
                                list.add(getNewConfigFieldInsnNode("keepNightVisionEffect"));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
