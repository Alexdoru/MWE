package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RenderGlobalTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.RenderGlobal";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(2);

        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderEntities") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lpk;Lbia;F)V" : "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V")) {
                int ordinal = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == ICONST_0) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof FieldInsnNode && nextNode.getOpcode() == PUTFIELD
                                && ((FieldInsnNode) nextNode).owner.equals(ASMLoadingPlugin.isObf ? "bfr" : "net/minecraft/client/renderer/RenderGlobal")
                                && ((FieldInsnNode) nextNode).name.equals(ASMLoadingPlugin.isObf ? "S" : "countEntitiesRendered")
                                && ((FieldInsnNode) nextNode).desc.equals("I")) {
                            /*
                             * Injects after line 565 :
                             * RenderGlobalHook.resetEntityItemCount();
                             */
                            methodNode.instructions.insertBefore(nextNode.getNext(), new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderGlobalHook", "resetEntityItemCount", "()V", false));
                            status.addInjection();
                        }
                    }

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "biu" : "net/minecraft/client/renderer/entity/RenderManager")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "renderEntitySimple")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Lpk;F)Z" : "(Lnet/minecraft/entity/Entity;F)Z")) {
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
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderGlobalHook", "renderEntitySimple", ASMLoadingPlugin.isObf ? "(Lbiu;Lpk;FDDD)V" : "(Lnet/minecraft/client/renderer/entity/RenderManager;Lnet/minecraft/entity/Entity;FDDD)V", false));
                            methodNode.instructions.insertBefore(insnNode, list);
                            methodNode.instructions.remove(insnNode.getNext()); // remove POP
                            methodNode.instructions.remove(insnNode); // remove INVOKEVIRTUAL RenderManager.renderEntitySimple (Lnet/minecraft/entity/Entity;F)Z
                            status.addInjection();
                        }
                    }

                }
            }
        }
        return classNode;
    }

}
