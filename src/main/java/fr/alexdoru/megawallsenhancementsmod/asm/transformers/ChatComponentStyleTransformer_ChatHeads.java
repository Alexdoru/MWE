package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class ChatComponentStyleTransformer_ChatHeads implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.util.ChatComponentStyle";
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
