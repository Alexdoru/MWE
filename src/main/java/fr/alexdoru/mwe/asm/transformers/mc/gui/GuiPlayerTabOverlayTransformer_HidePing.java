package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiPlayerTabOverlayTransformer_HidePing implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                AbstractInsnNode latestAload0 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, IADD)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkIntInsnNode(secondNode, BIPUSH, 13)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkInsnNode(thirdNode, IADD)) {
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 5));
                                list.add(new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("GuiPlayerTabOverlayHook_HidePing"),
                                        "getPingWidth",
                                        "(ILjava/util/List;)I",
                                        false
                                ));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    } else if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        latestAload0 = insnNode;
                    } else if (latestAload0 != null && checkMethodInsnNode(insnNode, MethodMapping.GUIPLAYERTABOVERLAY$DRAWPING)) {
                        final LabelNode label = new LabelNode();
                        final InsnList list = new InsnList();
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiPlayerTabOverlayHook_HidePing"),
                                "shouldDrawPing",
                                "()Z",
                                false
                        ));
                        list.add(new JumpInsnNode(IFEQ, label));
                        methodNode.instructions.insertBefore(latestAload0, list);
                        methodNode.instructions.insert(insnNode, label);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
