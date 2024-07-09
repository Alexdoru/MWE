package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class RenderGlobalTransformer_LimitDroppedItems implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERGLOBAL$RENDERENTITIES)) {
                int ordinal = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_0)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkFieldInsnNode(nextNode, PUTFIELD, FieldMapping.RENDERGLOBAL$COUNTENTITIESRENDERED)) {
                            /*
                             * Injects after line 565 :
                             * RenderGlobalHook.resetEntityItemCount();
                             */
                            methodNode.instructions.insert(nextNode, new MethodInsnNode(INVOKESTATIC, getHookClass("RenderGlobalHook"), "resetEntityItemCount", "()V", false));
                            status.addInjection();
                        }
                    } else if (checkMethodInsnNode(insnNode, MethodMapping.RENDERMANAGER$RENDERENTITYSIMPLE)) {
                        ordinal++;
                        /* Replace line 672 :
                         * this.renderManager.renderEntitySimple(entity2, partialTicks);
                         * With :
                         * RenderGlobalHook.renderEntitySimple(this.renderManager, entity2, partialTicks);
                         */
                        if (ordinal == 3) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(DLOAD, 5));
                            list.add(new VarInsnNode(DLOAD, 7));
                            list.add(new VarInsnNode(DLOAD, 9));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderGlobalHook"), "renderEntitySimple", "(L" + ClassMapping.RENDERMANAGER + ";L" + ClassMapping.ENTITY + ";FDDD)V", false));
                            methodNode.instructions.insertBefore(insnNode, list);
                            methodNode.instructions.remove(insnNode.getNext()); // remove POP
                            methodNode.instructions.remove(insnNode); // remove INVOKEVIRTUAL RenderManager.renderEntitySimple (Lnet/minecraft/entity/Entity;F)Z
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
