package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class ScoreboardTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.scoreboard.Scoreboard";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "addPlayerToTeam") && methodNode.desc.equals("(Ljava/lang/String;Ljava/lang/String;)Z")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ICONST_1 && insnNode instanceof InsnNode) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == IRETURN) {
                            /*
                             * Injects before line 329 :
                             * ScoreboardHook.transformNameTablist(player);
                             */
                            methodNode.instructions.insertBefore(insnNode, getInsnList());
                        }
                    }
                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "removePlayerFromTeam") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Ljava/lang/String;Laul;)V" : "(Ljava/lang/String;Lnet/minecraft/scoreboard/ScorePlayerTeam;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKEINTERFACE && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals("java/util/Collection")
                            && ((MethodInsnNode) insnNode).name.equals("remove")
                            && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/Object;)Z")) {
                        AbstractInsnNode secondNode = insnNode.getNext();
                        if (secondNode != null && secondNode.getOpcode() == POP) {
                            AbstractInsnNode thirdNode = secondNode.getNext();
                            if (thirdNode != null) {
                                /*
                                 * Injects after line 360 :
                                 * ScoreboardHook.transformNameTablist(player);
                                 */
                                methodNode.instructions.insertBefore(thirdNode, getInsnList());
                            }
                        }
                    }
                }
            }

        }
        return classNode;
    }

    private InsnList getInsnList() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(
                INVOKESTATIC,
                "fr/alexdoru/megawallsenhancementsmod/asm/hooks/ScoreboardHook",
                "transformNameTablist",
                "(Ljava/lang/String;)V",
                false
        ));
        return list;
    }

}
