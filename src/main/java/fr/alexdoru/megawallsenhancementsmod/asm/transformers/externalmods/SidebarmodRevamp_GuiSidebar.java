package fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class SidebarmodRevamp_GuiSidebar implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "revamp.sidebarmod.gui.GuiSidebar";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("drawSidebar")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals("net/minecraft/scoreboard/ScorePlayerTeam")
                            && ((MethodInsnNode) insnNode).name.equals("func_96667_a")
                            && ((MethodInsnNode) insnNode).desc.equals("(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == ASTORE && nextNode instanceof VarInsnNode && ((VarInsnNode) nextNode).var == 13) {
                            /*
                            Original line : String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                            After transformation : String s1 = GuiIngameHook.getSidebarTextLine(ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName()), j);
                             */
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ILOAD, 9));
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook", "getSidebarTextLine", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                    }

                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals("revamp/sidebarmod/gui/GuiSidebar")
                            && ((MethodInsnNode) insnNode).name.equals("func_73734_a")
                            && ((MethodInsnNode) insnNode).desc.equals("(IIIII)V")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "revamp/sidebarmod/gui/GuiSidebar", "sidebarX", "I"));
                            list.add(new VarInsnNode(ILOAD, 16));//scoreY
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "revamp/sidebarmod/gui/GuiSidebar", "shadow", "Z"));
                            list.add(new VarInsnNode(ILOAD, 9));//index
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook", "renderSiderbarGui", "(IIZI)V", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                        return classNode;
                    }

                }
            }
        }
        return classNode;
    }
}
