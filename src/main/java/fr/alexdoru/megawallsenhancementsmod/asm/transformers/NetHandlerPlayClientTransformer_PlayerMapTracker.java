package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetHandlerPlayClientTransformer_PlayerMapTracker implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetHandlerPlayClient";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);

        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.NETHANDLERPLAYCLIENT$PLAYERINFOMAP)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "clearPlayerMap", "()V", false));
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM)) {

                AbstractInsnNode targetNodeRemoveInjection = null;
                AbstractInsnNode targetNodePutInjection = null;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$REMOVE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            targetNodeRemoveInjection = nextNode;
                        }
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$PUT)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
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
                    listPut.add(getNewMethodInsnNode(MethodMapping.NETWORKPLAYERINFO$GETGAMEPROFILE));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "putPlayerInMap", "(Ljava/lang/String;L" + ClassMapping.NETWORKPLAYERINFO + ";)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);
                    status.addInjection();
                }
            }

        }
    }

}
