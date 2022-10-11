package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.*;
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

            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {

                boolean foundListFormattedStringToWidth = false;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkIntInsnNode(insnNode, BIPUSH, 20)) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 25));
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }

                    if (!isPatcherLoaded && checkIntInsnNode(insnNode, BIPUSH, 80)) {
                        methodNode.instructions.insertBefore(insnNode, new IntInsnNode(BIPUSH, 100));
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                    }

                    if (checkVarInsnNode(insnNode, ILOAD, 7) && checkVarInsnNode(insnNode.getNext(), ISTORE, 12)) {
                        /*
                         * Replaces line 109 :
                         * l = j;
                         * With :
                         * l = j + GuiPlayerTabOverlayHook.getFKScoreWidth();
                         */
                        final InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "getFKScoreWidth", "()I", false));
                        list.add(new InsnNode(IADD));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (!foundListFormattedStringToWidth && checkMethodInsnNode(insnNode, MethodMapping.LISTFORMATTEDSTRINGTOWIDTH)) {
                        foundListFormattedStringToWidth = true;
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "addPlayerCountinHeader", "(Ljava/util/List;)Ljava/util/List;", false));
                        status.addInjection();
                    }

                }
            }

            if (checkMethodNode(methodNode, MethodMapping.DRAWSCOREBOARDVALUES)) {
                boolean sliceFlag = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkFieldInsnNode(insnNode, GETSTATIC, FieldMapping.ENUMCHATFORMATTING$YELLOW)) {
                        /*
                        Original line :
                        String s1 = EnumChatFormatting.YELLOW + "" + i;
                        After transformation :
                        String s1 = GuiPlayerTabOverlayHook.getColoredHP(i) + "" + i;
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 7));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "getColoredHP", "(I)L" + ClassMapping.ENUMCHATFORMATTING + ";", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        sliceFlag = true;
                        status.addInjection();
                    }

                    if (sliceFlag && checkVarInsnNode(insnNode, ALOAD, 0)) {
                        /*
                        Injects before line 365 :
                        GuiPlayerTabOverlayHook.renderFinals(p_175247_6_.playerFinalkills, p_175247_5_, p_175247_2_);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 6)); // load networkplayerinfo / p_175247_6_
                        list.add(new FieldInsnNode(GETFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), "playerFinalkills", "I")); // get playerFinalkills field
                        list.add(new VarInsnNode(ILOAD, 5));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "renderFinals", "(III)V", false));
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
