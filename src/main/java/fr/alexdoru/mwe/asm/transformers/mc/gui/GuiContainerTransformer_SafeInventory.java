package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class GuiContainerTransformer_SafeInventory implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.inventory.GuiContainer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUICONTAINER$CHECKHOTBARKEYS)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GUICONTAINER$HANDLEMOUSECLICK)) {
                        // Insert before line 720 :
                        // if (GuiContainerHook_SafeInventory.shouldCancelHotkey(this.theSlot, i)) {
                        //    return true;
                        // }
                        final InsnList list = new InsnList();
                        final LabelNode notCancelled = new LabelNode();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICONTAINER$THESLOT));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiContainerHook_SafeInventory"),
                                "shouldCancelHotkey",
                                "(L" + ClassMapping.SLOT + ";I)Z",
                                false
                        ));
                        list.add(new JumpInsnNode(IFEQ, notCancelled));
                        list.add(new InsnNode(ICONST_1));
                        list.add(new InsnNode(IRETURN));
                        list.add(notCancelled);
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
