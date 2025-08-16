package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiPlayerTabOverlayTransformer_ColumnSpacing implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                int found = 0;
                AbstractInsnNode target1 = null;
                AbstractInsnNode target2 = null;
                AbstractInsnNode target3 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ICONST_5)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, IMUL)) {
                            found++;
                            if (found == 1) target1 = insnNode;
                            if (found == 2) target2 = insnNode;
                            if (found == 3) target3 = insnNode;
                        }
                    }
                }
                if (found == 3) {
                    this.injectHook(methodNode, status, target1);
                    this.injectHook(methodNode, status, target2);
                    this.injectHook(methodNode, status, target3);
                }
            }
        }
    }

    private void injectHook(MethodNode methodNode, InjectionCallback status, AbstractInsnNode target) {
        methodNode.instructions.insert(target, new MethodInsnNode(
                INVOKESTATIC,
                getHookClass("GuiPlayerTabOverlayHook_ColumnSpacing"),
                "getColumnSpacing",
                "(I)I",
                false
        ));
        status.addInjection();
    }

}
