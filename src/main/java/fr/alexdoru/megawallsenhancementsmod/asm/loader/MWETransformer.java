package fr.alexdoru.megawallsenhancementsmod.asm.loader;

import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public interface MWETransformer {

    /**
     * Returns the de-obfuscated name of the targeted class.
     * Example : "net.minecraft.client.Minecraft"
     */
    String getTargetClassName();

    /**
     * Returns the transformed ClassNode
     */
    void transform(ClassNode classNode, InjectionStatus status);

    default void addInterface(ClassNode classNode, String interfaceName) {
        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessors/" + interfaceName);
    }

    default String getHookClass(String className) {
        return "fr/alexdoru/megawallsenhancementsmod/asm/hooks/" + className;
    }

    default void nodeToString(AbstractInsnNode insnNode) {
        if (insnNode instanceof FieldInsnNode) {
            final FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNode;
            ASMLoadingPlugin.logger.info("FieldInsnNode :"
                    + " opcode '" + fieldInsnNode.getOpcode()
                    + "' name '" + fieldInsnNode.name
                    + "' owner '" + fieldInsnNode.owner
                    + "' desc '" + fieldInsnNode.desc + "'");
        } else if (insnNode instanceof MethodInsnNode) {
            final MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
            ASMLoadingPlugin.logger.info("MethodInsnNode :"
                    + " opcode '" + methodInsnNode.getOpcode()
                    + "' name '" + methodInsnNode.name
                    + "' owner '" + methodInsnNode.owner
                    + "' desc '" + methodInsnNode.desc + "'");
        }
    }

    default boolean isConstructorMethod(MethodNode methodNode) {
        return methodNode.name.startsWith("<init>");
    }

    default boolean checkMethodNode(MethodNode methodNode, MethodMapping mapping) {
        return methodNode.name.equals(mapping.name) && methodNode.desc.equals(mapping.desc);
    }

    default boolean checkInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof InsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkTypeInsnNode(AbstractInsnNode insnNode, int opcode, ClassMapping desc) {
        return insnNode instanceof TypeInsnNode && insnNode.getOpcode() == opcode && ((TypeInsnNode) insnNode).desc.equals(desc.toString());
    }

    default boolean checkIntInsnNode(AbstractInsnNode insnNode, int opcode, int operand) {
        return insnNode instanceof IntInsnNode && insnNode.getOpcode() == opcode && ((IntInsnNode) insnNode).operand == operand;
    }

    default boolean checkJumpInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof JumpInsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkMethodInsnNode(AbstractInsnNode insnNode, MethodMapping method) {
        return insnNode instanceof MethodInsnNode && insnNode.getOpcode() == method.opcode
                && ((MethodInsnNode) insnNode).owner.equals(method.owner)
                && ((MethodInsnNode) insnNode).name.equals(method.name)
                && ((MethodInsnNode) insnNode).desc.equals(method.desc);
    }

    default boolean checkVarInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof VarInsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkVarInsnNode(AbstractInsnNode insnNode, int opcode, int index) {
        return insnNode instanceof VarInsnNode && insnNode.getOpcode() == opcode && ((VarInsnNode) insnNode).var == index;
    }

    default boolean checkFieldInsnNode(AbstractInsnNode insnNode, int opcode, FieldMapping field) {
        return insnNode instanceof FieldInsnNode && insnNode.getOpcode() == opcode
                && ((FieldInsnNode) insnNode).owner.equals(field.owner)
                && ((FieldInsnNode) insnNode).name.equals(field.name)
                && ((FieldInsnNode) insnNode).desc.equals(field.desc);
    }

    default boolean checkLdcInsnNode(AbstractInsnNode insnNode, Object obj) {
        return insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst != null && ((LdcInsnNode) insnNode).cst.equals(obj);
    }

    default boolean checkFrameNode(AbstractInsnNode insnNode, int type) {
        return insnNode instanceof FrameNode && ((FrameNode) insnNode).type == type;
    }

    default MethodInsnNode getNewMethodInsnNode(MethodMapping method) {
        return new MethodInsnNode(method.opcode, method.owner, method.name, method.desc, false);
    }

    default FieldInsnNode getNewFieldInsnNode(int opcode, FieldMapping field) {
        return new FieldInsnNode(opcode, field.owner, field.name, field.desc);
    }

    default FieldInsnNode getNewConfigFieldInsnNode(String name) {
        return new FieldInsnNode(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/config/ConfigHandler", name, "Z");
    }

    default void addGetterAndSetterMethod(ClassNode classNode, String methodName, ClassMapping owner, String fieldName, String fieldDesc, String methodSignature) {
        addGetterMethod(classNode, "get" + methodName, owner.toString(), fieldName, fieldDesc, methodSignature);
        addSetterMethod(classNode, "set" + methodName, owner.toString(), fieldName, fieldDesc, methodSignature);
    }

    default void addGetterMethod(ClassNode classNode, String methodName, FieldMapping field, String methodSignature) {
        addGetterMethod(classNode, methodName, field.owner, field.name, field.desc, methodSignature);
    }

    default void addGetterMethod(ClassNode classNode, String methodName, ClassMapping owner, String fieldName, String fieldDesc, String methodSignature) {
        addGetterMethod(classNode, methodName, owner.toString(), fieldName, fieldDesc, methodSignature);
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
            case "C":
            case "I":
            case "Z":
                return IRETURN;
            case "L":
                return LRETURN;
            default:
                return ARETURN;
        }
    }

    default void addSetterMethod(ClassNode classNode, String methodName, ClassMapping owner, String fieldName, String fieldDesc, String methodSignature) {
        addSetterMethod(classNode, methodName, owner.toString(), fieldName, fieldDesc, methodSignature);
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
            case "C":
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
