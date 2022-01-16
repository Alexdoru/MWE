package fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class SidebarmodReloaded_CustomSidebar implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "fr.alexdoru.sidebarmod.gui.CustomSidebar";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("drawSidebar")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals("fr/alexdoru/sidebarmod/gui/CustomSidebar")
                            && ((MethodInsnNode) insnNode).name.equals("func_73734_a")
                            && ((MethodInsnNode) insnNode).desc.equals("(IIIII)V")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "fr/alexdoru/sidebarmod/gui/CustomSidebar", "sidebarX", "I"));
                            list.add(new VarInsnNode(ILOAD, 17));//scoreY
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new FieldInsnNode(GETFIELD, "fr/alexdoru/sidebarmod/gui/CustomSidebar", "shadow", "Z"));
                            list.add(new VarInsnNode(ILOAD, 9));//index
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook", "renderSiderbarGui", "(IIZI)V", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                        return classNode;
                    }
                }
            }
        }
        return classNode;
    }
}
