package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityOtherPlayerMPTransformer_FixAutoblockBypass implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.entity.EntityOtherPlayerMP"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITY$SETCURRENTITEMORARMOR)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("EntityOtherPlayerMPHook_FixAutoblockBypass"),
                        "shouldCancelEquipmentUpdate",
                        "(L" + ClassMapping.ENTITYOTHERPLAYERMP + ";IL" + ClassMapping.ITEMSTACK + ";)Z",
                        false
                ));
                final LabelNode notCancelled = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, notCancelled));
                list.add(new InsnNode(RETURN));
                list.add(notCancelled);
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
