package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RenderGlobalTransformer_ListenDestroyedBlocks implements MWETransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.RenderGlobal";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERGLOBAL$PLAYAUXSFX)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.EFFECTRENDERER$ADDBLOCKDESTROYEFFECTS)) {
                        final InsnList list = new InsnList();
                        list.add(new InsnNode(DUP));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderGlobalHook"), "listenDestroyedBlocks", "(L" + ClassMapping.IBLOCKSTATE + ";L" + ClassMapping.BLOCKPOS + ";)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
