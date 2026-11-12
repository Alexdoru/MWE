package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class LayerArmorBaseTransformer_LeatherArmor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArmorBase"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.LAYERARMORBASE$GETCURRENTARMOR)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ARETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("mc/render/LayerArmorBaseHook_LeatherArmor"),
                                "getArmorToRender",
                                "(" + ClassMapping.ITEMSTACK.desc() + ClassMapping.ENTITYLIVINGBASE.desc() + ")" + ClassMapping.ITEMSTACK.desc(),
                                false
                        ));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
