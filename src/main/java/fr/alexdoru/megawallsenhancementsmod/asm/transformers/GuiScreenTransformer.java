package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
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
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "handleComponentClick") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Leu;)Z" : "(Lnet/minecraft/util/IChatComponent;)Z")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof VarInsnNode && insnNode.getOpcode() == ALOAD && ((VarInsnNode) insnNode).var == 0) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (secondNode instanceof VarInsnNode && secondNode.getOpcode() == ALOAD && ((VarInsnNode) secondNode).var == 2) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (thirdNode instanceof MethodInsnNode && thirdNode.getOpcode() == INVOKEVIRTUAL
                                    && ((MethodInsnNode) thirdNode).owner.equals(ASMLoadingPlugin.isObf ? "et" : "net/minecraft/event/ClickEvent")
                                    && ((MethodInsnNode) thirdNode).name.equals(ASMLoadingPlugin.isObf ? "b" : "getValue")
                                    && ((MethodInsnNode) thirdNode).desc.equals("()Ljava/lang/String;")) {
                                final AbstractInsnNode fourthNode = thirdNode.getNext();
                                if (fourthNode instanceof InsnNode && fourthNode.getOpcode() == ICONST_0) {
                                    final AbstractInsnNode fifthNode = fourthNode.getNext();
                                    if (fifthNode instanceof MethodInsnNode && fifthNode.getOpcode() == INVOKEVIRTUAL
                                            && ((MethodInsnNode) fifthNode).owner.equals(ASMLoadingPlugin.isObf ? "axu" : "net/minecraft/client/gui/GuiScreen")
                                            && ((MethodInsnNode) fifthNode).name.equals(ASMLoadingPlugin.isObf ? "b" : "sendChatMessage")
                                            && ((MethodInsnNode) fifthNode).desc.equals("(Ljava/lang/String;Z)V")) {
                                        /*
                                         * Inject before line 450 :
                                         * if(GuiScreenHook.handleMWEnCustomChatCommand(clickevent.getValue())) {
                                         *     return true;
                                         * }
                                         */
                                        methodNode.instructions.insertBefore(secondNode, getInsnList());
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
        list.add(new MethodInsnNode(INVOKEVIRTUAL, ASMLoadingPlugin.isObf ? "et" : "net/minecraft/event/ClickEvent", ASMLoadingPlugin.isObf ? "b" : "getValue", "()Ljava/lang/String;", false));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenHook"), "handleMWEnCustomChatCommand", "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFEQ, notCancelled));
        list.add(new InsnNode(ICONST_1));
        list.add(new InsnNode(IRETURN));
        list.add(notCancelled);
        return list;
    }

}
