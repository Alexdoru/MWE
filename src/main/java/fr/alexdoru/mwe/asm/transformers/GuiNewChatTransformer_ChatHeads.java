package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiNewChatTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
        status.setInjectionPoints(4);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$DRAWCHAT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GLSTATEMANAGER$ENABLEBLEND)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 10)); // chatline
                        list.add(new VarInsnNode(ILOAD, 14)); // l1 (alpha)
                        list.add(new VarInsnNode(ILOAD, 15)); // int i2
                        list.add(new VarInsnNode(ILOAD, 16)); // int j2
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiNewChatHook_ChatHeads"),
                                "preRenderStringCall",
                                "(L" + ClassMapping.CHATLINE + ";III)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    } else if (checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$DRAWSTRINGWITHSHADOW)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        methodNode.instructions.insert(nextNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiNewChatHook_ChatHeads"),
                                "postRenderStringCall",
                                "()V",
                                false
                        ));
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$PRINTCHATMESSAGE)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("GuiNewChatHook_ChatHeads"),
                        "addHeadToMessage",
                        "(L" + ClassMapping.ICHATCOMPONENT + ";)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$GETCHATCOMPONENT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ILOAD, 11)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkVarInsnNode(secondNode, ILOAD, 6)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IF_ICMPLE)) {
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 10));
                                list.add(new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("GuiNewChatHook_ChatHeads"),
                                        "fixComponentHover",
                                        "(L" + ClassMapping.CHATLINE + ";)I",
                                        false
                                ));
                                list.add(new InsnNode(IADD));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
