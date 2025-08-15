package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class RenderGlobalTransformer_EntityOutlines implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(2);
        MethodNode targetMethod1 = null;
        MethodNode targetMethod2 = null;
        AbstractInsnNode targetInsn1 = null;
        AbstractInsnNode targetInsn2 = null;
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERGLOBAL$RENDERENTITIES)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.RENDERGLOBAL$ISRENDERENTITYOUTLINES)) {
                        targetMethod1 = methodNode;
                        targetInsn1 = insnNode;
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.RENDGERGLOBAL$RENDERENTITYOUTLINEFRAMEBUFFER)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.RENDERGLOBAL$ISRENDERENTITYOUTLINES)) {
                        targetMethod2 = methodNode;
                        targetInsn2 = insnNode;
                    }
                }
            }
        }

        if (targetInsn1 != null && targetInsn2 != null) {
            targetMethod1.instructions.insert(targetInsn1, getRenderWitherOutlineInsnList());
            status.addInjection();
            targetMethod2.instructions.insert(targetInsn2, new MethodInsnNode(
                    INVOKESTATIC,
                    getHookClass("RenderGlobalHook_EntityOutlines"),
                    "shouldDoFinalDraw",
                    "(Z)Z",
                    false
            ));
            status.addInjection();
        }

    }

    private InsnList getRenderWitherOutlineInsnList() {
        final InsnList list = new InsnList();
        // Z is already loaded on the stack
        list.add(new VarInsnNode(ALOAD, 1)); // Entity renderViewEntity
        list.add(new VarInsnNode(ALOAD, 2)); // ICamera camera
        list.add(new VarInsnNode(FLOAD, 3)); // float partialTicks
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.RENDERGLOBAL$ENTITYOUTLINEFRAMEBUFFER)); // this.entityOutlineFramebuffer
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.RENDERGLOBAL$ENTITYOUTLINESHADER)); // this.entityOutlineShader
        list.add(new MethodInsnNode(
                INVOKESTATIC,
                getHookClass("RenderGlobalHook_EntityOutlines"),
                "renderWitherOutline",
                "(Z" +
                        "L" + ClassMapping.ENTITY + ";" +
                        "L" + ClassMapping.ICAMERA + ";" +
                        "F" +
                        "L" + ClassMapping.FRAMEBUFFER + ";" +
                        "L" + ClassMapping.SHADERGROUP + ";" +
                        ")Z",
                false
        ));
        return list;
    }

}
