package fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class SidebarmodReloaded_CustomSidebarTransformer implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "fr.alexdoru.sidebarmod.gui.CustomSidebar";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("drawSidebar")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals("net/minecraft/scoreboard/ScorePlayerTeam")
                            && ((MethodInsnNode) insnNode).name.equals("func_96667_a")
                            && ((MethodInsnNode) insnNode).desc.equals("(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkVarInsnNode(nextNode, ASTORE, 15)) {
                            /*
                            Original line : String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                            After transformation : String s1 = GuiIngameHook.getSidebarTextLine(ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName()), j);
                             */
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ILOAD, 9));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameHook"), "getSidebarTextLine", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
        return classNode;
    }
}
