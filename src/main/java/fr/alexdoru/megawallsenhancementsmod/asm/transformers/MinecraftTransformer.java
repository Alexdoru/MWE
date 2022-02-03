package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

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
}
