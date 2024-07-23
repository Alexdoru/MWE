package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class NetHandlerPlayClientTransformer_PlayerMapTracker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {

            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("NetHandlerPlayClientHook_PlayerMapTracker"),
                                "clearPlayerMap",
                                "()V",
                                false
                        ));
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM)) {

                AbstractInsnNode targetRemoveNode = null;
                AbstractInsnNode targetPutNode = null;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$REMOVE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            targetRemoveNode = nextNode;
                        }
                    }
                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$PUT)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            targetPutNode = nextNode.getNext();
                        }
                    }
                }

                if (targetRemoveNode != null && targetPutNode != null) {

                    methodNode.instructions.insertBefore(targetRemoveNode, new MethodInsnNode(
                            INVOKESTATIC,
                            getHookClass("NetHandlerPlayClientHook_PlayerMapTracker"),
                            "removePlayerFromMap",
                            "(Ljava/lang/Object;)Ljava/lang/Object;",
                            false
                    ));
                    status.addInjection();

                    final InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(
                            INVOKESTATIC,
                            getHookClass("NetHandlerPlayClientHook_PlayerMapTracker"),
                            "putPlayerInMap",
                            "(L" + ClassMapping.NETWORKPLAYERINFO + ";)V",
                            false
                    ));
                    methodNode.instructions.insertBefore(targetPutNode, listPut);
                    status.addInjection();

                }
            }

        }
    }

}
