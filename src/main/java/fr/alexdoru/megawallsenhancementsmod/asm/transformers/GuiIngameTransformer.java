package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiIngameTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiIngame";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "displayTitle") && methodNode.desc.equals("(Ljava/lang/String;Ljava/lang/String;III)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ALOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 2) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == PUTFIELD
                                && ((FieldInsnNode) nextNode).owner.equals(ASMLoadingPlugin.isObf ? "avo" : "net/minecraft/client/gui/GuiIngame")
                                && ((FieldInsnNode) nextNode).name.equals(ASMLoadingPlugin.isObf ? "y" : "displayedSubTitle")
                                && ((FieldInsnNode) nextNode).desc.equals("Ljava/lang/String;")) {
                            methodNode.instructions.insertBefore(
                                    nextNode,
                                    new MethodInsnNode(INVOKESTATIC,
                                            "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook",
                                            "cancelHungerTitle",
                                            "(Ljava/lang/String;)Ljava/lang/String;",
                                            false
                                    ));
                            break;
                        }
                    }
                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderScoreboard") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lauk;Lavr;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;Lnet/minecraft/client/gui/ScaledResolution;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "aul" : "net/minecraft/scoreboard/ScorePlayerTeam")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "formatPlayerName")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Lauq;Ljava/lang/String;)Ljava/lang/String;" : "(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == ASTORE && nextNode instanceof VarInsnNode && ((VarInsnNode) nextNode).var == 15) {
                            /*
                            Transforms line 579 :
                            Original line : String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                            After transformation : String s1 = GuiIngameHook.getSidebarTextLine(ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName()), j);
                             */
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ILOAD, 11));
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook", "getSidebarTextLine", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                    }
                }
            }

        }
        return classNode;
    }

}
