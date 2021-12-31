package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityRendererTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            for (MethodNode methodNode : classNode.methods) {

                if (methodNode.name.equals(MixinLoader.isObf ? "g" : "updateLightmap") && methodNode.desc.equals("(F)V")
                        || methodNode.name.equals(MixinLoader.isObf ? "i" : "updateFogColor") && methodNode.desc.equals("(F)V")) {

                    for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                        if (insnNode.getOpcode() == GETSTATIC && insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).name.equals(MixinLoader.isObf ? "r" : "nightVision")) {
                            AbstractInsnNode nextNode = insnNode.getNext();
                            if (nextNode.getOpcode() == INVOKEVIRTUAL && nextNode instanceof MethodInsnNode && ((MethodInsnNode) nextNode).name.equals(MixinLoader.isObf ? "a" : "isPotionActive")) {
                                AbstractInsnNode secondNode = nextNode.getNext();
                                if (secondNode.getOpcode() == IFEQ && secondNode instanceof JumpInsnNode) {
                                    LabelNode labelNode = ((JumpInsnNode) secondNode).label;
                                    InsnList list = new InsnList();
                                    list.add(new JumpInsnNode(IFEQ, labelNode));
                                    list.add(new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", "keepNightVisionEffect", "Z"));
                                    methodNode.instructions.insertBefore(secondNode, list);

                                }
                            }
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
