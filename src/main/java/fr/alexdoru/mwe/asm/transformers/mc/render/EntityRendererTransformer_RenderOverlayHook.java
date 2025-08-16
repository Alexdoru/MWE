package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityRendererTransformer_RenderOverlayHook implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.EntityRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYRENDERER$UPDATECAMERAANDRENDER)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GUIINGAME$RENDERGAMEOVERLAY)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(FLOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("EntityRendererhook_RenderOverlay"),
                                "onPostRenderGameOverlay",
                                "(F)V",
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
