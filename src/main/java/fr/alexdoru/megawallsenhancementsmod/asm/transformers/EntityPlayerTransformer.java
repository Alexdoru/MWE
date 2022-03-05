package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityPlayerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.entity.player.EntityPlayer";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "f_" : "getDisplayName") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "()Leu;" : "()Lnet/minecraft/util/IChatComponent;")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "aul" : "net/minecraft/scoreboard/ScorePlayerTeam")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "formatPlayerName")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Lauq;Ljava/lang/String;)Ljava/lang/String;" : "(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "wn" : "net/minecraft/entity/player/EntityPlayer", ASMLoadingPlugin.isObf ? "bH" : "gameProfile", "Lcom/mojang/authlib/GameProfile;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/EntityPlayerHook", "getTransformedDisplayName", "(Ljava/lang/String;Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;", false));
                        methodNode.instructions.insertBefore(insnNode.getNext(), list);
                    }
                }
            }
        }
        return classNode;
    }

}
