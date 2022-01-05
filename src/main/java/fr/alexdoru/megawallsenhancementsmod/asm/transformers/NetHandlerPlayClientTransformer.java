package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
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

            if (methodNode.name.equals(MixinLoader.isObf ? "a" : "handlePlayerListItem") && methodNode.desc.equals(MixinLoader.isObf ? "(Lgz;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem;)V")) {

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
                    InsnList listRemove = new InsnList();
                    listRemove.add(new VarInsnNode(ALOAD, 3));
                    listRemove.add(new MethodInsnNode(INVOKEVIRTUAL, MixinLoader.isObf ? "gz$b" : "net/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData", MixinLoader.isObf ? "a" : "getProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                    listRemove.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listRemove.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "removePlayerFromMap", MixinLoader.isObf ? "(Ljava/lang/String;)Lbdc;" : "(Ljava/lang/String;)Lnet/minecraft/client/network/NetworkPlayerInfo;", false));
                    listRemove.add(new InsnNode(POP));
                    methodNode.instructions.insertBefore(targetNodeRemoveInjection, listRemove);

                    InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", MixinLoader.isObf ? "a" : "getGameProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                    listPut.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "putPlayerInMap", MixinLoader.isObf ? "(Ljava/lang/String;Lbdc;)V" : "(Ljava/lang/String;Lnet/minecraft/client/network/NetworkPlayerInfo;)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);

                    MixinLoader.logger.info("Injected mirror playerInfoMap");
                }
            }

        }
        return classNode;
    }

    /*
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S38PacketPlayerListItem.AddPlayerData s38packetplayerlistitem$addplayerdata : packetIn.func_179767_a())
        {
            if (packetIn.func_179768_b() == S38PacketPlayerListItem.Action.REMOVE_PLAYER)
            {
                this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                NameUtil.removePlayerFromMap(s38packetplayerlistitem$addplayerdata.getProfile().getName()); // ADDED VIA ASM
            }
            else
            {
                NetworkPlayerInfo networkplayerinfo = (NetworkPlayerInfo)this.playerInfoMap.get(s38packetplayerlistitem$addplayerdata.getProfile().getId());

                if (packetIn.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER)
                {
                    networkplayerinfo = new NetworkPlayerInfo(s38packetplayerlistitem$addplayerdata);
                    this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
                    NameUtil.putPlayerInMap(networkplayerinfo.getGameProfile().getName(), networkplayerinfo); // ADDED VIA ASM
                }

                if (networkplayerinfo != null)
                {
                    switch (packetIn.func_179768_b())
                    {
                        case ADD_PLAYER:
                            networkplayerinfo.setGameType(s38packetplayerlistitem$addplayerdata.getGameMode());
                            networkplayerinfo.setResponseTime(s38packetplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_GAME_MODE:
                            networkplayerinfo.setGameType(s38packetplayerlistitem$addplayerdata.getGameMode());
                            break;
                        case UPDATE_LATENCY:
                            networkplayerinfo.setResponseTime(s38packetplayerlistitem$addplayerdata.getPing());
                            break;
                        case UPDATE_DISPLAY_NAME:
                            networkplayerinfo.setDisplayName(s38packetplayerlistitem$addplayerdata.getDisplayName());
                    }
                }
            }
        }
    }
    */

}
