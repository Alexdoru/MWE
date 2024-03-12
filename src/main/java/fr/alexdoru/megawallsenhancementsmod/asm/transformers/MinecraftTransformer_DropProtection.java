package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class MinecraftTransformer_DropProtection implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);

        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.MINECRAFT$RUNTICK)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.INVENTORYPLAYER$CHANGECURRENTITEM) ||
                            checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.INVENTORYPLAYER$CURRENTITEM)) {
                        /*
                         * Injects after line 1869 & 2077
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insert(insnNode, updateCurrentSlotInsnList());
                        status.addInjection();
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.ENTITYPLAYERSP$DROPONEITEM)) {
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

    }

    private InsnList updateCurrentSlotInsnList() {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "updateCurrentSlot", "(L" + ClassMapping.MINECRAFT + ";)V", false));
        return list;
    }

}
