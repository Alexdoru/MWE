package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetworkPlayerInfoTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetworkPlayerInfo";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(2);

        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessor/NetworkPlayerInfoAccessor");

        {
            FieldVisitor fieldVisitor = classNode.visitField(ACC_PUBLIC, "playerFinalkills", "I", null, 0);
            fieldVisitor.visitEnd();
        }

        {
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "setPlayerFinalkills", "(I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitFieldInsn(PUTFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", "playerFinalkills", "I");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lbdc;" : "Lnet/minecraft/client/network/NetworkPlayerInfo;", null, l0, l2, 0);
            mv.visitLocalVariable("playerFinalkillsIn", "I", null, l0, l2, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

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
                        status.addInjection();

                        /*Adds after line 48 : this.playersFinalKills = NetworkPlayerInfoHook.getPlayersFinals(this.gameProfile.getName())*/
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null) {
                            InsnList list2 = new InsnList();
                            list2.add(new VarInsnNode(ALOAD, 0));
                            list2.add(new VarInsnNode(ALOAD, 0));
                            list2.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", ASMLoadingPlugin.isObf ? "a" : "gameProfile", "Lcom/mojang/authlib/GameProfile;"));
                            list2.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                            list2.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/NetworkPlayerInfoHook", "getPlayersFinals", "(Ljava/lang/String;)I", false));
                            list2.add(new FieldInsnNode(PUTFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", "playerFinalkills", "I"));
                            methodNode.instructions.insertBefore(nextNode, list2);
                            status.addInjection();
                        }

                        return classNode;
                    }
                }
            }
        }
        return classNode;
    }

}
