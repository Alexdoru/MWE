package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class RendererLivingEntityTransformer_Renegade implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERERLIVINGENTITY$RENDERNAME)) {
                for (final AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(node, MethodMapping.ENTITYLIVINGBASE$GETDISPLAYNAME)) {
                        final AbstractInsnNode nextNode = node.getNext();
                        if (checkMethodInsnNode(nextNode, MethodMapping.ICHATCOMPONENT$GETFORMATTEDTEXT)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new VarInsnNode(DLOAD, 8));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("mc/render/RenderHook_Renegade"),
                                    "appendToNametag",
                                    "(Ljava/lang/String;L" + ClassMapping.ENTITYLIVINGBASE + ";D)Ljava/lang/String;",
                                    false
                            ));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
