package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class MinecraftTransformer implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "s" : "runTick") && methodNode.desc.equals("()V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "wm" : "net/minecraft/entity/player/InventoryPlayer")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "d" : "changeCurrentItem")
                            && ((MethodInsnNode) insnNode).desc.equals("(I)V")) {
                        /*
                         * Inject before line 1869
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insertBefore(insnNode, updateCurrentSlotInsnList());
                    }

                    if (insnNode.getOpcode() == PUTFIELD && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "wm" : "net/minecraft/entity/player/InventoryPlayer")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "c" : "currentItem")
                            && ((FieldInsnNode) insnNode).desc.equals("I")) {
                        /*
                         * Inject before line 2077
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insertBefore(insnNode, updateCurrentSlotInsnList());
                    }

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "bew" : "net/minecraft/client/entity/EntityPlayerSP")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "dropOneItem")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Z)Luz;" : "(Z)Lnet/minecraft/entity/item/EntityItem;")) {
                        /*
                         * Replace line 2101 :
                         * this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                         * With :
                         * MinecraftHook.dropOneItem(this.thePlayer);
                         */
                        methodNode.instructions.remove(insnNode.getPrevious());
                        methodNode.instructions.remove(insnNode.getNext());
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "dropOneItem", ASMLoadingPlugin.isObf ? "(Lbew;)V" : "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                    }

                }
            }
        }

        return classNode;
    }

    private InsnList updateCurrentSlotInsnList() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "updateCurrentSlot", ASMLoadingPlugin.isObf ? "(Lave;)V" : "(Lnet/minecraft/client/Minecraft;)V", false));
        return list;
    }
}
