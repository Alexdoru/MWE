package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class MinecraftTransformer_WarpProtection implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.MINECRAFT$RIGHTCLICKMOUSE)) {
                int ordinal = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.INVENTORYPLAYER$GETCURRENTITEM)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (ordinal == 0 && checkVarInsnNode(nextNode, ASTORE)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, ((VarInsnNode) nextNode).var));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "shouldCancelRightClick", "(L" + ClassMapping.ITEMSTACK + ";)Z", false));
                            final LabelNode notCancelled = new LabelNode();
                            list.add(new JumpInsnNode(IFEQ, notCancelled)); // if (true) { return;} else {jump to notCancelled label}
                            list.add(new InsnNode(RETURN)); // return;
                            list.add(notCancelled);
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                        ordinal++;
                    }
                }
            }
        }
    }

}
