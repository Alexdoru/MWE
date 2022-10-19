package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

public class GuiIngameTransformer_CancelHunger implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiIngame";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.DISPLAYTITLE)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 2) && checkFieldInsnNode(insnNode.getNext(), PUTFIELD, FieldMapping.GUIINGAME$DISPLAYEDSUBTITLE)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameHook"), "cancelHungerTitle", "(Ljava/lang/String;)Ljava/lang/String;", false));
                        status.addInjection();
                        break;
                    }
                }
            }
        }
        return classNode;
    }

}
