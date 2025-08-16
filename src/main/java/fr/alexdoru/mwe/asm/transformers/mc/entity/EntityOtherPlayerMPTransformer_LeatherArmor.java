package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class EntityOtherPlayerMPTransformer_LeatherArmor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.entity.EntityOtherPlayerMP"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITY$SETCURRENTITEMORARMOR)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("EntityOtherPlayerMPHook_LeatherArmor"),
                        "getLeatherArmor",
                        "(L" + ClassMapping.ENTITYOTHERPLAYERMP + ";IL" + ClassMapping.ITEMSTACK + ";)L" + ClassMapping.ITEMSTACK + ";",
                        false
                ));
                list.add(new VarInsnNode(ASTORE, 2));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
