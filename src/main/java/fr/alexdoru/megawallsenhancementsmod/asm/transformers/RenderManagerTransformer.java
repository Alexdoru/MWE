package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RenderManagerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals(MixinLoader.isObf ? "b" : "renderDebugBoundingBox") && methodNode.desc.equals(MixinLoader.isObf ? "(Lpk;DDDFF)V" : "(Lnet/minecraft/entity/Entity;DDDFF)V")) {
                    for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                        if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst.equals(new Double("2.0"))) {
                            methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(new Double("3.0")));
                            methodNode.instructions.remove(insnNode);
                        }
                    }
                }
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
    }

}
