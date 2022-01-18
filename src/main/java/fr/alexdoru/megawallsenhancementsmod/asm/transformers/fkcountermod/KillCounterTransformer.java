package fr.alexdoru.megawallsenhancementsmod.asm.transformers.fkcountermod;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class KillCounterTransformer implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "fr.alexdoru.fkcountermod.events.KillCounter";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("updateNetworkPlayerinfo")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ILOAD && ((VarInsnNode) insnNode).var == 1) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == ISTORE && ((VarInsnNode) nextNode).var == 3) {
                            methodNode.instructions.insertBefore(insnNode, new VarInsnNode(ALOAD, 2)); // Loads networkPlayerInfo
                            // already there : ILOAD 1 // ILOAD finals
                            methodNode.instructions.insertBefore(nextNode, new FieldInsnNode(PUTFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", "playerFinalkills", "I"));
                            methodNode.instructions.remove(nextNode);
                            ASMLoadingPlugin.logger.info("Transformed KillCounter");
                        }
                    }
                }
            }
        }
        return classNode;
    }
}
