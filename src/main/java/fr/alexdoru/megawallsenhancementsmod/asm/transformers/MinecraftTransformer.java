package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class MinecraftTransformer implements IMyClassTransformer {
    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public ClassNode transform(ClassNode classNode) {

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "s" : "runTick") && methodNode.desc.equals("()V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "wm" : "net/minecraft/entity/player/InventoryPlayer")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "d" : "changeCurrentItem")
                            && ((MethodInsnNode) insnNode).desc.equals("(I)V")) {
                        /*
                         * Injects before line 1869
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insertBefore(insnNode, updateCurrentSlotInsnList());
                    }

                    if (insnNode.getOpcode() == PUTFIELD && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "avh" : "net/minecraft/client/settings/GameSettings")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "y" : "advancedItemTooltips")
                            && ((FieldInsnNode) insnNode).desc.equals("Z")
                            && insnNode.getNext() != null) {
                        /*
                         * Injects after line 1994 :
                         * MinecraftHook.onSettingChange(this.gameSettings.advancedItemTooltips, "Advanced Item Tooltips");
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ave" : "net/minecraft/client/Minecraft", ASMLoadingPlugin.isObf ? "t" : "gameSettings", ASMLoadingPlugin.isObf ? "Lavh;" : "Lnet/minecraft/client/settings/GameSettings;"));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "avh" : "net/minecraft/client/settings/GameSettings", ASMLoadingPlugin.isObf ? "y" : "advancedItemTooltips", "Z"));
                        list.add(new LdcInsnNode("Advanced Item Tooltips"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "onSettingChange", ASMLoadingPlugin.isObf ? "(Lave;ZLjava/lang/String;)V" : "(Lnet/minecraft/client/Minecraft;ZLjava/lang/String;)V", false));
                        methodNode.instructions.insertBefore(insnNode.getNext(), list);
                    }

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "biu" : "net/minecraft/client/renderer/entity/RenderManager")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "b" : "setDebugBoundingBox")
                            && ((MethodInsnNode) insnNode).desc.equals("(Z)V")
                            && insnNode.getNext() != null) {
                        /*
                         * Injects after line 2000 :
                         * MinecraftHook.onSettingChange(this.renderManager.isDebugBoundingBox(), "Hitboxes");
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ave" : "net/minecraft/client/Minecraft", ASMLoadingPlugin.isObf ? "aa" : "renderManager", ASMLoadingPlugin.isObf ? "Lbiu;" : "Lnet/minecraft/client/renderer/entity/RenderManager;"));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, ASMLoadingPlugin.isObf ? "biu" : "net/minecraft/client/renderer/entity/RenderManager", ASMLoadingPlugin.isObf ? "b" : "isDebugBoundingBox", "()Z", false));
                        list.add(new LdcInsnNode("Hitboxes"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "onSettingChange", ASMLoadingPlugin.isObf ? "(Lave;ZLjava/lang/String;)V" : "(Lnet/minecraft/client/Minecraft;ZLjava/lang/String;)V", false));
                        methodNode.instructions.insertBefore(insnNode.getNext(), list);
                    }

                    if (insnNode.getOpcode() == PUTFIELD && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "avh" : "net/minecraft/client/settings/GameSettings")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "z" : "pauseOnLostFocus")
                            && ((FieldInsnNode) insnNode).desc.equals("Z")
                            && insnNode.getNext() != null) {
                        /*
                         * Injects after line 2005 :
                         * MinecraftHook.onSettingChange(this.gameSettings.pauseOnLostFocus, "Pause on lost focus");
                         */
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "ave" : "net/minecraft/client/Minecraft", ASMLoadingPlugin.isObf ? "t" : "gameSettings", ASMLoadingPlugin.isObf ? "Lavh;" : "Lnet/minecraft/client/settings/GameSettings;"));
                        list.add(new FieldInsnNode(GETFIELD, ASMLoadingPlugin.isObf ? "avh" : "net/minecraft/client/settings/GameSettings", ASMLoadingPlugin.isObf ? "z" : "pauseOnLostFocus", "Z"));
                        list.add(new LdcInsnNode("Pause on lost focus"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "onSettingChange", ASMLoadingPlugin.isObf ? "(Lave;ZLjava/lang/String;)V" : "(Lnet/minecraft/client/Minecraft;ZLjava/lang/String;)V", false));
                        methodNode.instructions.insertBefore(insnNode.getNext(), list);
                    }

                    if (insnNode.getOpcode() == PUTFIELD && insnNode instanceof FieldInsnNode
                            && ((FieldInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "wm" : "net/minecraft/entity/player/InventoryPlayer")
                            && ((FieldInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "c" : "currentItem")
                            && ((FieldInsnNode) insnNode).desc.equals("I")) {
                        /*
                         * Injects before line 2077
                         * MinecraftHook.updateCurrentSlot(this);
                         */
                        methodNode.instructions.insertBefore(insnNode, updateCurrentSlotInsnList());
                    }

                    if (insnNode.getOpcode() == INVOKEVIRTUAL && insnNode instanceof MethodInsnNode
                            && ((MethodInsnNode) insnNode).owner.equals(ASMLoadingPlugin.isObf ? "bew" : "net/minecraft/client/entity/EntityPlayerSP")
                            && ((MethodInsnNode) insnNode).name.equals(ASMLoadingPlugin.isObf ? "a" : "dropOneItem")
                            && ((MethodInsnNode) insnNode).desc.equals(ASMLoadingPlugin.isObf ? "(Z)Luz;" : "(Z)Lnet/minecraft/entity/item/EntityItem;")) {
                        /*
                         * Replaces line 2101 :
                         * this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                         * With :
                         * MinecraftHook.dropOneItem(this.thePlayer);
                         */
                        methodNode.instructions.remove(insnNode.getPrevious());
                        methodNode.instructions.remove(insnNode.getNext());
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "dropOneItem", ASMLoadingPlugin.isObf ? "(Lbew;)V" : "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        methodNode.instructions.remove(insnNode);
                    }

                }
            }
        }

        return classNode;
    }

    private InsnList updateCurrentSlotInsnList() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/MinecraftHook", "updateCurrentSlot", ASMLoadingPlugin.isObf ? "(Lave;)V" : "(Lnet/minecraft/client/Minecraft;)V", false));
        return list;
    }
}
