package fr.alexdoru.mwe.asm.transformers.externalmods;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import org.objectweb.asm.tree.*;

// Fix patcher's EntityCulling not rendering the outlines of culled entities
public class EntityCullingTransformer_FixOutlineCulling implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"club.sk1er.patcher.util.world.render.culling.EntityCulling"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("checkEntity") && methodNode.desc.equals("(Lnet/minecraft/entity/Entity;)Z")) {
                if (needsFixing(methodNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("checkEntity") && methodNode.desc.equals("(Lnet/minecraft/entity/Entity;)Z")) {
                if (needsFixing(methodNode)) {
                    injectFix(status, methodNode);
                }
            }
        }
    }

    private boolean needsFixing(MethodNode methodNode) {
        // this is here for future compat in case they ever fix the bug themselves
        // looks for
        // INVOKEINTERFACE ......./RenderManagerAccessor...renderoutlines()Z (itf)
        for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (insnNode instanceof MethodInsnNode) {
                final MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                if (methodInsnNode.getOpcode() == INVOKEINTERFACE
                        && methodInsnNode.owner.endsWith("RenderManagerAccessor")
                        && methodInsnNode.name.toLowerCase().contains("renderoutlines")
                        && methodInsnNode.desc.equals("()Z")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void injectFix(InjectionStatus status, MethodNode methodNode) {
        final InsnList list = new InsnList();
        list.add(new MethodInsnNode(
                INVOKESTATIC,
                getHookClass("RenderGlobalHook_EntityOutlines"),
                "isRenderOutlines",
                "()Z",
                false
        ));
        final LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, label));
        list.add(new InsnNode(ICONST_0));
        list.add(new InsnNode(IRETURN));
        list.add(label);
        list.add(new FrameNode(F_SAME, 0, null, 0, null));
        methodNode.instructions.insert(list);
        status.addInjection();
    }

}
