package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiPlayerTabOverlayTransformer_PlayerCount implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$LISTFORMATTEDSTRINGTOWIDTH)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiPlayerTabOverlayHook_PlayerCount"),
                                "addPlayerCountInHeader",
                                "(Ljava/util/List;)Ljava/util/List;",
                                false
                        ));
                        status.addInjection();
                        break;
                    }
                }
            }
        }
    }

}
