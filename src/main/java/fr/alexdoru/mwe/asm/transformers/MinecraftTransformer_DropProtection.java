package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class MinecraftTransformer_DropProtection implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.MINECRAFT$RUNTICK)) {
                this.injectUpdateCurrentSlot(methodNode, status);
                this.injectDropItemCheck(methodNode, status);
            }
        }
    }

    private void injectUpdateCurrentSlot(MethodNode methodNode, InjectionCallback status) {
        for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (checkMethodInsnNode(insnNode, MethodMapping.INVENTORYPLAYER$CHANGECURRENTITEM) ||
                    checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.INVENTORYPLAYER$CURRENTITEM)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("MinecraftHook_DropProtection"),
                        "updateCurrentSlot",
                        "(L" + ClassMapping.MINECRAFT + ";)V",
                        false
                ));
                methodNode.instructions.insert(insnNode, list);
                status.addInjection();
            }
        }
    }

    private void injectDropItemCheck(MethodNode methodNode, InjectionCallback status) {
        boolean slice = false;
        AbstractInsnNode targetNode = null;
        LabelNode label = null;
        for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (!slice && checkFieldInsnNode(insnNode, GETFIELD, FieldMapping.GAMESETTINGS$KEYBINDDROP)) {
                slice = true;
            }
            if (slice && label == null && checkMethodInsnNode(insnNode, MethodMapping.ENTITYPLAYERSP$ISSPECTATOR)) {
                final AbstractInsnNode nextNode = insnNode.getNext();
                if (checkJumpInsnNode(nextNode, IFNE)) {
                    targetNode = nextNode;
                    label = ((JumpInsnNode) nextNode).label;
                }
            }
            if (slice && label != null && checkMethodInsnNode(insnNode, MethodMapping.ENTITYPLAYERSP$DROPONEITEM)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("MinecraftHook_DropProtection"),
                        "shouldDropItem",
                        "(L" + ClassMapping.MINECRAFT + ";)Z",
                        false
                ));
                list.add(new JumpInsnNode(IFEQ, label));
                methodNode.instructions.insert(targetNode, list);
                status.addInjection();
                return;
            }

        }
    }

}
