package fr.alexdoru.mwe.asm.transformers.mc.network;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class C08PacketPlayerBlockPlacementTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.network.play.client.C08PacketPlayerBlockPlacement"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.C08PACKETPLAYERBLOCKPLACEMENT$INIT3)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC,
                                getHookClass("C08PacketPlayerBlockPlacementHook"),
                                "onBlockPlace",
                                "(L" + ClassMapping.BLOCKPOS + ";IL" + ClassMapping.ITEMSTACK + ";)V",
                                false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
