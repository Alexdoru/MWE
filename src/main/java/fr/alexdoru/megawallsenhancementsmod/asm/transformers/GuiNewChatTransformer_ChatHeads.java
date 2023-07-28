package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiNewChatTransformer_ChatHeads implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiNewChat";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$DRAWCHAT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GLSTATEMANAGER$ENABLEBLEND)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 10)); // chatline
                        list.add(new VarInsnNode(ILOAD, 14)); // l1 (alpha)
                        list.add(new VarInsnNode(ILOAD, 15)); // int i2
                        list.add(new VarInsnNode(ILOAD, 16)); // int j2
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiNewChatHook_ChatHeads"),
                                "preRenderStringCall",
                                "(L" + ClassMapping.CHATLINE + ";III)V",
                                false
                        ));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    } else if (checkMethodInsnNode(insnNode, MethodMapping.FONTRENDERER$DRAWSTRINGWITHSHADOW)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        methodNode.instructions.insert(nextNode, new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiNewChatHook_ChatHeads"),
                                "postRenderStringCall",
                                "()V",
                                false
                        ));
                        status.addInjection();
                    }
                }
            } else if (checkMethodNode(methodNode, MethodMapping.GUINEWCHAT$PRINTCHATMESSAGE)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        getHookClass("GuiNewChatHook_ChatHeads"),
                        "addHeadToMessage",
                        "(L" + ClassMapping.ICHATCOMPONENT + ";)V",
                        false
                ));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
