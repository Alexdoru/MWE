package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class RenderGlobalTransformer_ListenDestroyedBlocks implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERGLOBAL$PLAYAUXSFX)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.EFFECTRENDERER$ADDBLOCKDESTROYEFFECTS)) {
                        final InsnList list = new InsnList();
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("RenderGlobalHook_ListenDestroyedBlocks"),
                                "listenDestroyedBlocks",
                                "(L" + ClassMapping.IBLOCKSTATE + ";L" + ClassMapping.BLOCKPOS + ";)V",
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
