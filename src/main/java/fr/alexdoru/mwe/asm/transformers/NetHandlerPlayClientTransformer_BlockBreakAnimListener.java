package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class NetHandlerPlayClientTransformer_BlockBreakAnimListener implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEBLOCKBREAKANIM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.WORLDCLIENT$SENDBLOCKBREAKPROGRESS)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETHANDLERPLAYCLIENT$GAMECONTROLLER));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$THEWORLD));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("NetHandlerPlayClientHook_BlockBreakAnim"),
                                "handleBlockBreakAnim",
                                "(L" + ClassMapping.WORLDCLIENT + ";L" + ClassMapping.S25PACKETBLOCKBREAKANIM + ";)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
