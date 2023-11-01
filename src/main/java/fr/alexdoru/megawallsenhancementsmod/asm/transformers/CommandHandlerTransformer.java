package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CommandHandlerTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.command.CommandHandler"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
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
    }

}
