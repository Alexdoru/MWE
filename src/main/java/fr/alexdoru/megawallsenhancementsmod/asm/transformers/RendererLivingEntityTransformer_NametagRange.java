package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RendererLivingEntityTransformer_NametagRange implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERERLIVINGENTITY$RENDERNAME)) {
                final InsnList list1 = new InsnList();
                list1.add(new VarInsnNode(ALOAD, 1));
                list1.add(new InsnNode(ICONST_0));
                list1.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_NametagRange"), "setRenderNametag", "(L" + ClassMapping.ENTITYLIVINGBASE + ";Z)V", false));
                methodNode.instructions.insert(list1);
                status.addInjection();
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GLSTATEMANAGER$ALPHAFUNC)) {
                        final InsnList list2 = new InsnList();
                        list2.add(new VarInsnNode(ALOAD, 1));
                        list2.add(new InsnNode(ICONST_1));
                        list2.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RendererLivingEntityHook_NametagRange"), "setRenderNametag", "(L" + ClassMapping.ENTITYLIVINGBASE + ";Z)V", false));
                        methodNode.instructions.insert(insnNode, list2);
                        status.addInjection();
                        break;
                    }
                }
            }
        }
    }

}
