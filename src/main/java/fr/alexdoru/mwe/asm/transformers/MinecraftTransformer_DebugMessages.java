package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class MinecraftTransformer_DebugMessages implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(4);

        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.MINECRAFT$RUNTICK)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.RENDERGLOBAL$LOADRENDERER)) {
                        // Injects after line 1989 :
                        // MinecraftHook_DebugMessages.onSettingChange(this.gameSettings.advancedItemTooltips, "Advanced Item Tooltips");
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("MinecraftHook_DebugMessages"),
                                "onReloadChunks",
                                "(L" + ClassMapping.MINECRAFT + ";)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.GAMESETTINGS$ADVANCEDITEMTOOLTIPS)) {
                        // Injects after line 1994 :
                        // MinecraftHook_DebugMessages.onSettingChange(this.gameSettings.advancedItemTooltips, "Advanced Item Tooltips");
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$GAMESETTINGS));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GAMESETTINGS$ADVANCEDITEMTOOLTIPS));
                        list.add(new LdcInsnNode("Advanced Item Tooltips"));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("MinecraftHook_DebugMessages"),
                                "onSettingChange",
                                "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.RENDERMANAGER$SETDEBUGBOUNDINGBOX)) {
                        // Injects after line 2000 :
                        // MinecraftHook_DebugMessages.onSettingChange(this.renderManager.isDebugBoundingBox(), "Hitboxes");
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$RENDERMANAGER));
                        list.add(getNewMethodInsnNode(MethodMapping.RENDERMANAGER$ISDEBUGBOUNDINGBOX));
                        list.add(new LdcInsnNode("Hitboxes"));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("MinecraftHook_DebugMessages"),
                                "onSettingChange",
                                "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.GAMESETTINGS$PAUSEONLOSTFOCUS)) {
                        // Injects after line 2005 :
                        // MinecraftHook_DebugMessages.onSettingChange(this.gameSettings.pauseOnLostFocus, "Pause on lost focus");
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.MINECRAFT$GAMESETTINGS));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GAMESETTINGS$PAUSEONLOSTFOCUS));
                        list.add(new LdcInsnNode("Pause on lost focus"));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("MinecraftHook_DebugMessages"),
                                "onSettingChange",
                                "(L" + ClassMapping.MINECRAFT + ";ZLjava/lang/String;)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }

                }
            }
        }

    }

}
