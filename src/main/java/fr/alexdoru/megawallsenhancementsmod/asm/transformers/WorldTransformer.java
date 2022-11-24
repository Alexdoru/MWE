package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class WorldTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.world.World";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.WORLD$UPDATEENTITYWITHOPTIONALFORCE)) {
                AbstractInsnNode latestAload0 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        latestAload0 = insnNode;
                    } else if (latestAload0 != null && checkMethodInsnNode(insnNode, MethodMapping.PROFILER$ENDSECTION)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("WorldHook"), "performChecksOnEntity", "(L" + ClassMapping.WORLD + ";L" + ClassMapping.ENTITY + ";)V", false));
                        methodNode.instructions.insertBefore(latestAload0, list);
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

}
