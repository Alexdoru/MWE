package fr.alexdoru.mwe.api.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public interface IClassNodeTransformer extends Opcodes {

    /**
     * Returns the de-obfuscated names of the classes targeted by this transformer
     * <p>
     * Example : "net.minecraft.client.Minecraft"
     */
    String[] getTargetClassName();

    /**
     * Returns true if this transformer should apply
     */
    default boolean shouldApply(ClassNode classNode) {
        return true;
    }

    /**
     * Performs transformations to the ClassNode
     */
    void transform(ClassNode classNode, InjectionCallback status);

    /* ========= ASM helper methods ========= */

    default boolean isConstructorMethod(MethodNode methodNode) {
        return methodNode.name.startsWith("<init>");
    }

    default boolean checkMethodNode(MethodNode methodNode, IMethodInfo mapping) {
        return checkMethodNode(methodNode, mapping.methodName(), mapping.desc());
    }

    default boolean checkMethodNode(MethodNode methodNode, String name, String desc) {
        return methodNode.name.equals(name) && methodNode.desc.equals(desc);
    }

    default boolean checkTypeInsnNode(AbstractInsnNode insnNode, int opcode, IClassInfo desc) {
        return checkTypeInsnNode(insnNode, opcode, desc.internalName());
    }

    default boolean checkTypeInsnNode(AbstractInsnNode insnNode, int opcode, String desc) {
        return insnNode instanceof TypeInsnNode && insnNode.getOpcode() == opcode && ((TypeInsnNode) insnNode).desc.equals(desc);
    }

    default boolean checkInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof InsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkIntInsnNode(AbstractInsnNode insnNode, int opcode, int operand) {
        return insnNode instanceof IntInsnNode && insnNode.getOpcode() == opcode && ((IntInsnNode) insnNode).operand == operand;
    }

    default boolean checkJumpInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof JumpInsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkVarInsnNode(AbstractInsnNode insnNode, int opcode) {
        return insnNode instanceof VarInsnNode && insnNode.getOpcode() == opcode;
    }

    default boolean checkVarInsnNode(AbstractInsnNode insnNode, int opcode, int index) {
        return insnNode instanceof VarInsnNode && insnNode.getOpcode() == opcode && ((VarInsnNode) insnNode).var == index;
    }

    default boolean checkLdcInsnNode(AbstractInsnNode insnNode, Object obj) {
        return insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst != null && ((LdcInsnNode) insnNode).cst.equals(obj);
    }

    default boolean checkFrameNode(AbstractInsnNode insnNode, int type) {
        return insnNode instanceof FrameNode && ((FrameNode) insnNode).type == type;
    }

    default boolean checkMethodInsnNode(AbstractInsnNode insnNode, IMethodInfo method) {
        return checkMethodInsnNode(insnNode, method.opcode(), method.owner(), method.methodName(), method.desc());
    }

    default boolean checkMethodInsnNode(AbstractInsnNode insnNode, int opcode, String owner, String name, String desc) {
        return insnNode instanceof MethodInsnNode && insnNode.getOpcode() == opcode
                && ((MethodInsnNode) insnNode).owner.equals(owner)
                && ((MethodInsnNode) insnNode).name.equals(name)
                && ((MethodInsnNode) insnNode).desc.equals(desc);
    }

    default boolean checkFieldInsnNode(AbstractInsnNode insnNode, int opcode, IFieldInfo field) {
        return checkFieldInsnNode(insnNode, opcode, field.owner(), field.fieldName(), field.desc());
    }

    default boolean checkFieldInsnNode(AbstractInsnNode insnNode, int opcode, String owner, String name, String desc) {
        return insnNode instanceof FieldInsnNode && insnNode.getOpcode() == opcode
                && ((FieldInsnNode) insnNode).owner.equals(owner)
                && ((FieldInsnNode) insnNode).name.equals(name)
                && ((FieldInsnNode) insnNode).desc.equals(desc);
    }

    default MethodInsnNode getNewMethodInsnNode(IMethodInfo method) {
        return new MethodInsnNode(method.opcode(), method.owner(), method.methodName(), method.desc(), method.opcode() == INVOKEINTERFACE);
    }

    default FieldInsnNode getNewFieldInsnNode(int opcode, IFieldInfo field) {
        return new FieldInsnNode(opcode, field.owner(), field.fieldName(), field.desc());
    }

    default void addField(ClassNode classNode, int access, IFieldInfo field) {
        classNode.visitField(access, field.fieldName(), field.desc(), null, null);
    }

    default void addField(ClassNode classNode, int access, String fieldName, String fieldDesc) {
        classNode.visitField(access, fieldName, fieldDesc, null, null);
    }

    default void insertAfterLabel(MethodNode methodNode, LabelNode label, InsnList list) {
        AbstractInsnNode insn = label;
        while (insn.getNext() instanceof LineNumberNode || insn.getNext() instanceof FrameNode) {
            insn = insn.getNext();
        }
        methodNode.instructions.insert(insn, list);
    }

    /* ========= ASM getter/setter methods ========= */

    default void addGetterAndSetterMethod(ClassNode classNode, String methodName, IFieldInfo field) {
        addGetterMethod(classNode, "get" + methodName, field);
        addSetterMethod(classNode, "set" + methodName, field);
    }

    default void addGetterAndSetterMethod(ClassNode classNode, String methodName, IClassInfo owner, String fieldName, String fieldDesc) {
        addGetterMethod(classNode, "get" + methodName, owner.internalName(), fieldName, fieldDesc, null);
        addSetterMethod(classNode, "set" + methodName, owner.internalName(), fieldName, fieldDesc, null);
    }

    default void addGetterMethod(ClassNode classNode, String methodName, IFieldInfo field) {
        addGetterMethod(classNode, methodName, field.owner(), field.fieldName(), field.desc(), null);
    }

    default void addGetterMethod(ClassNode classNode, String methodName, IFieldInfo field, String methodSignature) {
        addGetterMethod(classNode, methodName, field.owner(), field.fieldName(), field.desc(), methodSignature);
    }

    default void addSetterMethod(ClassNode classNode, String methodName, IFieldInfo field) {
        addSetterMethod(classNode, methodName, field.owner(), field.fieldName(), field.desc(), null);
    }

    default void addSetterMethod(ClassNode classNode, String methodName, IFieldInfo field, String methodSignature) {
        addSetterMethod(classNode, methodName, field.owner(), field.fieldName(), field.desc(), methodSignature);
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
            case "J":
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
            case "C":
            case "I":
            case "Z":
                return ILOAD;
            case "J":
                return LLOAD;
            default:
                return ALOAD;
        }
    }

}
