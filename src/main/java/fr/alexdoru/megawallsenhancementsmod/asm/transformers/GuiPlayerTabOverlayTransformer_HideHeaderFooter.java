package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiPlayerTabOverlayTransformer_HideHeaderFooter implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkFieldInsnNode(secondNode, GETFIELD, FieldMapping.GUIPLAYERTABOVERLAY$HEADER)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFNULL)) {
                                final LabelNode label = ((JumpInsnNode) thirdNode).label;
                                final InsnList list = new InsnList();
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_HideHeaderFooter"), "shouldRenderHeader", "()Z", false));
                                list.add(new JumpInsnNode(IFEQ, label));
                                methodNode.instructions.insert(thirdNode, list);
                                status.addInjection();
                            }
                        } else if (checkFieldInsnNode(secondNode, GETFIELD, FieldMapping.GUIPLAYERTABOVERLAY$FOOTER)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFNULL)) {
                                final LabelNode label = ((JumpInsnNode) thirdNode).label;
                                final InsnList list = new InsnList();
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_HideHeaderFooter"), "shouldHideFooter", "()Z", false));
                                list.add(new JumpInsnNode(IFNE, label));
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
