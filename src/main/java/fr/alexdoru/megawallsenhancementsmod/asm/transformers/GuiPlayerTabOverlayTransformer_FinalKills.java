package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer_FinalKills implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(2);

        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
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
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.DRAWSCOREBOARDVALUES)) {
                int ordinal = 0;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.DRAWSTRINGWITHSHADOW)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (ordinal == 1 && checkInsnNode(nextNode, POP)) {
                            /*
                            Injects after line 365 :
                            GuiPlayerTabOverlayHook.renderFinals(p_175247_6_.playerFinalkills, p_175247_5_, p_175247_2_);
                             */
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 6)); // load networkplayerinfo / p_175247_6_
                            list.add(new FieldInsnNode(GETFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), "playerFinalkills", "I")); // get playerFinalkills field
                            list.add(new VarInsnNode(ILOAD, 5));
                            list.add(new VarInsnNode(ILOAD, 2));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "renderFinals", "(III)V", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                        ordinal++;
                    }
                }
            }

        }

        return classNode;

    }

}
