package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetHandlerPlayClientTransformer_PlayerMapTracker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
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
                    methodNode.instructions.insertBefore(targetNodeRemoveInjection, new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "removePlayerFromMap", "(Ljava/lang/Object;)V", false));
                    methodNode.instructions.remove(targetNodeRemoveInjection);
                    status.addInjection();
                    /*
                     * Injects after line 1637 :
                     * NetHandlerPlayClientHook.putPlayerInMap(networkplayerinfo.getGameProfile().getName(), networkplayerinfo);
                     */
                    final InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "putPlayerInMap", "(L" + ClassMapping.NETWORKPLAYERINFO + ";)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);
                    status.addInjection();
                }
            }

        }
    }

}
