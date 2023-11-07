package fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import org.objectweb.asm.tree.*;

public class SidebarmodRevampTransformer_CustomSidebarLines implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"revamp.sidebarmod.gui.GuiSidebar"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("drawSidebar")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL
                            && ((MethodInsnNode) insnNode).owner.equals("net/minecraft/scoreboard/ScoreObjective")
                            && ((MethodInsnNode) insnNode).name.equals("func_96678_d")
                            && ((MethodInsnNode) insnNode).desc.equals("()Ljava/lang/String;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof MethodInsnNode && nextNode.getOpcode() == INVOKEVIRTUAL
                                && ((MethodInsnNode) nextNode).owner.equals("net/minecraft/client/gui/FontRenderer")
                                && ((MethodInsnNode) nextNode).name.equals("func_78256_a")
                                && ((MethodInsnNode) nextNode).desc.equals("(Ljava/lang/String;)I")) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 3));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "revamp/sidebarmod/gui/GuiSidebar", "redNumbers", "Z"));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameHook"), "getSidebarTextLineWidth", "(ILnet/minecraft/client/gui/FontRenderer;Z)I", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }

                    if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKESTATIC
                            && ((MethodInsnNode) insnNode).owner.equals("net/minecraft/scoreboard/ScorePlayerTeam")
                            && ((MethodInsnNode) insnNode).name.equals("func_96667_a")
                            && ((MethodInsnNode) insnNode).desc.equals("(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkVarInsnNode(nextNode, ASTORE, 13)) {
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
    }

}
