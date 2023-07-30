package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class GuiPlayerTabOverlayTransformer_PlayerCount implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.LISTFORMATTEDSTRINGTOWIDTH)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "addPlayerCountInHeader", "(Ljava/util/List;)Ljava/util/List;", false));
                        status.addInjection();
                        break;
                    }
                }
            }
        }
    }

}
