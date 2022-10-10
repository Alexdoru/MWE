package fr.alexdoru.megawallsenhancementsmod.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public interface IMyClassTransformer {

    /**
     * Returns the de-obfuscated name of the targeted class.
     * Example : "net.minecraft.client.Minecraft"
     */
    String getTargetClassName();

    /**
     * Returns the transformed ClassNode
     */
    ClassNode transform(ClassNode classNode, InjectionStatus status);

    default void addInterface(ClassNode classNode, String interfaceName) {
        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessor/" + interfaceName);
    }

    default String getHookClass(String className) {
        return "fr/alexdoru/megawallsenhancementsmod/asm/hooks/" + className;
    }

    /**
     * Adds a getter method for the specified field in the classnode
     */
    default void addGetterMethod(ClassNode classNode, String methodName, String owner, String fieldName, String fieldDesc, String methodSignature) {
        final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, methodName, "()" + fieldDesc, methodSignature, null);
        mv.visitCode();
        final Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, owner, fieldName, fieldDesc);
        mv.visitInsn(getReturnOpcode(fieldDesc));
        final Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + owner + ";", null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /**
     * Returns the appropriate RETURN Opcode depending on the descriptor
     */
    default int getReturnOpcode(String fieldDesc) {
        switch (fieldDesc) {
            case "D":
                return DRETURN;
            case "F":
                return FRETURN;
            case "I":
            case "Z":
                return IRETURN;
            case "L":
                return LRETURN;
            default:
                return ARETURN;
        }
    }

    /**
     * Adds a setter method for the specified field in the classnode
     */
    default void addSetterMethod(ClassNode classNode, String methodName, String owner, String fieldName, String fieldDesc, String methodSignature) {
        final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, methodName, "(" + fieldDesc + ")V", methodSignature, null);
        mv.visitCode();
        final Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(getLoadOpcode(fieldDesc), 1);
        mv.visitFieldInsn(PUTFIELD, owner, fieldName, fieldDesc);
        final Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        final Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L" + owner + ";", null, l0, l2, 0);
        mv.visitLocalVariable(fieldName + "In", fieldDesc, null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * Returns the appropriate LOAD Opcode depending on the descriptor
     */
    default int getLoadOpcode(String fieldDesc) {
        switch (fieldDesc) {
            case "D":
                return DLOAD;
            case "F":
                return FLOAD;
            case "I":
            case "Z":
                return ILOAD;
            case "L":
                return LLOAD;
            default:
                return ALOAD;
        }
    }

}
