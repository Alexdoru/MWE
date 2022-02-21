package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetworkPlayerInfoTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetworkPlayerInfo";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lgz$b;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == PUTFIELD && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo") && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "h" : "displayName")) {
                        /*
                         * Replace line 48 with :
                         * this.displayname = NetworkPlayerInfoHook.getDisplayName(this.displayname, this.gameProfile)
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", ASMLoadingPlugin.isObf ? "a" : "gameProfile", "Lcom/mojang/authlib/GameProfile;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/NetworkPlayerInfoHook", "getDisplayName", ASMLoadingPlugin.isObf ? "(Leu;Lcom/mojang/authlib/GameProfile;)Leu;" : "(Lnet/minecraft/util/IChatComponent;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/util/IChatComponent;", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                    }
                }
            }
        }
        return classNode;
    }

}
