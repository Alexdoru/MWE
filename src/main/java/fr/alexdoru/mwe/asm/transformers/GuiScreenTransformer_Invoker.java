package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiScreenTransformer_Invoker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreen"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "GuiScreenInvoker");
        final MethodNode mn = new MethodNode(ACC_PUBLIC, "mwe$drawHoveringText", "(Ljava/util/List;II)V", "(Ljava/util/List<Ljava/lang/String;>;II)V", null);
        classNode.methods.add(mn);
        final LabelNode l0 = new LabelNode();
        mn.instructions.add(l0);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new VarInsnNode(ALOAD, 1));
        mn.instructions.add(new VarInsnNode(ILOAD, 2));
        mn.instructions.add(new VarInsnNode(ILOAD, 3));
        mn.instructions.add(getNewMethodInsnNode(MethodMapping.GUISCREEN$DRAWHOVERINGTEXT));
        final LabelNode l1 = new LabelNode();
        mn.instructions.add(l1);
        mn.instructions.add(new InsnNode(RETURN));
        final LabelNode l2 = new LabelNode();
        mn.instructions.add(l2);
        mn.localVariables.add(new LocalVariableNode("this", "L" + ClassMapping.GUISCREEN.name + ";", null, l0, l2, 0));
        mn.localVariables.add(new LocalVariableNode("textLines", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/String;>;", l0, l2, 1));
        mn.localVariables.add(new LocalVariableNode("x", "I", null, l0, l2, 2));
        mn.localVariables.add(new LocalVariableNode("y", "I", null, l0, l2, 3));
        mn.visitMaxs(4, 4);
    }

}
