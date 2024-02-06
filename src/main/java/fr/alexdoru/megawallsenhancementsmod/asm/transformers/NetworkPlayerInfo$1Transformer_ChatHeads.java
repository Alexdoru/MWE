package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class NetworkPlayerInfo$1Transformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetworkPlayerInfo$1"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
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
