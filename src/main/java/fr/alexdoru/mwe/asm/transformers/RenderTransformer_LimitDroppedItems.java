package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class RenderTransformer_LimitDroppedItems implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{
                "net.minecraft.client.renderer.RenderGlobal",
                "net.minecraft.client.renderer.entity.RenderEntityItem"
        };
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        if (ClassMapping.RENDERGLOBAL.name.equals(classNode.name)) {
            for (final MethodNode methodNode : classNode.methods) {
                if (checkMethodNode(methodNode, MethodMapping.RENDERGLOBAL$RENDERENTITIES)) {
                    for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                        if (checkInsnNode(insnNode, ICONST_0)) {
                            final AbstractInsnNode nextNode = insnNode.getNext();
                            if (checkFieldInsnNode(nextNode, PUTFIELD, FieldMapping.RENDERGLOBAL$COUNTENTITIESRENDERED)) {
                                methodNode.instructions.insert(nextNode, new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("RenderHook_LimitDroppedItems"),
                                        "resetEntityItemCount",
                                        "()V",
                                        false
                                ));
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        } else if (ClassMapping.RENDERENTITYITEM.name.equals(classNode.name)) {
            for (final MethodNode methodNode : classNode.methods) {
                if (checkMethodNode(methodNode, MethodMapping.RENDER$SHOULDRENDER)) {
                    MWELoadingPlugin.logger.error("RenderEntityItem is already overriding shouldRender() !");
                    return;
                }
            }
            final MethodNode mn = new MethodNode(ACC_PUBLIC, MethodMapping.RENDER$SHOULDRENDER.name, MethodMapping.RENDER$SHOULDRENDER.desc, null, null);
            classNode.methods.add(mn);
            final InsnList list = mn.instructions;
            final LabelNode l0 = new LabelNode();
            final LabelNode l1 = new LabelNode();
            final LabelNode l2 = new LabelNode();
            final LabelNode l3 = new LabelNode();
            list.add(l0);
            list.add(new VarInsnNode(ALOAD, 0));
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new VarInsnNode(ALOAD, 2));
            list.add(new VarInsnNode(DLOAD, 3));
            list.add(new VarInsnNode(DLOAD, 5));
            list.add(new VarInsnNode(DLOAD, 7));
            list.add(new MethodInsnNode(
                    INVOKESPECIAL,
                    ClassMapping.RENDER.name,
                    MethodMapping.RENDER$SHOULDRENDER.name,
                    MethodMapping.RENDER$SHOULDRENDER.desc,
                    false
            ));
            list.add(new JumpInsnNode(IFEQ, l1));
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new VarInsnNode(DLOAD, 3));
            list.add(new VarInsnNode(DLOAD, 5));
            list.add(new VarInsnNode(DLOAD, 7));
            list.add(new MethodInsnNode(
                    INVOKESTATIC,
                    getHookClass("RenderHook_LimitDroppedItems"),
                    "shouldRenderEntityItem",
                    "(L" + ClassMapping.ENTITY + ";DDD)Z",
                    false
            ));
            list.add(new JumpInsnNode(IFEQ, l1));
            list.add(new InsnNode(ICONST_1));
            list.add(new JumpInsnNode(GOTO, l2));
            list.add(l1);
            list.add(new FrameNode(F_SAME, 0, null, 0, null));
            list.add(new InsnNode(ICONST_0));
            list.add(l2);
            list.add(new FrameNode(F_SAME1, 0, null, 1, new Object[]{INTEGER}));
            list.add(new InsnNode(IRETURN));
            list.add(l3);
            mn.localVariables.add(new LocalVariableNode("this", "L" + ClassMapping.RENDERENTITYITEM + ";", null, l0, l3, 0));
            mn.localVariables.add(new LocalVariableNode("entityItem", "L" + ClassMapping.ENTITY + ";", null, l0, l3, 1));
            mn.localVariables.add(new LocalVariableNode("camera", "L" + ClassMapping.ICAMERA + ";", null, l0, l3, 2));
            mn.localVariables.add(new LocalVariableNode("camX", "D", null, l0, l3, 3));
            mn.localVariables.add(new LocalVariableNode("camY", "D", null, l0, l3, 5));
            mn.localVariables.add(new LocalVariableNode("camZ", "D", null, l0, l3, 7));
            mn.visitMaxs(9, 9);
            status.addInjection();
        }
    }

}
