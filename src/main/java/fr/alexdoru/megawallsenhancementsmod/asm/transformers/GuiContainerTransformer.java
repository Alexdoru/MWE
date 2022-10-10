package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiContainerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.inventory.GuiContainer";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "b" : "checkHotbarKeys") && methodNode.desc.equals("(I)Z")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "ayl" : "net/minecraft/client/gui/inventory/GuiContainer")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "handleMouseClick")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Lyg;III)V" : "(Lnet/minecraft/inventory/Slot;III)V")) {
                        /*
                         * Insert before line 720 :
                         * if(GuiContainerHook.shouldCancelHotkey(this.theSlot, i)) {
                         *    return true;
                         * }
                         */
                        methodNode.instructions.insertBefore(insnNode, getInsnList());
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

    private InsnList getInsnList() {
        final InsnList list = new InsnList();
        final LabelNode notCancelled = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ayl" : "net/minecraft/client/gui/inventory/GuiContainer", ASMLoadingPlugin.isObf ? "u" : "theSlot", ASMLoadingPlugin.isObf ? "Lyg;" : "Lnet/minecraft/inventory/Slot;"));
        list.add(new VarInsnNode(ILOAD, 2));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiContainerHook"), "shouldCancelHotkey", ASMLoadingPlugin.isObf ? "(Lyg;I)Z" : "(Lnet/minecraft/inventory/Slot;I)Z", false));
        list.add(new JumpInsnNode(IFEQ, notCancelled));
        list.add(new InsnNode(ICONST_1));
        list.add(new InsnNode(IRETURN));
        list.add(notCancelled);
        return list;
    }

}
