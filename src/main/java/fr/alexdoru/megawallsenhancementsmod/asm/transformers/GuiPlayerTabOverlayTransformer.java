package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "drawScoreboardValues") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lauk;ILjava/lang/String;IILbdc;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;ILjava/lang/String;IILnet/minecraft/client/network/NetworkPlayerInfo;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == GETSTATIC
                            && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "a" : "net/minecraft/util/EnumChatFormatting")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "o" : "YELLOW")
                            && ((FieldInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "La;" : "Lnet/minecraft/util/EnumChatFormatting;")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 7));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/ColorUtil", "getColoredHP", ASMLoadingPlugin.isObf ? "(I)La;" : "(I)Lnet/minecraft/util/EnumChatFormatting;", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        ASMLoadingPlugin.logger.info("Transformed GuiPlayerTabOverlay");
                        return classNode;
                    }
                }
            }
        }
        return classNode;
    }

}
