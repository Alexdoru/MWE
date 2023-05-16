package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer_HidePing implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {
                AbstractInsnNode latestAload0 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, IADD)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkIntInsnNode(secondNode, BIPUSH, 13)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkInsnNode(thirdNode, IADD)) {
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 5));
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "getPingWidth", "(Ljava/util/List;)I", false));
                                methodNode.instructions.insert(insnNode, list);
                                methodNode.instructions.remove(secondNode);
                                status.addInjection();
                            }
                        }
                    } else if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        latestAload0 = insnNode;
                    } else if (latestAload0 != null && checkMethodInsnNode(insnNode, MethodMapping.GUIPLAYERTABOVERLAY$DRAWPING)) {
                        final LabelNode label = new LabelNode();
                        final InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "shouldDrawPing", "()Z", false));
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
