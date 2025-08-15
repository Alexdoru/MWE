package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiChatTransformer_SearchBox implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(7);
        classNode.visitField(ACC_PRIVATE, FieldMapping.GUICHAT$SEARCHBOXHOOK.name, FieldMapping.GUICHAT$SEARCHBOXHOOK.desc, null, null);
        for (final MethodNode methodNode : classNode.methods) {
            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new TypeInsnNode(NEW, ClassMapping.GUICHATHOOKSEARCHBOX.name));
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(
                                INVOKESPECIAL,
                                ClassMapping.GUICHATHOOKSEARCHBOX.name,
                                "<init>",
                                "(L" + ClassMapping.GUICHAT + ";)V",
                                false
                        ));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUICHAT$INITGUI)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                        list.add(new MethodInsnNode(
                                INVOKEVIRTUAL,
                                getHookClass("GuiChatHook_SearchBox"),
                                "onInitGui",
                                "()V",
                                false
                        ));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUICHAT$ONGUICLOSED)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        getHookClass("GuiChatHook_SearchBox"),
                        "onGuiClosed",
                        "()V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            } else if (checkMethodNode(methodNode, MethodMapping.GUICHAT$DRAWSCREEN)) {
                // we inject after this.inputField.drawtextbox(); because if we inject
                // at head, it will get wrapped inside an if statement injected by Patcher's (bad) ASM
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GUITEXTFIELD$DRAWTEXTBOX)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new MethodInsnNode(
                                INVOKEVIRTUAL,
                                getHookClass("GuiChatHook_SearchBox"),
                                "onDrawScreen",
                                "(II)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                        break;
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUICHAT$KEYTYPED)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        getHookClass("GuiChatHook_SearchBox"),
                        "onKeyTyped",
                        "(CI)Z",
                        false
                ));
                final LabelNode label = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, label));
                list.add(new InsnNode(RETURN));
                list.add(label);
                methodNode.instructions.insert(list);
                status.addInjection();
            } else if (checkMethodNode(methodNode, MethodMapping.GUICHAT$MOUSECLICKED)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$SEARCHBOXHOOK));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        getHookClass("GuiChatHook_SearchBox"),
                        "onMouseClicked",
                        "(III)Z",
                        false
                ));
                final LabelNode label = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, label));
                list.add(new InsnNode(RETURN));
                list.add(label);
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
