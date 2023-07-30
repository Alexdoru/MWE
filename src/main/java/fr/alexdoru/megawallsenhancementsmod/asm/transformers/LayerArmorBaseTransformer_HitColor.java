package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class LayerArmorBaseTransformer_HitColor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.LAYERARMORBASE$SHOULDCOMBINETEXTURES)) {
                final InsnList list = new InsnList();
                final LabelNode notCanceled = new LabelNode();
                list.add(getNewConfigFieldInsnNode("colorArmorWhenHurt"));
                list.add(new JumpInsnNode(IFEQ, notCanceled));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IRETURN));
                list.add(notCanceled);
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
