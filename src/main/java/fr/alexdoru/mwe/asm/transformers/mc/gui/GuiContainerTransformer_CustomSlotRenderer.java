package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class GuiContainerTransformer_CustomSlotRenderer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.inventory.GuiContainer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUICONTAINER$DRAWSLOT)) {
                boolean slice = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (!slice && checkVarInsnNode(insnNode, ALOAD, 4) && checkJumpInsnNode(insnNode.getNext(), IFNONNULL)) {
                        slice = true;
                    }
                    if (slice && checkVarInsnNode(insnNode, ILOAD, 6) && checkJumpInsnNode(insnNode.getNext(), IFNE)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 6));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 4));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("mc/gui/GuiContainerHook_CustomSlotRenderer"),
                                "drawCustomSlot",
                                "(Z" + ClassMapping.GUICONTAINER.desc() + ClassMapping.SLOT.desc() + ClassMapping.ITEMSTACK.desc() + ")Z",
                                false
                        ));
                        list.add(new VarInsnNode(ISTORE, 6));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
