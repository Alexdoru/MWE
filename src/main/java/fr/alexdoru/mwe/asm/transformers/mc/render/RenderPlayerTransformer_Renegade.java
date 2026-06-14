package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class RenderPlayerTransformer_Renegade implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderPlayer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYER$RENDEROFFSETLIVINGLABEL)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.SCOREOBJECTIVE$GETDISPLAYNAME)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkMethodInsnNode(secondNode, MethodMapping.STRINGBUILDER$APPEND_STRING)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkMethodInsnNode(thirdNode, MethodMapping.STRINGBUILDER$TOSTRING)) {
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 1));
                                list.add(new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("mc/render/RenderHook_Renegade"),
                                        "appendToScore",
                                        "(Ljava/lang/StringBuilder;L" + ClassMapping.ABSTRACTCLIENTPLAYER + ";)Ljava/lang/StringBuilder;",
                                        false
                                ));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
