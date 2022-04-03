package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RenderManagerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderManager";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(7);

        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals("<init>") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lbmj;Lbjh;)V" : "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ICONST_0) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof FieldInsnNode && nextNode.getOpcode() == PUTFIELD
                                && ((FieldInsnNode) nextNode).owner.equals(ASMLoadingPlugin.isObf ? "biu" : "net/minecraft/client/renderer/entity/RenderManager")
                                && ((FieldInsnNode) nextNode).name.equals(ASMLoadingPlugin.isObf ? "t" : "debugBoundingBox")
                                && ((FieldInsnNode) nextNode).desc.equals("Z")) {
                            /*
                             * Replaces in the constructor :
                             * this.debugBoundingBox = false;
                             * With :
                             * this.debugBoundingBox = ConfigHandler.isDebugHitboxOn;
                             */
                            methodNode.instructions.insertBefore(nextNode, new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", "isDebugHitboxOn", "Z"));
                            methodNode.instructions.remove(insnNode);
                            status.addInjection();
                        }
                    }
                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "b" : "renderDebugBoundingBox") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lpk;DDDFF)V" : "(Lnet/minecraft/entity/Entity;DDDFF)V")) {

                /*
                 * Injects at head :
                 * if(RenderManagerHook.cancelHitboxRender(entityIn)) {
                 *     return;
                 * }
                 */
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getCancelRenderInsnList());
                status.addInjection();

                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == ALOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 12) {
                        /*
                         * Replaces line 451 :
                         * RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, 255, 255, 255, 255);
                         * With :
                         * RenderGlobal.drawOutlinedBoundingBox(RenderManagerHook.getAxisAlignedBB(axisalignedbb1, entityIn), 255, 255, 255, 255);
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderManagerHook", "getAxisAlignedBB", ASMLoadingPlugin.isObf ? "(Laug;Lpk;)Lnet/minecraft/util/AxisAlignedBB;" : "(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/AxisAlignedBB;", false));
                        methodNode.instructions.insertBefore(insnNode.getNext(), list);
                        status.addInjection();
                    }

                    if (insnNode.getOpcode() == INSTANCEOF && insnNode instanceof TypeInsnNode && ((TypeInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "pr" : "net/minecraft/entity/EntityLivingBase")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == IFEQ && nextNode instanceof JumpInsnNode) {
                            LabelNode labelNode = ((JumpInsnNode) nextNode).label;
                            /*
                             * Transforms line 453 :
                             * if (entityIn instanceof EntityLivingBase)
                             * Becomes :
                             * if (entityIn instanceof EntityLivingBase && ConfigHandler.drawRedBox)
                             */
                            InsnList list = new InsnList();
                            list.add(new JumpInsnNode(IFEQ, labelNode));
                            list.add(new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", "drawRedBox", "Z"));
                            methodNode.instructions.insertBefore(nextNode, list);
                            status.addInjection();
                        }

                    }

                    if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst.equals(new Double("2.0"))) {
                        /*
                         * Line 464
                         * Replaces the 2.0D with RenderManagerHook.getBlueVectLength(entityIn);
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1)); // load entity
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderManagerHook", "getBlueVectLength", ASMLoadingPlugin.isObf ? "(Lpk;)D" : "(Lnet/minecraft/entity/Entity;)D", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }
                }

            }
        }
        return classNode;
    }

    private InsnList getCancelRenderInsnList() {
        InsnList list = new InsnList();
        LabelNode notCancelled = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 1)); // load entity
        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderManagerHook", "cancelHitboxRender", ASMLoadingPlugin.isObf ? "(Lpk;)Z" : "(Lnet/minecraft/entity/Entity;)Z", false)); // load the boolean
        list.add(new JumpInsnNode(IFEQ, notCancelled)); // if (true) { return;} else {jump to notCancelled label}
        list.add(new InsnNode(RETURN)); // return;
        list.add(notCancelled);
        return list;
    }

}