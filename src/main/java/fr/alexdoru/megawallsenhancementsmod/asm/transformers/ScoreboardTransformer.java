package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class ScoreboardTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.scoreboard.Scoreboard";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(2);

        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.ADDPLAYERTOTEAM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_1) && checkInsnNode(insnNode.getNext(), IRETURN)) {
                        /*
                         * Injects before line 329 :
                         * ScoreboardHook.transformNameTablist(player);
                         */
                        methodNode.instructions.insertBefore(insnNode, getInsnList());
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.REMOVEPLAYERFROMTEAM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.COLLECTION$REMOVE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            /*
                             * Injects after line 360 :
                             * ScoreboardHook.transformNameTablist(player);
                             */
                            methodNode.instructions.insert(nextNode, getInsnList());
                            status.addInjection();
                        }
                    }
                }
            }

        }
        return classNode;
    }

    private InsnList getInsnList() {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ScoreboardHook"), "transformNameTablist", "(Ljava/lang/String;)V", false));
        return list;
    }

}
