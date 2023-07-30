package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GuiChatTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUICHAT$SENDAUTOCOMPLETEREQUEST)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUICHAT$WAITINGONAUTOCOMPLETE));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiChatHook"), "autoComplete", "(ZLjava/lang/String;Ljava/lang/String;)Z", false));
                list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.GUICHAT$WAITINGONAUTOCOMPLETE));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
            if (checkMethodNode(methodNode, MethodMapping.GUICHAT$ONAUTOCOMPLETERESPONSE)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, GETSTATIC, FieldMapping.CLIENTCOMMANDHANDLER$INSTANCE)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiChatHook"), "getLatestAutoComplete", "([Ljava/lang/String;)[Ljava/lang/String;", false));
                        list.add(new VarInsnNode(ASTORE, 1));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
