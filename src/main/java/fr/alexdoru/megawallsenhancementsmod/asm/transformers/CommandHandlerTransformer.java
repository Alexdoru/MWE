package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

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
            if (checkMethodNode(methodNode, MethodMapping.GETTABCOMPLETIONOPTION)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_0)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, AALOAD)) {
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
