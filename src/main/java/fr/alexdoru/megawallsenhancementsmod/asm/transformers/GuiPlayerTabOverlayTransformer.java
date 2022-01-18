package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer implements IMyClassTransformer {

    private static final String PATCHER_CLASS = "club/sk1er/patcher/Patcher.class";

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        boolean isPatcherLoaded = true;
        try {
            isPatcherLoaded = this.getClass().getClassLoader().getResource(PATCHER_CLASS) != null;
        } catch (Exception ignored) {
        }
        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderPlayerlist") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(ILauo;Lauk;)V" : "(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == BIPUSH && insnNode instanceof IntInsnNode && ((IntInsnNode) insnNode).operand == 20) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 25));
                        methodNode.instructions.remove(insnNode);
                    }

                    if (!isPatcherLoaded && insnNode.getOpcode() == BIPUSH && insnNode instanceof IntInsnNode && ((IntInsnNode) insnNode).operand == 80) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 100));
                        methodNode.instructions.remove(insnNode);
                    }

                    if (insnNode.getOpcode() == ILOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 7) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == ISTORE && nextNode instanceof VarInsnNode && ((VarInsnNode) nextNode).var == 12) {
                            /*transformes line 109 to be : l = j + GuiPlayerTabOverlayHook.getFKScoreWidth();*/
                            InsnList list = new InsnList();
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "getFKScoreWidth", "()I", false));
                            list.add(new InsnNode(IADD));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                    }

                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "drawScoreboardValues") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lauk;ILjava/lang/String;IILbdc;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;ILjava/lang/String;IILnet/minecraft/client/network/NetworkPlayerInfo;)V")) {
                boolean injetedTransformation = false;
                int removedInstructions = 0;
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == GETSTATIC
                            && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "a" : "net/minecraft/util/EnumChatFormatting")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "o" : "YELLOW")
                            && ((FieldInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "La;" : "Lnet/minecraft/util/EnumChatFormatting;")) {
                        /*
                        Original line :
                        String s1 = EnumChatFormatting.YELLOW + "" + i;
                        After transformation :
                        String s1 = GuiPlayerTabOverlayHook.getScoretoRender(networkplayerinfo.playerFinalkills, i) + i;
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 6)); // load networkplayerinfo
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", "playerFinalkills", "I")); // get playerFinalkills field
                        list.add(new VarInsnNode(ILOAD, 7)); // load i
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "getScoretoRender", "(II)Ljava/lang/String;", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        injetedTransformation = true;
                    }

                    if (injetedTransformation && removedInstructions < 3) {
                        methodNode.instructions.remove(insnNode);
                        removedInstructions++;
                    }

                }
            }

        }
        ASMLoadingPlugin.logger.info("Transformed GuiPlayerTabOverlay");
        return classNode;
    }

}
