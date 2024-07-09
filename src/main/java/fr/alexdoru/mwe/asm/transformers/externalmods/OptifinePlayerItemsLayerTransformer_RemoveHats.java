package fr.alexdoru.mwe.asm.transformers.externalmods;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import org.objectweb.asm.tree.*;

public class OptifinePlayerItemsLayerTransformer_RemoveHats implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.optifine.player.PlayerItemParser"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("parseItemRenderer") && methodNode.desc.equals("(Lcom/google/gson/JsonObject;Ljava/awt/Dimension;)Lnet/optifine/player/PlayerItemRenderer;")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKESTATIC
                            && ((MethodInsnNode) insnNode).name.equals("parseAttachModel")
                            && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/String;)I")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof VarInsnNode && nextNode.getOpcode() == ISTORE) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ILOAD, ((VarInsnNode) nextNode).var));
                            list.add(new InsnNode(ICONST_1));
                            final LabelNode label = new LabelNode();
                            list.add(new JumpInsnNode(IF_ICMPNE, label));
                            list.add(getNewConfigFieldInsnNode("hideOptifineHats"));
                            list.add(new JumpInsnNode(IFEQ, label));
                            list.add(new InsnNode(ACONST_NULL));
                            list.add(new InsnNode(ARETURN));
                            list.add(label);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
