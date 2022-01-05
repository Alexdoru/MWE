package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
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
            if (methodNode.name.equals("<init>") && methodNode.desc.equals(MixinLoader.isObf ? "(Lgz$b;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == PUTFIELD && ((FieldInsnNode) insnNode).owner.equals(MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo") && ((FieldInsnNode) insnNode).name.equals(MixinLoader.isObf ? "h" : "displayName")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", MixinLoader.isObf ? "h" : "displayName", MixinLoader.isObf ? "Leu;" : "Lnet/minecraft/util/IChatComponent;"));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", MixinLoader.isObf ? "a" : "gameProfile", "Lcom/mojang/authlib/GameProfile;"));
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/NetworkPlayerInfoHook", "getDisplayName", MixinLoader.isObf ? "(Leu;Lcom/mojang/authlib/GameProfile;)Leu;" : "(Lnet/minecraft/util/IChatComponent;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/util/IChatComponent;", false));
                            list.add(new FieldInsnNode(PUTFIELD, MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", MixinLoader.isObf ? "h" : "displayName", MixinLoader.isObf ? "Leu;" : "Lnet/minecraft/util/IChatComponent;"));
                            methodNode.instructions.insertBefore(nextNode, list);
                            MixinLoader.logger.info("Transformed NetworkPlayerInfo");
                            return classNode;
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
