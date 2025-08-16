package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class GuiNewChatTransformer_SearchBox implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(11);
        addInterface(classNode, "GuiNewChatExt");
        classNode.visitField(ACC_PRIVATE, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK.name, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK.desc, null, null);
        for (final MethodNode methodNode : classNode.methods) {
            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new TypeInsnNode(NEW, ClassMapping.GUINEWCHATHOOKSEARCHBOX.name));
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(
                                INVOKESPECIAL,
                                ClassMapping.GUINEWCHATHOOKSEARCHBOX.name,
                                "<init>",
                                "(L" + ClassMapping.GUINEWCHAT + ";)V",
                                false
                        ));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$DRAWCHAT)
                    || checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$SCROLL)
                    || checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$GETCHATCOMPONENT)) {
                int injects = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkFieldInsnNode(nextNode, GETFIELD, FieldMapping.GUINEWCHAT$DRAWNCHATLINES)) {
                            final boolean injectUpdate = injects == 0 && checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$DRAWCHAT);
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
                            methodNode.instructions.insertBefore(insnNode, list);
                            methodNode.instructions.insert(nextNode, new MethodInsnNode(
                                    INVOKEVIRTUAL,
                                    getHookClass("GuiNewChatHook_SearchBox"),
                                    injectUpdate ? "updateLinesToRender" : "getLinesToRender",
                                    "(Ljava/util/List;)Ljava/util/List;",
                                    false
                            ));
                            status.addInjection();
                            injects++;
                        }
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$CLEARCHATMESSAGES)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        getHookClass("GuiNewChatHook_SearchBox"),
                        "clearSearch",
                        "()V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$SETCHATLINE)) {
                boolean search = false;
                AbstractInsnNode target1 = null;
                AbstractInsnNode target2 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, GETFIELD, FieldMapping.GUINEWCHAT$DRAWNCHATLINES)) {
                        search = true;
                    } else if (checkFieldInsnNode(insnNode, GETFIELD, FieldMapping.GUINEWCHAT$CHATLINES)) {
                        break;
                    } else if (search && target1 == null && checkTypeInsnNode(insnNode, NEW, ClassMapping.CHATLINE)) {
                        target1 = insnNode;
                    } else if (search && checkMethodInsnNode(insnNode, MethodMapping.LIST$ADD_AT_INDEX)) {
                        target2 = insnNode;
                        break;
                    }
                }
                if (target1 != null && target2 != null) {
                    final InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
                    methodNode.instructions.insertBefore(target1, list);
                    methodNode.instructions.insertBefore(target2, new MethodInsnNode(
                            INVOKEVIRTUAL,
                            getHookClass("GuiNewChatHook_SearchBox"),
                            "addDrawnChatLine",
                            "(L" + ClassMapping.CHATLINE + ";)L" + ClassMapping.CHATLINE + ";",
                            false
                    ));
                    status.addInjection();
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$DELETECHATLINE)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        getHookClass("GuiNewChatHook_SearchBox"),
                        "deleteChatLine",
                        "(I)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }

        final MethodNode mn1 = new MethodNode(ACC_PUBLIC, "mwe$setSearchText", "(Ljava/lang/Object;)V", null, null);
        classNode.methods.add(mn1);
        final LabelNode l0 = new LabelNode();
        mn1.instructions.add(l0);
        mn1.instructions.add(new VarInsnNode(ALOAD, 0));
        mn1.instructions.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
        mn1.instructions.add(new VarInsnNode(ALOAD, 1));
        mn1.instructions.add(new MethodInsnNode(INVOKEVIRTUAL,
                getHookClass("GuiNewChatHook_SearchBox"),
                "setSearchText",
                "(Ljava/lang/Object;)V",
                false));
        final LabelNode l1 = new LabelNode();
        mn1.instructions.add(l1);
        mn1.instructions.add(new InsnNode(RETURN));
        final LabelNode l2 = new LabelNode();
        mn1.instructions.add(l2);
        mn1.localVariables.add(new LocalVariableNode("this", "L" + ClassMapping.GUINEWCHAT.name + ";", null, l0, l2, 0));
        mn1.localVariables.add(new LocalVariableNode("obj", "Ljava/lang/Object;", null, l0, l2, 1));
        mn1.visitMaxs(2, 2);

        final MethodNode mn2 = new MethodNode(ACC_PUBLIC, "mwe$clearSearch", "()V", null, null);
        classNode.methods.add(mn2);
        final LabelNode l00 = new LabelNode();
        mn2.instructions.add(l00);
        mn2.instructions.add(new VarInsnNode(ALOAD, 0));
        mn2.instructions.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUINEWCHAT$SEARCHBOXHOOK));
        mn2.instructions.add(new MethodInsnNode(INVOKEVIRTUAL,
                getHookClass("GuiNewChatHook_SearchBox"),
                "clearSearch",
                "()V",
                false));
        final LabelNode l11 = new LabelNode();
        mn2.instructions.add(l11);
        mn2.instructions.add(new InsnNode(RETURN));
        final LabelNode l22 = new LabelNode();
        mn2.instructions.add(l22);
        mn2.localVariables.add(new LocalVariableNode("this", "L" + ClassMapping.GUINEWCHAT.name + ";", null, l00, l22, 0));
        mn2.visitMaxs(1, 1);

    }

}
