package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.mwe.asm.loader.InjectionStatus;
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
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
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
