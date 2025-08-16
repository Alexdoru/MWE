package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class ChatComponentStyleTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.util.ChatComponentStyle"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        return !MWELoadingPlugin.isFeatherPresent();
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.CHATCOMPONENTSTYLE$APPENDSIBLING)) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ChatComponentStyleHook_ChatHeads"), "transferHeadToComponent", "(L" + ClassMapping.ICHATCOMPONENT + ";L" + ClassMapping.ICHATCOMPONENT + ";)V", false));
                methodNode.instructions.insert(list);
                status.addInjection();
            }
        }
    }

}
