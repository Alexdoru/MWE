package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class EntityPlayerSPTransformer_CommandListener implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.entity.EntityPlayerSP"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYPLAYERSP$SENDCHATMESSAGE)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("EntityPlayerSPHook_CommandListener"),
                        "onMessageSent",
                        "(Ljava/lang/String;)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
