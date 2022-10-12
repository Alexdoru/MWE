package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.*;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class MinecraftTransformer_DropProtection implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);

        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RUNTICK)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.CHANGECURRENTITEM) ||
                            checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.INVENTORYPLAYER$CURRENTITEM)) {
                        /*
                         * Injects after line 1869 & 2077
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insert(insnNode, updateCurrentSlotInsnList());
                        status.addInjection();
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.DROPONEITEM)) {
                        /*
                         * Replaces line 2101 :
                         * this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                         * With :
                         * MinecraftHook.dropOneItem(this.thePlayer);
                         */
                        methodNode.instructions.remove(insnNode.getPrevious());
                        methodNode.instructions.remove(insnNode.getNext());
                        final InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "dropOneItem", "(L" + ClassMapping.ENTITYPLAYERSP + ";)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }

                }
            }
        }

        return classNode;

    }

    private InsnList updateCurrentSlotInsnList() {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "updateCurrentSlot", "(L" + ClassMapping.MINECRAFT + ";)V", false));
        return list;
    }

}
