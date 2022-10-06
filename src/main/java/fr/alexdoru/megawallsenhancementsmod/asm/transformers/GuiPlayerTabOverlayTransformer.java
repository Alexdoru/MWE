package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer implements IMyClassTransformer {

    private static final String PATCHER_CLASS = "club/sk1er/patcher/Patcher.class";

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        boolean isPatcherLoaded = true;
        final int injectionPoints = 6;
        status.setInjectionPoints(injectionPoints);

        try {
            isPatcherLoaded = this.getClass().getClassLoader().getResource(PATCHER_CLASS) != null;
            if (isPatcherLoaded) {
                status.setInjectionPoints(injectionPoints - 1);
            }
        } catch (Exception ignored) {
        }

        for (final MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderPlayerlist") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(ILauo;Lauk;)V" : "(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V")) {

                boolean foundListFormattedStringToWidth = false;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == BIPUSH && insnNode instanceof IntInsnNode && ((IntInsnNode) insnNode).operand == 20) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 25));
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }

                    if (!isPatcherLoaded && insnNode.getOpcode() == BIPUSH && insnNode instanceof IntInsnNode && ((IntInsnNode) insnNode).operand == 80) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 100));
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }

                    if (insnNode.getOpcode() == ILOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 7) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode.getOpcode() == ISTORE && nextNode instanceof VarInsnNode && ((VarInsnNode) nextNode).var == 12) {
                            /*
                             * Replaces line 109 :
                             * l = j;
                             * With :
                             * l = j + GuiPlayerTabOverlayHook.getFKScoreWidth();
                             */
                            final InsnList list = new InsnList();
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "getFKScoreWidth", "()I", false));
                            list.add(new InsnNode(IADD));
                            methodNode.instructions.insertBefore(nextNode, list);
                            status.addInjection();
                        }
                    }

                    if (!foundListFormattedStringToWidth && insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL &&
                            ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "avn" : "net/minecraft/client/gui/FontRenderer")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "c" : "listFormattedStringToWidth")
                            && ((MethodInsnNode) insnNode).desc.equals("(Ljava/lang/String;I)Ljava/util/List;")) {
                        foundListFormattedStringToWidth = true;
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "addPlayerCountinHeader", "(Ljava/util/List;)Ljava/util/List;", false));
                        status.addInjection();
                    }

                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "drawScoreboardValues") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lauk;ILjava/lang/String;IILbdc;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;ILjava/lang/String;IILnet/minecraft/client/network/NetworkPlayerInfo;)V")) {
                boolean sliceFlag = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == GETSTATIC
                            && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "a" : "net/minecraft/util/EnumChatFormatting")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "o" : "YELLOW")
                            && ((FieldInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "La;" : "Lnet/minecraft/util/EnumChatFormatting;")) {
                        /*
                        Original line :
                        String s1 = EnumChatFormatting.YELLOW + "" + i;
                        After transformation :
                        String s1 = GuiPlayerTabOverlayHook.getColoredHP(i) + "" + i;
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 7));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "getColoredHP", ASMLoadingPlugin.isObf ? "(I)La;" : "(I)Lnet/minecraft/util/EnumChatFormatting;", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        sliceFlag = true;
                        status.addInjection();
                    }

                    if (sliceFlag && insnNode.getOpcode() == ALOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 0) {
                        /*
                        Injects before line 365 :
                        GuiPlayerTabOverlayHook.renderFinals(p_175247_6_.playerFinalkills, p_175247_5_, p_175247_2_);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 6)); // load networkplayerinfo / p_175247_6_
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "bdc" : "net/minecraft/client/network/NetworkPlayerInfo", "playerFinalkills", "I")); // get playerFinalkills field
                        list.add(new VarInsnNode(ILOAD, 5));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiPlayerTabOverlayHook", "renderFinals", "(III)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                        break;
                    }

                }
            }

        }

        return classNode;

    }

}
