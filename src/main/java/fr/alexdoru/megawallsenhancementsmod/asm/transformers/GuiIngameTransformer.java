package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
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
            if (methodNode.name.equals(MixinLoader.isObf ? "a" : "displayTitle") && methodNode.desc.equals("(Ljava/lang/String;Ljava/lang/String;III)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ALOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 2) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == PUTFIELD
                                && ((FieldInsnNode) nextNode).owner.equals(MixinLoader.isObf ? "avo" : "net/minecraft/client/gui/GuiIngame")
                                && ((FieldInsnNode) nextNode).name.equals(MixinLoader.isObf ? "y" : "displayedSubTitle")
                                && ((FieldInsnNode) nextNode).desc.equals("Ljava/lang/String;")) {
                            methodNode.instructions.insertBefore(
                                    nextNode,
                                    new MethodInsnNode(INVOKESTATIC,
                                            "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook",
                                            "cancelHungerTitle",
                                            "(Ljava/lang/String;)Ljava/lang/String;",
                                            false
                                    ));
                            MixinLoader.logger.info("Transformed GuiIngame");
                            return classNode;
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
