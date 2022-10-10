package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class CommandHandlerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.command.CommandHandler";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "getTabCompletionOptions") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lm;Ljava/lang/String;Lcj;)Ljava/util/List;" : "(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;Lnet/minecraft/util/BlockPos;)Ljava/util/List;")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof InsnNode && insnNode.getOpcode() == ICONST_0) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof InsnNode && nextNode.getOpcode() == AALOAD) {
                            /*
                             * Replaces line 169 :
                             * String s = astring[0];
                             * With :
                             * String s = CommandHandlerHook.putToLowerCase(astring[0]);
                             */
                            methodNode.instructions.insert(nextNode, new MethodInsnNode(INVOKESTATIC, getHookClass("CommandHandlerHook"), "putToLowerCase", "(Ljava/lang/String;)Ljava/lang/String;", false));
                            status.addInjection();
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
