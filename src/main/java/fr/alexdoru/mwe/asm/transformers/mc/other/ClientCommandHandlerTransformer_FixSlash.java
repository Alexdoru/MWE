package fr.alexdoru.mwe.asm.transformers.mc.other;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class ClientCommandHandlerTransformer_FixSlash implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraftforge.client.ClientCommandHandler"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.COMMANDHANDLER$EXECUTECOMMAND)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("ClientCommandHandlerHook_FixSlash"),
                        "shouldCancel",
                        "(Ljava/lang/String;)Z",
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
    }

}
