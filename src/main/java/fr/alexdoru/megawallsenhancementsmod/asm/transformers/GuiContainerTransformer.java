package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.*;
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
            if (checkMethodNode(methodNode, MethodMapping.CHECKHOTBARKEYS)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.HANDLEMOUSECLICK)) {
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
        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICONTAINER$THESLOT));
        list.add(new VarInsnNode(ILOAD, 2));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiContainerHook"), "shouldCancelHotkey", "(L" + ClassMapping.SLOT + ";I)Z", false));
        list.add(new JumpInsnNode(IFEQ, notCancelled));
        list.add(new InsnNode(ICONST_1));
        list.add(new InsnNode(IRETURN));
        list.add(notCancelled);
        return list;
    }

}
