package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityPlayerSPTransformer_Sprint implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.entity.EntityPlayerSP"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYPLAYERSP$ONLIVINGUPDATE)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, GETFIELD, FieldMapping.GAMESETTINGS$KEYBINDSPRINT)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkMethodInsnNode(nextNode, MethodMapping.KEYBINDING$ISKEYDOWN)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.ENTITYPLAYERSP$MC));
                            list.add(new MethodInsnNode(
                                    INVOKESTATIC,
                                    getHookClass("EntityPlayerSPHook_Sprint"),
                                    "shouldSprint",
                                    "(ZL" + ClassMapping.MINECRAFT + ";)Z",
                                    false
                            ));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
    }

}
