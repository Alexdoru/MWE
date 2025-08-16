package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiIngameTransformer_CancelHunger implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiIngame"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIINGAME$DISPLAYTITLE)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 2) && checkFieldInsnNode(insnNode.getNext(), PUTFIELD, FieldMapping.GUIINGAME$DISPLAYEDSUBTITLE)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiIngameHook_CancelHunger"),
                                "cancelHungerTitle",
                                "(Ljava/lang/String;)Ljava/lang/String;",
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
