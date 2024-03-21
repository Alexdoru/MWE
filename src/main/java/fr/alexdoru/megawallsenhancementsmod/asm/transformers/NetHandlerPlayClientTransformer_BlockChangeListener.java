package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class NetHandlerPlayClientTransformer_BlockChangeListener implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            final boolean isBlockChange = checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEBLOCKCHANGE);
            final boolean isMultiBlockChange = checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEMULTIBLOCKCHANGE);
            if (isBlockChange || isMultiBlockChange) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.PACKETTHREADUTIL$CHECKTHREADANDENQUEUE)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("NetHandlerPlayClientHook_BlockChangeListener"),
                                isBlockChange ? "onBlockChange" : "onMultiBlockChange",
                                "(L" + (isBlockChange ? ClassMapping.S23PACKETBLOCKCHANGE : ClassMapping.S22PACKETMULTIBLOCKCHANGE) + ";)V",
                                false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                        break;
                    }
                }
            }
        }
    }

}
