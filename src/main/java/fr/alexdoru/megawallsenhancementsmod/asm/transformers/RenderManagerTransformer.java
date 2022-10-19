package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
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

        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.RENDERMANAGER$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_0) && checkFieldInsnNode(insnNode.getNext(), PUTFIELD, FieldMapping.RENDERMANAGER$DEBUGBOUNDINGBOX)) {
                        /*
                         * Replaces in the constructor :
                         * this.debugBoundingBox = false;
                         * With :
                         * this.debugBoundingBox = ConfigHandler.isDebugHitboxOn;
                         */
                        methodNode.instructions.insert(insnNode, getNewConfigFieldInsnNode("isDebugHitboxOn"));
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.RENDERDEBUGBOUNDINGBOX)) {
                /*
                 * Injects at head :
                 * if(RenderManagerHook.cancelHitboxRender(entityIn)) {
                 *     return;
                 * }
                 */
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getCancelRenderInsnList());
                status.addInjection();

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkVarInsnNode(insnNode, ALOAD, 12)) {
                        /*
                         * Replaces line 451 :
                         * RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, 255, 255, 255, 255);
                         * With :
                         * RenderGlobal.drawOutlinedBoundingBox(RenderManagerHook.getAxisAlignedBB(axisalignedbb1, entityIn), 255, 255, 255, 255);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderManagerHook"), "getAxisAlignedBB", "(L" + ClassMapping.AXISALIGNEDBB + ";L" + ClassMapping.ENTITY + ";)L" + ClassMapping.AXISALIGNEDBB + ";", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkTypeInsnNode(insnNode, INSTANCEOF, ClassMapping.ENTITYLIVINGBASE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkJumpInsnNode(nextNode, IFEQ)) {
                            final LabelNode labelNode = ((JumpInsnNode) nextNode).label;
                            /*
                             * Transforms line 453 :
                             * if (entityIn instanceof EntityLivingBase)
                             * Becomes :
                             * if (entityIn instanceof EntityLivingBase && ConfigHandler.drawRedBox)
                             */
                            final InsnList list = new InsnList();
                            list.add(new JumpInsnNode(IFEQ, labelNode));
                            list.add(getNewConfigFieldInsnNode("drawRedBox"));
                            methodNode.instructions.insert(insnNode, list);
                            status.addInjection();
                        }
                    }

                    if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst.equals(new Double("2.0"))) {
                        /*
                         * Line 464
                         * Replaces the 2.0D with RenderManagerHook.getBlueVectLength(entityIn);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1)); // load entity
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderManagerHook"), "getBlueVectLength", "(L" + ClassMapping.ENTITY + ";)D", false));
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
        final InsnList list = new InsnList();
        final LabelNode notCancelled = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 1)); // load entity
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderManagerHook"), "cancelHitboxRender", "(L" + ClassMapping.ENTITY + ";)Z", false)); // load the boolean
        list.add(new JumpInsnNode(IFEQ, notCancelled)); // if (true) { return;} else {jump to notCancelled label}
        list.add(new InsnNode(RETURN)); // return;
        list.add(notCancelled);
        return list;
    }

}