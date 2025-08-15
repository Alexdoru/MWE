package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityRendererTransformer_CancelNightVision implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYRENDERER$UPDATELIGHTMAP) || checkMethodNode(methodNode, MethodMapping.ENTITYRENDERER$UPDATEFOGCOLOR)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, GETSTATIC, FieldMapping.POTION$NIGHTVISION)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (secondNode instanceof MethodInsnNode && secondNode.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) secondNode).name.equals(MethodMapping.ENTITYLIVINGBASE$ISPOTIONACTIVE.name)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFEQ)) {
                                final LabelNode labelNode = ((JumpInsnNode) thirdNode).label;
                                final InsnList list = new InsnList();
                                list.add(getNewConfigFieldInsnNode("cancelNightVisionEffect"));
                                list.add(new JumpInsnNode(IFNE, labelNode));
                                methodNode.instructions.insert(thirdNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
