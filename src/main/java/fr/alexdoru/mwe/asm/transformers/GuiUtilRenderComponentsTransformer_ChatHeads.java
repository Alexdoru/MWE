package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiUtilRenderComponentsTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiUtilRenderComponents"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        return !MWELoadingPlugin.isFeatherPresent();
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(6);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIUTILRENDERCOMPONENTS$SPLITTEXT)) {
                boolean injectedFirst = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.CHATCOMPONENTTEXT$INIT)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (!injectedFirst && checkVarInsnNode(nextNode, ASTORE)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiUtilRenderComponentsHook_ChatHeads"),
                                    "getStarterChatComponent",
                                    "(L" + ClassMapping.CHATCOMPONENTTEXT + ";L" + ClassMapping.ICHATCOMPONENT + ";)L" + ClassMapping.CHATCOMPONENTTEXT + ";",
                                    false
                            ));
                            methodNode.instructions.insert(insnNode, list);
                            status.addInjection();
                            injectedFirst = true;
                        }
                    } else if (checkVarInsnNode(insnNode, ILOAD, 1)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiUtilRenderComponentsHook_ChatHeads"),
                                "modifyLineWidth",
                                "(I)I",
                                false
                        ));
                        status.addInjection();
                    } else if (checkMethodInsnNode(insnNode, MethodMapping.LIST$ADD)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            methodNode.instructions.insert(nextNode, new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("GuiUtilRenderComponentsHook_ChatHeads"),
                                    "removeOffset",
                                    "()V",
                                    false
                            ));
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
