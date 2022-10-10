package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiIngameForgeTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraftforge.client.GuiIngameForge";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderGameOverlay") && methodNode.desc.equals("(F)V")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof VarInsnNode && insnNode.getOpcode() == ILOAD) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (secondNode instanceof VarInsnNode && secondNode.getOpcode() == ILOAD) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (thirdNode instanceof VarInsnNode && thirdNode.getOpcode() == FLOAD) {
                                final AbstractInsnNode fourthNode = thirdNode.getNext();
                                if (fourthNode instanceof MethodInsnNode && fourthNode.getOpcode() == INVOKEVIRTUAL
                                        && ((MethodInsnNode) fourthNode).owner.equals("net/minecraftforge/client/GuiIngameForge")
                                        && ((MethodInsnNode) fourthNode).name.equals("renderRecordOverlay")
                                        && ((MethodInsnNode) fourthNode).desc.equals("(IIF)V")) {
                                    /*
                                     * Replaces line 150 :
                                     * renderRecordOverlay(width, height, partialTicks);
                                     * With :
                                     * renderRecordOverlay(width, GuiIngameForgeHook.adjustActionBarHeight(height, left_height), partialTicks);
                                     */
                                    final InsnList list = new InsnList();
                                    list.add(new FieldInsnNode(GETSTATIC, "net/minecraftforge/client/GuiIngameForge", "left_height", "I"));
                                    list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameForgeHook", "adjustActionBarHeight", "(II)I", false));
                                    methodNode.instructions.insert(secondNode, list);
                                    status.addInjection();
                                }
                            }
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
