package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class ScoreboardTransformer implements MWETransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.scoreboard.Scoreboard";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.SCOREBOARD$REMOVETEAM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD)) {
                        final int index = ((VarInsnNode) insnNode).var;
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkMethodInsnNode(secondNode, MethodMapping.MAP$REMOVE)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkInsnNode(thirdNode, POP)) {
                                /*
                                 * Injects after line 299 :
                                 * ScoreboardHook.removeTeamHook(player);
                                 */
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, index));
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ScoreboardHook"), "removeTeamHook", "(Ljava/lang/String;)V", false));
                                methodNode.instructions.insert(thirdNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.SCOREBOARD$ADDPLAYERTOTEAM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_1) && checkInsnNode(insnNode.getNext(), IRETURN)) {
                        /*
                         * Injects before line 329 :
                         * ScoreboardHook.addPlayerToTeamHook(player);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ScoreboardHook"), "addPlayerToTeamHook", "(Ljava/lang/String;)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.SCOREBOARD$REMOVEPLAYERFROMTEAM)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.COLLECTION$REMOVE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            /*
                             * Injects after line 360 :
                             * ScoreboardHook.removePlayerFromTeamHook(player);
                             */
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ScoreboardHook"), "removePlayerFromTeamHook", "(Ljava/lang/String;)V", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }

        }
    }

}
