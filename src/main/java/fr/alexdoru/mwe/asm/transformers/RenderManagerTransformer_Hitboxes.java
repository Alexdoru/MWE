package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class RenderManagerTransformer_Hitboxes implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderManager"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(11);
        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.RENDERMANAGER$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_0)) {
                        if (checkFieldInsnNode(insnNode.getNext(), PUTFIELD, FieldMapping.RENDERMANAGER$DEBUGBOUNDINGBOX)) {
                            methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("RenderManagerHook_Hitboxes"),
                                    "shouldToggleOnStart",
                                    "(Z)Z",
                                    false
                            ));
                            status.addInjection();
                        }
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.RENDERMANAGER$RENDERDEBUGBOUNDINGBOX)) {
                // Injects at head :
                // if (!RenderManagerHook_Hitboxes.shouldRenderHitbox(entityIn)) {
                //     return;
                // }
                methodNode.instructions.insert(getShouldRenderInsnList());
                status.addInjection();

                int count255 = -1;
                AbstractInsnNode injectionStartForIf = null;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkVarInsnNode(insnNode, ALOAD, 12)) {
                        count255 = 0;
                        // Replaces line 451 :
                        // RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, 255, 255, 255, 255);
                        // With :
                        // RenderGlobal.drawOutlinedBoundingBox(
                        //              RenderManagerHook_Hitboxes.getAxisAlignedBB(axisalignedbb1, entityIn),
                        //              RenderManagerHook_Hitboxes.getRedHitboxColor(255, this.textRenderer, entityIn),
                        //              RenderManagerHook_Hitboxes.getGreenHitboxColor(255),
                        //              RenderManagerHook_Hitboxes.getBlueHitboxColor(255),
                        //              255
                        // );
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("RenderManagerHook_Hitboxes"),
                                "getAxisAlignedBB",
                                "(L" + ClassMapping.AXISALIGNEDBB + ";L" + ClassMapping.ENTITY + ";)L" + ClassMapping.AXISALIGNEDBB + ";",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (count255 > -1 && count255 < 3 && checkIntInsnNode(insnNode, SIPUSH, 255)) {
                        if (count255 == 0) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("RenderManagerHook_Hitboxes"),
                                    "getRedHitboxColor",
                                    "(IL" + ClassMapping.ENTITY + ";)I",
                                    false
                            ));
                            methodNode.instructions.insert(insnNode, list);
                            status.addInjection();
                        } else if (count255 == 1) {
                            methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("RenderManagerHook_Hitboxes"),
                                    "getGreenHitboxColor",
                                    "(I)I",
                                    false
                            ));
                            status.addInjection();
                        } else {
                            methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("RenderManagerHook_Hitboxes"),
                                    "getBlueHitboxColor",
                                    "(I)I",
                                    false
                            ));
                            status.addInjection();
                        }
                        count255++;
                    }

                    if (checkTypeInsnNode(insnNode, INSTANCEOF, ClassMapping.ENTITYLIVINGBASE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkJumpInsnNode(nextNode, IFEQ)) {
                            final LabelNode labelNode = ((JumpInsnNode) nextNode).label;
                            // Transforms line 453 :
                            // if (entityIn instanceof EntityLivingBase)
                            // Becomes :
                            // if (entityIn instanceof EntityLivingBase && ConfigHandler.drawRedBox)
                            final InsnList list = new InsnList();
                            list.add(new JumpInsnNode(IFEQ, labelNode));
                            list.add(getNewConfigFieldInsnNode("drawRedBox"));
                            methodNode.instructions.insert(insnNode, list);
                            status.addInjection();
                        }
                    }

                    if (checkLdcInsnNode(insnNode, new Double("2.0"))) {
                        // Line 464
                        // Replaces the 2.0D with RenderManagerHook.getBlueVectLength(2.0D);
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("RenderManagerHook_Hitboxes"),
                                "getBlueVectLength",
                                "(D)D",
                                false
                        ));
                        status.addInjection();
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.TESSELLATOR$GETINSTANCE)) {
                        injectionStartForIf = insnNode;
                    }

                    if (injectionStartForIf != null && checkMethodInsnNode(insnNode, MethodMapping.TESSELLATOR$DRAW)) {
                        // Surround the blue vector render code (7 lines of code) with an if statement
                        // if (shouldRenderBlueVect(entityIn)) {
                        //       Tessellator tessellator = Tessellator.getInstance();
                        //       .....
                        //       tessellator.draw();
                        // }
                        final InsnList list = new InsnList();
                        final LabelNode label = new LabelNode();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("RenderManagerHook_Hitboxes"),
                                "shouldRenderBlueVect",
                                "(L" + ClassMapping.ENTITY + ";)Z",
                                false
                        ));
                        list.add(new JumpInsnNode(IFEQ, label));
                        methodNode.instructions.insertBefore(injectionStartForIf, list);
                        methodNode.instructions.insert(insnNode, label);
                        status.addInjection();
                    }

                }
            }
        }
    }

    private InsnList getShouldRenderInsnList() {
        final InsnList list = new InsnList();
        final LabelNode label = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.RENDERMANAGER$LIVINGENTITY));
        list.add(new MethodInsnNode(INVOKESTATIC,
                getHookClass("RenderManagerHook_Hitboxes"),
                "shouldRenderHitbox",
                "(L" + ClassMapping.ENTITY + ";L" + ClassMapping.ENTITY + ";)Z",
                false
        ));
        list.add(new JumpInsnNode(IFNE, label));
        list.add(new InsnNode(RETURN));
        list.add(label);
        return list;
    }

}