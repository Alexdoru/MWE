package fr.alexdoru.mwe.asm.transformers.mc.other;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CommandHandlerTransformer_FixCaseCommand implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{
                "net.minecraft.command.CommandHandler",
                "net.minecraftforge.client.ClientCommandHandler"
        };
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        if (ClassMapping.CLIENTCOMMANDHANDLER.name.equals(classNode.name)) {
            status.setInjectionPoints(1);
        } else {
            status.setInjectionPoints(4);
        }
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.COMMANDHANDLER$GETTABCOMPLETIONOPTION) ||
                    checkMethodNode(methodNode, MethodMapping.COMMANDHANDLER$EXECUTECOMMAND)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (isSplitMethod(insnNode)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("CommandHandlerHook_FixCaseCommand"),
                                "putToLowerCase",
                                "([Ljava/lang/String;)[Ljava/lang/String;",
                                false
                        ));
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.COMMANDHANDLER$REGISTERCOMMAND)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 1)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkMethodInsnNode(nextNode, MethodMapping.MAP$PUT)) {
                            methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("CommandHandlerHook_FixCaseCommand"),
                                    "putToLowerCase",
                                    "(Ljava/lang/String;)Ljava/lang/String;",
                                    false
                            ));
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

    private static boolean isSplitMethod(AbstractInsnNode insnNode) {
        return insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL
                && ((MethodInsnNode) insnNode).owner.equals("java/lang/String")
                && ((MethodInsnNode) insnNode).name.equals("split")
                && ((MethodInsnNode) insnNode).desc.endsWith(")[Ljava/lang/String;");
    }

}
