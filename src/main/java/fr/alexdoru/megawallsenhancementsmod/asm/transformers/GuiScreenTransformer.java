package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiScreenTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiScreen";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.HANDLECOMPONENTCLICK)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkVarInsnNode(secondNode, ALOAD, 2)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkMethodInsnNode(thirdNode, MethodMapping.CLICKEVENT$GETVALUE)) {
                                final AbstractInsnNode fourthNode = thirdNode.getNext();
                                if (checkInsnNode(fourthNode, ICONST_0)) {
                                    final AbstractInsnNode fifthNode = fourthNode.getNext();
                                    if (checkMethodInsnNode(fifthNode, MethodMapping.SENDCHATMESSAGE)) {
                                        /*
                                         * Inject before line 450 :
                                         * if(GuiScreenHook.handleMWEnCustomChatCommand(clickevent.getValue())) {
                                         *     return true;
                                         * }
                                         */
                                        methodNode.instructions.insert(insnNode, getInsnList());
                                        status.addInjection();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return classNode;
    }

    private InsnList getInsnList() {
        final InsnList list = new InsnList();
        final LabelNode notCancelled = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 2));
        list.add(getNewMethodInsnNode(MethodMapping.CLICKEVENT$GETVALUE));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenHook"), "handleMWEnCustomChatCommand", "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFEQ, notCancelled));
        list.add(new InsnNode(ICONST_1));
        list.add(new InsnNode(IRETURN));
        list.add(notCancelled);
        return list;
    }

}
