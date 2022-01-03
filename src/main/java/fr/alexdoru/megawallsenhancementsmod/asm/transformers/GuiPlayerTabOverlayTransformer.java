package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals(MixinLoader.isObf ? "a" : "drawScoreboardValues") && methodNode.desc.equals(MixinLoader.isObf ? "(Lauk;ILjava/lang/String;IILbdc;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;ILjava/lang/String;IILnet/minecraft/client/network/NetworkPlayerInfo;)V")) {
                    for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                        if (insnNode.getOpcode() == GETSTATIC
                                && insnNode instanceof FieldInsnNode
                                && ((FieldInsnNode) insnNode).owner.equals(MixinLoader.isObf ? "a" : "net/minecraft/util/EnumChatFormatting")
                                && ((FieldInsnNode) insnNode).name.equals(MixinLoader.isObf ? "o" : "YELLOW")
                                && ((FieldInsnNode) insnNode).desc.equals(MixinLoader.isObf ? "La;" : "Lnet/minecraft/util/EnumChatFormatting;")) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ILOAD, 7));
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/utils/ColorUtil", "getColoredHP", MixinLoader.isObf ? "(I)La;" : "(I)Lnet/minecraft/util/EnumChatFormatting;", false));
                            methodNode.instructions.insertBefore(insnNode, list);
                            methodNode.instructions.remove(insnNode);
                            MixinLoader.logger.info("Injected Colored HP in Tablist");
                        }
                    }
                }
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
    }

}
