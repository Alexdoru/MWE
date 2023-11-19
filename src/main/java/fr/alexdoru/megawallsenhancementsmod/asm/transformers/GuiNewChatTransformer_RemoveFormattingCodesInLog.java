package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiNewChatTransformer_RemoveFormattingCodesInLog implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$PRINTCHATMESSAGEWITHOPTIONALDELETION)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.ICHATCOMPONENT$GETUNFORMATTEDTEXT)) {
                        methodNode.instructions.insert(insnNode, getNewMethodInsnNode(MethodMapping.ENUMCHATFORMATTING$GETTEXTWITHOUTFORMATTINGCODES));
                        status.addInjection();
                    }
                }
            }
        }
    }

}
