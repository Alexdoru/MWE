package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiPlayerTabOverlayTransformer_FixMissplacedDrawRect implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiPlayerTabOverlay";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDERPLAYERLIST)) {
                //    ILOAD 16 <-- capture var
                //    ICONST_2
                //    IDIV
                //    IADD
                //    ICONST_1
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ILOAD)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkInsnNode(secondNode, ICONST_2)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkInsnNode(thirdNode, IDIV)) {
                                final AbstractInsnNode fourthNode = thirdNode.getNext();
                                if (checkInsnNode(fourthNode, IADD)) {
                                    final AbstractInsnNode fifthNode = fourthNode.getNext();
                                    if (checkInsnNode(fifthNode, ICONST_1)) {
                                        final InsnList list = new InsnList();
                                        list.add(new VarInsnNode(ILOAD, ((VarInsnNode) insnNode).var));
                                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook"), "fixMissplacedDrawRect", "(I)I", false));
                                        list.add(new InsnNode(IADD));
                                        methodNode.instructions.insert(fifthNode.getNext(), list);
                                        status.addInjection();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
