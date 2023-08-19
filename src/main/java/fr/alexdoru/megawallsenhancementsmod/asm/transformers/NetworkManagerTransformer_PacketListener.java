package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class NetworkManagerTransformer_PacketListener implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.network.NetworkManager"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.NETWORKMANAGER$CHANNELREAD0)) {
                LabelNode targetNode = null;
                for (final TryCatchBlockNode tryCatchBlock : methodNode.tryCatchBlocks) {
                    if (tryCatchBlock.type.equals(ClassMapping.THREADQUICKEXITEXCEPTION.name)) {
                        targetNode = tryCatchBlock.handler;
                    }
                }
                if (targetNode != null) {
                    final InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 2));
                    list.add(new MethodInsnNode(
                            INVOKESTATIC,
                            getHookClass("NetworkManagerHook_PacketListener"),
                            "listen",
                            "(L" + ClassMapping.PACKET + ";)V",
                            false));
                    methodNode.instructions.insert(targetNode, list);
                    status.addInjection();
                }
            }
        }
    }

}
