package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class EntityOtherPlayerMPTransformer_PositionTracker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.entity.EntityOtherPlayerMP"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITY$SETPOSITIONANDROTATION2)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(DLOAD, 1));
                list.add(new VarInsnNode(DLOAD, 3));
                list.add(new VarInsnNode(DLOAD, 5));
                list.add(new VarInsnNode(FLOAD, 7));
                list.add(new VarInsnNode(FLOAD, 8));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("EntityOtherPlayerMPHook_PositionTracker"),
                        "setPositionAndRotation",
                        "(L" + ClassMapping.ENTITYOTHERPLAYERMP + ";DDDFF)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
