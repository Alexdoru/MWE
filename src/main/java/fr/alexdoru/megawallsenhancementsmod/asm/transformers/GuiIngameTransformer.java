package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiIngameTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiIngame";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {
        int injections = 0;
        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "displayTitle") && methodNode.desc.equals("(Ljava/lang/String;Ljava/lang/String;III)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == ALOAD && insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 2) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null && nextNode.getOpcode() == PUTFIELD
                                && ((FieldInsnNode) nextNode).owner.equals(ASMLoadingPlugin.isObf ? "avo" : "net/minecraft/client/gui/GuiIngame")
                                && ((FieldInsnNode) nextNode).name.equals(ASMLoadingPlugin.isObf ? "y" : "displayedSubTitle")
                                && ((FieldInsnNode) nextNode).desc.equals("Ljava/lang/String;")) {
                            methodNode.instructions.insertBefore(
                                    nextNode,
                                    new MethodInsnNode(INVOKESTATIC,
                                            "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiIngameHook",
                                            "cancelHungerTitle",
                                            "(Ljava/lang/String;)Ljava/lang/String;",
                                            false
                                    ));
                            injections++;
                            break;
                        }
                    }
                }
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderScoreboard") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lauk;Lavr;)V" : "(Lnet/minecraft/scoreboard/ScoreObjective;Lnet/minecraft/client/gui/ScaledResolution;)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode.getOpcode() == INVOKESTATIC && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "avo" : "net/minecraft/client/gui/GuiIngame")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "drawRect")
                            && ((MethodInsnNode) insnNode).desc.equals("(IIIII)V")) {
                        AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode != null) {
                            InsnList list = new InsnList();
                            /*CALL FKCounterGui.instance.renderinSidebar(l1, k, ConfigHandler.text_shadow, j)*/
                            list.add(new FieldInsnNode(GETSTATIC, "fr/alexdoru/fkcountermod/gui/FKCounterGui", "instance", "Lfr/alexdoru/fkcountermod/gui/FKCounterGui;"));
                            list.add(new VarInsnNode(ILOAD, 10)); //l1
                            list.add(new VarInsnNode(ILOAD, 17)); //k
                            list.add(new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", "text_shadow", "Z"));
                            list.add(new VarInsnNode(ILOAD, 11));//j
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, "fr/alexdoru/fkcountermod/gui/FKCounterGui", "renderinSidebar", "(IIZI)V", false));
                            methodNode.instructions.insertBefore(nextNode, list);
                        }
                        injections++;
                        break;
                    }
                }
            }

        }
        if (injections == 2) {
            ASMLoadingPlugin.logger.info("Transformed GuiIngame");
        }
        return classNode;
    }

}
