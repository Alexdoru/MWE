package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.BIPUSH;

public class GuiPlayerTabOverlayTransformer_LongerTab implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        boolean isPatcherLoaded = true;
        final int injectionPoints = 2;
        status.setInjectionPoints(injectionPoints);

        try {
            final String PATCHER_CLASS = "club/sk1er/patcher/Patcher.class";
            isPatcherLoaded = this.getClass().getClassLoader().getResource(PATCHER_CLASS) != null;
            if (isPatcherLoaded) {
                status.setInjectionPoints(injectionPoints - 1);
            }
        } catch (Exception ignored) {}

        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {
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
                }
            }
        }

        return classNode;

    }

}
