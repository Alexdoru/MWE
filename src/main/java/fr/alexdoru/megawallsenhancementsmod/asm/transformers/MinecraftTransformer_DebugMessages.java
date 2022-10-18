package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.*;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class MinecraftTransformer_DebugMessages implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(4);

        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RUNTICK)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.LOADRENDERER)) {
                        /*
                         * Injects after line 1989 :
                         * MinecraftHook.onSettingChange(this.gameSettings.advancedItemTooltips, "Advanced Item Tooltips");
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "onReloadChunks", "(L" + ClassMapping.MINECRAFT + ";)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.GAMESETTINGS$ADVANCEDITEMTOOLTIPS)) {
                        /*
                         * Injects after line 1994 :
                         * MinecraftHook.onSettingChange(this.gameSettings.advancedItemTooltips, "Advanced Item Tooltips");
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$GAMESETTINGS));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GAMESETTINGS$ADVANCEDITEMTOOLTIPS));
                        list.add(new LdcInsnNode("Advanced Item Tooltips"));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "onSettingChange", "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.SETDEBUGBOUNDINGBOX)) {
                        /*
                         * Injects after line 2000 :
                         * MinecraftHook.onSettingChange(this.renderManager.isDebugBoundingBox(), "Hitboxes");
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$RENDERMANAGER));
                        list.add(getNewMethodInsnNode(MethodMapping.ISDEBUGBOUNDINGBOX));
                        list.add(new LdcInsnNode("Hitboxes"));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("MinecraftHook"), "onSettingChange", "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.GAMESETTINGS$PAUSEONLOSTFOCUS)) {
                        /*
                         * Injects after line 2005 :
                         * MinecraftHook.onSettingChange(this.gameSettings.pauseOnLostFocus, "Pause on lost focus");
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$GAMESETTINGS));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GAMESETTINGS$PAUSEONLOSTFOCUS));
                        list.add(new LdcInsnNode("Pause on lost focus"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "onSettingChange", "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                }
            }
        }

        return classNode;

    }

}
