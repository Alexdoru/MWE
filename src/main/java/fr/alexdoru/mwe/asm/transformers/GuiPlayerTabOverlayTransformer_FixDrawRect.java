package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiPlayerTabOverlayTransformer_FixDrawRect implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(4);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                this.fixWidth(methodNode, status);
                this.fixHeight(methodNode, status);
            }
        }
    }

    private void fixHeight(MethodNode methodNode, InjectionStatus status) {
        //    ILOAD 15 <- search
        //    ICONST_1
        //    ISUB
        int ordinal = 0;
        boolean search = false;
        for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$LISTFORMATTEDSTRINGTOWIDTH)) {
                ordinal++;
            }
            if (ordinal == 2 && !search && checkVarInsnNode(insnNode, ALOAD)) {
                final AbstractInsnNode nextNode = insnNode.getNext();
                if (checkJumpInsnNode(nextNode, IFNULL)) {
                    search = true;
                }
            }
            if (search && checkVarInsnNode(insnNode, ILOAD)) {
                final AbstractInsnNode nextNode = insnNode.getNext();
                if (checkInsnNode(nextNode, ICONST_1)) {
                    final AbstractInsnNode thirdNode = nextNode.getNext();
                    if (checkInsnNode(thirdNode, ISUB)) {
                        methodNode.instructions.insert(thirdNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiPlayerTabOverlayHook_FixDrawRect"),
                                "fixDrawRectHeight",
                                "(I)I",
                                false
                        ));
                        status.addInjection();
                        return;

                    }
                }
            }
        }
    }

    private void fixWidth(MethodNode methodNode, InjectionStatus status) {
        //    ILOAD 16 <-- capture var
        //    ICONST_2
        //    IDIV
        //    IADD
        //    ICONST_1
        for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (checkVarInsnNode(insnNode, ILOAD)) {
                final AbstractInsnNode secondNode = insnNode.getNext();
                if (checkInsnNode(secondNode, ICONST_2)) {
                    final AbstractInsnNode thirdNode = secondNode.getNext();
                    if (checkInsnNode(thirdNode, IDIV)) {
                        final AbstractInsnNode fourthNode = thirdNode.getNext();
                        if (checkInsnNode(fourthNode, IADD)) {
                            final AbstractInsnNode fifthNode = fourthNode.getNext();
                            if (checkInsnNode(fifthNode, ICONST_1)) {
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ILOAD, ((VarInsnNode) insnNode).var));
                                list.add(new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("GuiPlayerTabOverlayHook_FixDrawRect"),
                                        "fixDrawRectWidth",
                                        "(I)I",
                                        false
                                ));
                                list.add(new InsnNode(IADD));
                                methodNode.instructions.insert(fifthNode.getNext(), list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
