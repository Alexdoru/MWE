package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;


public class NetHandlerPlayClientTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode methodNode : classNode.methods) {

                if (methodNode.name.equals(MixinLoader.isObf ? "a" : "handlePlayerListItem") && methodNode.desc.equals(MixinLoader.isObf ? "(Lgz;)V" : "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem;)V")) {

                    MixinLoader.logger.info("------------ found method handlePlayerListItem");

                    for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                        if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("remove") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
                            AbstractInsnNode nextNode = insnNode.getNext();
                            if (nextNode.getOpcode() == POP) {
                                AbstractInsnNode targetNode = nextNode.getNext();
                                if (targetNode != null) {
                                    MixinLoader.logger.info("------------ found remove call");
                                    InsnList list = new InsnList();
                                    list.add(new VarInsnNode(ALOAD, 3)); // TODO Add obfuscated names
                                    list.add(new MethodInsnNode(INVOKEVIRTUAL, MixinLoader.isObf ? "gz$b" : "net/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData", MixinLoader.isObf ? "a" : "getProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                                    list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "removePlayerFromMap", MixinLoader.isObf ? "(Ljava/lang/String;)Lbdc;" : "(Ljava/lang/String;)Lnet/minecraft/client/network/NetworkPlayerInfo;", false));
                                    list.add(new InsnNode(POP));
                                    methodNode.instructions.insertBefore(targetNode, list);
                                    MixinLoader.logger.info("------------ injected remove call");
                                    continue;
                                }
                            }
                        }

                        if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals("put") && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")) {
                            AbstractInsnNode nextNode = insnNode.getNext();
                            if (nextNode.getOpcode() == POP) {
                                AbstractInsnNode targetNode = nextNode.getNext();
                                if (targetNode != null) {
                                    MixinLoader.logger.info("------------ found put call");
                                    InsnList list = new InsnList();
                                    list.add(new VarInsnNode(ALOAD, 4)); // TODO Add obfuscated names
                                    list.add(new MethodInsnNode(INVOKEVIRTUAL, MixinLoader.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", MixinLoader.isObf ? "a" : "getGameProfile", "()Lcom/mojang/authlib/GameProfile;", false));
                                    list.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                                    list.add(new VarInsnNode(ALOAD, 4));
                                    list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/NameUtil", "putPlayerInMap", MixinLoader.isObf ? "(Ljava/lang/String;Lbdc;)V" : "(Ljava/lang/String;Lnet/minecraft/client/network/NetworkPlayerInfo;)V", false));
                                    methodNode.instructions.insertBefore(targetNode, list);
                                    MixinLoader.logger.info("------------ injected put call");
                                }
                            }
                        }

                    }

                }

            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
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
