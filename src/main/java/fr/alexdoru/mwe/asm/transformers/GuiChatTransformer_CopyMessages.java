package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiChatTransformer_CopyMessages implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUICHAT$MOUSECLICKED)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("GuiChatHook_CopyMessages"),
                        "onChatRightClick",
                        "(I)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 4)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkMethodInsnNode(nextNode, MethodMapping.GUICHAT$HANDLECOMPONENTCLICK)) {
                            final InsnList list2 = new InsnList();
                            list2.add(new VarInsnNode(ALOAD, 4));
                            list2.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiChatHook_CopyMessages"),
                                    "onChatLeftClick",
                                    "(ZL" + ClassMapping.ICHATCOMPONENT + ";)Z",
                                    false
                            ));
                            methodNode.instructions.insert(nextNode, list2);
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
