package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetHandlerPlayClientTransformer_TeleportListener implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetHandlerPlayClient";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEENTITYTELEPORT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.ENTITY$ONGROUND)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "onEntityTeleport", "(L" + ClassMapping.ENTITY + ";)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

}
