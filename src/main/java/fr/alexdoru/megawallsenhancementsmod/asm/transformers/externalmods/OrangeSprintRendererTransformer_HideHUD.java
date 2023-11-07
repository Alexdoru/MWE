package fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import org.objectweb.asm.tree.*;

public class OrangeSprintRendererTransformer_HideHUD implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"com.orangemarshall.simplemod.togglesprint.SprintRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("render")) {
                final InsnList list = new InsnList();
                final LabelNode notCanceled = new LabelNode();
                list.add(getNewConfigFieldInsnNode("hideToggleSprintText"));
                list.add(new JumpInsnNode(IFEQ, notCanceled));
                list.add(new InsnNode(RETURN));
                list.add(notCanceled);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
