package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class EntityPlayerSPTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.entity.EntityPlayerSP";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYPLAYERSP$SENDCHATMESSAGE)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("EntityPlayerSPHook"), "onMessageSent", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
