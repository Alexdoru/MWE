package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiNewChatTransformer_CleanChatLogs implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$PRINTCHATMESSAGEWITHOPTIONALDELETION)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.LOGGER$INFO)) {
                        methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiNewChatHook_CleanChatLogs"),
                                "removeFormatting",
                                "(Ljava/lang/String;)Ljava/lang/String;",
                                false));
                        status.addInjection();
                    }
                }
            }
        }
    }

}
