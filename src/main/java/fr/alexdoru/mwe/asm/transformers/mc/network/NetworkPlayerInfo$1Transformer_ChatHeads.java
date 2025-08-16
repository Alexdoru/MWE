package fr.alexdoru.mwe.asm.transformers.mc.network;

import fr.alexdoru.mwe.asm.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class NetworkPlayerInfo$1Transformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetworkPlayerInfo$1"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        return !MWELoadingPlugin.isFeatherPresent();
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(MethodMapping.NETWORKPLAYERINFO$1$SKINAVAILABLE.name)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.NETWORKPLAYERINFO$ACCESS002)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            // call this.networkplayerinfo.mwe$onFinishedSkinLoading();
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$1$INSTANCE));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, ClassMapping.NETWORKPLAYERINFO.name, "mwe$onFinishedSkinLoading", "()V", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
