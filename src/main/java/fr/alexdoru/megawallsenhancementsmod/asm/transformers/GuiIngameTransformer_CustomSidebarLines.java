package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiIngameTransformer_CustomSidebarLines implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiIngame"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIINGAME$RENDERSCOREBOARD)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.SCOREOBJECTIVE$GETDISPLAYNAME)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkMethodInsnNode(nextNode, MethodMapping.FONTRENDERER$GETSTRINGWIDTH)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(getNewMethodInsnNode(MethodMapping.GUIINGAME$GETFONTRENDERER));
                            list.add(new InsnNode(ICONST_1));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameHook"), "getSidebarTextLineWidth", "(IL" + ClassMapping.FONTRENDERER + ";Z)I", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.SCOREPLAYERTEAM$FORMATPLAYERNAME) && checkVarInsnNode(insnNode.getNext(), ASTORE, 15)) {
                       /*
                       Transforms line 579 :
                       Original line : String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                       After transformation : String s1 = GuiIngameHook.getSidebarTextLine(ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName()), j);
                        */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 11));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameHook"), "getSidebarTextLine", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                }
            }
        }
    }

}
