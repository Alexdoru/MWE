package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public abstract class GuiPlayerTabOverlayTransformer_HeaderModifier implements MWETransformer {

    protected final int injectAfterHeaderCheck(ClassNode classNode, InjectionCallback status, FieldMapping target, String hookClass) {
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {

                boolean captureLocals = false;
                LabelNode label = null;
                int listIndex = -1;
                int widthIndex = -1;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (!captureLocals && checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkFieldInsnNode(secondNode, GETFIELD, target)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFNULL)) {
                                captureLocals = true;
                                label = ((JumpInsnNode) thirdNode).label;
                            }
                        }
                    }
                    if (captureLocals) {
                        if (checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$LISTFORMATTEDSTRINGTOWIDTH) && checkVarInsnNode(insnNode.getNext(), ASTORE)) {
                            listIndex = ((VarInsnNode) insnNode.getNext()).var;
                        }
                        if (checkMethodInsnNode(insnNode, MethodMapping.MATH$MAX) && checkVarInsnNode(insnNode.getNext(), ISTORE)) {
                            widthIndex = ((VarInsnNode) insnNode.getNext()).var;
                        }
                    }
                    if (captureLocals && insnNode == label) {
                        break;
                    }
                }

                if (label != null && listIndex != -1 && widthIndex != -1) {
                    final InsnList list = getInsnList(listIndex, widthIndex, hookClass);
                    insertAfterLabel(methodNode, label, list);
                    status.addInjection();
                    return listIndex;
                }

            }
        }
        return -1;
    }

    private InsnList getInsnList(int listIndex, int widthIndex, String hookClass) {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, listIndex));
        list.add(new MethodInsnNode(
                INVOKESTATIC,
                getHookClass("mc/gui/" + hookClass),
                "modifyList",
                "(Ljava/util/List;)Ljava/util/List;",
                false
        ));
        list.add(new VarInsnNode(ASTORE, listIndex));
        list.add(new VarInsnNode(ILOAD, widthIndex));
        list.add(new MethodInsnNode(
                INVOKESTATIC,
                getHookClass("mc/gui/" + hookClass),
                "modifyWidth",
                "(I)I",
                false
        ));
        list.add(new VarInsnNode(ISTORE, widthIndex));
        return list;
    }

}
