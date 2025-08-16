package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityLivingBaseTransformer_RotationTracker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.EntityLivingBase"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITY$SETROTATIONYAWHEAD)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(FLOAD, 1));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("EntityLivingBaseHook_RotationTracker"),
                        "setRotationYawHead",
                        "(L" + ClassMapping.ENTITYLIVINGBASE + ";F)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
