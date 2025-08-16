package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class RendererLivingEntityTransformer_ColorOutlines implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERERLIVINGENTITY$SETSCORETEAMCOLOR)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkLdcInsnNode(insnNode, 16777215)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("RendererLivingEntityHook_ColorOutlines"),
                                "getEntityOutlineColor",
                                "(IL" + ClassMapping.ENTITYLIVINGBASE + ";)I",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }

    }

}
