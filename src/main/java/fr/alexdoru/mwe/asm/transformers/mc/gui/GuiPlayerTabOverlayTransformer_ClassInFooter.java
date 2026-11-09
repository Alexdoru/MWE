package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiPlayerTabOverlayTransformer_ClassInFooter extends GuiPlayerTabOverlayTransformer_HeaderModifier {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(2);
        final int listIndex = injectAfterHeaderCheck(classNode, status, FieldMapping.GUIPLAYERTABOVERLAY$FOOTER, "GuiPlayerTabOverlayHook_ClassCount");
        if (listIndex == -1) {
            return;
        }
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                boolean slice = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, listIndex) && checkJumpInsnNode(insnNode.getNext(), IFNULL)) {
                        slice = true;
                    }
                    if (slice && checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$DRAWSTRINGWITHSHADOW)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("mc/gui/GuiPlayerTabOverlayHook_ClassCount"),
                                "drawString",
                                "(" + ClassMapping.FONTRENDERER.desc() + "Ljava/lang/String;FFII)I",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                        status.addInjection();
                        return;
                    }
                }
            }
        }
    }

}
