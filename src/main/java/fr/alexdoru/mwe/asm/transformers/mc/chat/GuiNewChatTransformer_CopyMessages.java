package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiNewChatTransformer_CopyMessages implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$GETCHATCOMPONENT)) {
                boolean searchReturn = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkTypeInsnNode(insnNode, CHECKCAST, ClassMapping.CHATLINE)) {
                        searchReturn = true;
                    } else if (searchReturn) {
                        if (insnNode.getOpcode() != ACONST_NULL && checkInsnNode(insnNode.getNext(), ARETURN)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 10));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiNewChatHook_CopyMessages"),
                                    "copyChatLine",
                                    "(L" + ClassMapping.CHATLINE + ";)V",
                                    false
                            ));
                            methodNode.instructions.insertBefore(insnNode, list);
                            status.addInjection();
                        } else if (checkFrameNode(insnNode, F_CHOP)) {
                            break;
                        }
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$SETCHATLINE)) {
                int injects = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.CHATLINE$INIT)) {
                        final InsnList list = new InsnList();
                        if (injects == 0) {
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiNewChatHook_CopyMessages"),
                                    "setText",
                                    "(L" + ClassMapping.CHATLINE + ";L" + ClassMapping.ICHATCOMPONENT + ";)L" + ClassMapping.CHATLINE + ";",
                                    false
                            ));
                        } else {
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiNewChatHook_CopyMessages"),
                                    "setText1",
                                    "(L" + ClassMapping.CHATLINE + ";)L" + ClassMapping.CHATLINE + ";",
                                    false
                            ));
                        }
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                        injects++;
                    }
                }
            }
        }
    }

}
