package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetHandlerPlayClientTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetHandlerPlayClient";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {

        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "handlePlayerListItem") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lgz;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem;)V")) {

                AbstractInsnNode targetNodeRemoveInjection = null;
                AbstractInsnNode targetNodePutInjection = null;

                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("remove") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == POP) {
                            targetNodeRemoveInjection = nextNode.getNext();
                        }
                    }

                    if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("put") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == POP) {
                            targetNodePutInjection = nextNode.getNext();
                        }
                    }

                }

                if (targetNodeRemoveInjection != null && targetNodePutInjection != null) {
                    /*
                     * Injects after line 1628 :
                     * NameUtil.removePlayerFromMap(s38packetplayerlistitem$addplayerdata.getProfile().getName());
                     */
                    InsnList listRemove = new InsnList();
                    listRemove.add(new VarInsnNode(ALOAD, 3));
                    listRemove.add(new MethodInsnNode(INVOKEVIRTUAL, ASMLoadingPlugin.isObf ? "gz$b" : "net/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData", ASMLoadingPlugin.isObf ? "a" : "getProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                    listRemove.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listRemove.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "removePlayerFromMap", ASMLoadingPlugin.isObf ? "(Ljava/lang/String;)Lbdc;" : "(Ljava/lang/String;)Lnet/minecraft/client/network/NetworkPlayerInfo;", false));
                    listRemove.add(new InsnNode(POP));
                    methodNode.instructions.insertBefore(targetNodeRemoveInjection, listRemove);
                    /*
                     * Injects after line 1637 :
                     * NameUtil.putPlayerInMap(networkplayerinfo.getGameProfile().getName(), networkplayerinfo);
                     */
                    InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", ASMLoadingPlugin.isObf ? "a" : "getGameProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "putPlayerInMap", ASMLoadingPlugin.isObf ? "(Ljava/lang/String;Lbdc;)V" : "(Ljava/lang/String;Lnet/minecraft/client/network/NetworkPlayerInfo;)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);
                    return classNode;
                }
            }

        }
        return classNode;
    }

}
