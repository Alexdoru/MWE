package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class LayerArmorBaseTransformer_HitColor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
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
