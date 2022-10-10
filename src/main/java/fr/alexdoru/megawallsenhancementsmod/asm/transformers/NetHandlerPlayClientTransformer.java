package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetHandlerPlayClientTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetHandlerPlayClient";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);

        for (final MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals("<init>") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lave;Laxu;Lek;Lcom/mojang/authlib/GameProfile;)V" : "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiScreen;Lnet/minecraft/network/NetworkManager;Lcom/mojang/authlib/GameProfile;)V")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == PUTFIELD && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "bcy" : "net/minecraft/client/network/NetHandlerPlayClient")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "i" : "playerInfoMap")
                            && ((FieldInsnNode) insnNode).desc.equals("Ljava/util/Map;")) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "clearPlayerMap", "()V", false));
                        status.addInjection();
                    }
                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "handlePlayerListItem") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lgz;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem;)V")) {

                AbstractInsnNode targetNodeRemoveInjection = null;
                AbstractInsnNode targetNodePutInjection = null;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("remove") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == POP) {
                            targetNodeRemoveInjection = nextNode;
                        }
                    }

                    if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("put") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == POP) {
                            targetNodePutInjection = nextNode.getNext();
                        }
                    }

                }

                if (targetNodeRemoveInjection != null && targetNodePutInjection != null) {
                    /*
                     * Replace line 1628 :
                     * this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                     * With :
                     * NetHandlerPlayClientHook.removePlayerFromMap(this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId()));
                     */
                    final InsnList listRemove = new InsnList();
                    listRemove.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "removePlayerFromMap", "(Ljava/lang/Object;)V", false));
                    methodNode.instructions.insertBefore(targetNodeRemoveInjection, listRemove);
                    methodNode.instructions.remove(targetNodeRemoveInjection);
                    status.addInjection();
                    /*
                     * Injects after line 1637 :
                     * NetHandlerPlayClientHook.putPlayerInMap(networkplayerinfo.getGameProfile().getName(), networkplayerinfo);
                     */
                    final InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", ASMLoadingPlugin.isObf ? "a" : "getGameProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "putPlayerInMap", ASMLoadingPlugin.isObf ? "(Ljava/lang/String;Lbdc;)V" : "(Ljava/lang/String;Lnet/minecraft/client/network/NetworkPlayerInfo;)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);
                    status.addInjection();
                }
            }

        }
        return classNode;
    }

}
