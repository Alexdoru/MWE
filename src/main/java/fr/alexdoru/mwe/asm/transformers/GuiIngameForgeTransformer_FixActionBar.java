package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiIngameForgeTransformer_FixActionBar implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIINGAME$RENDERGAMEOVERLAY)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ILOAD)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkVarInsnNode(secondNode, ILOAD)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkVarInsnNode(thirdNode, FLOAD) && checkMethodInsnNode(thirdNode.getNext(), MethodMapping.GUIINGAMEFORGE$RENDERRECORDOVERLAY)) {
                                // Replaces line 150 :
                                // renderRecordOverlay(width, height, partialTicks);
                                // With :
                                // renderRecordOverlay(width, GuiIngameForgeHook.adjustActionBarHeight(height, left_height), partialTicks);
                                final InsnList list = new InsnList();
                                list.add(new FieldInsnNode(GETSTATIC, "net/minecraftforge/client/GuiIngameForge", "left_height", "I"));
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiIngameForgeHook"), "adjustActionBarHeight", "(II)I", false));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
