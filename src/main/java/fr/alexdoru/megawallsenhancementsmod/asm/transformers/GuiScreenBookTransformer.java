package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class GuiScreenBookTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiScreenBook";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lwn;Lzx;Z)V" : "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Z)V")) {
                InsnList list = new InsnList();
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
        }
        return classNode;
    }

}
