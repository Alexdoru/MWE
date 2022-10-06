package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiScreenBookTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiScreenBook";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals("<init>") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lwn;Lzx;Z)V" : "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Z)V")) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC,
                        "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiScreenBookHook",
                        "onBookInit",
                        ASMLoadingPlugin.isObf ? "(Lzx;)V" : "(Lnet/minecraft/item/ItemStack;)V",
                        false
                ));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), list);
                status.addInjection();
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "keyTyped") && methodNode.desc.equals("(CI)V")) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC,
                        "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiScreenBookHook",
                        "onKeyTyped",
                        "(I)V",
                        false));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), list);
                status.addInjection();
            }

            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "drawScreen") && methodNode.desc.equals("(IIF)V")) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if(insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "ayo" : "net/minecraft/client/gui/GuiScreenBook")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "b" : "drawTexturedModalRect")
                            && ((MethodInsnNode) insnNode).desc.equals("(IIIIII)V")) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ayo" : "net/minecraft/client/gui/GuiScreenBook", ASMLoadingPlugin.isObf ? "l" : "width", "I"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ayo" : "net/minecraft/client/gui/GuiScreenBook", ASMLoadingPlugin.isObf ? "v" : "bookImageHeight", "I"));
                        list.add(new MethodInsnNode(INVOKESTATIC,
                                "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiScreenBookHook",
                                "renderInstructions",
                                "(II)V",
                                false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                        break;
                    }
                }
            }

        }
        return classNode;
    }

}
