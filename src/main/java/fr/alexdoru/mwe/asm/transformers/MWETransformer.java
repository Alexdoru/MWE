package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.api.asm.IClassNodeTransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public interface MWETransformer extends IClassNodeTransformer {

    /* ========= MWE ASM helper methods ========= */

    default void addInterface(ClassNode classNode, String interfaceName) {
        classNode.interfaces.add("fr/alexdoru/mwe/asm/interfaces/" + interfaceName);
    }

    default String getHookClass(String className) {
        return "fr/alexdoru/mwe/asm/hooks/" + className;
    }

    default boolean checkMethodNode(MethodNode methodNode, MethodMapping mapping) {
        return checkMethodNode(methodNode, mapping.name, mapping.desc);
    }

    default boolean checkTypeInsnNode(AbstractInsnNode insnNode, int opcode, ClassMapping desc) {
        return checkTypeInsnNode(insnNode, opcode, desc.toString());
    }

    default boolean checkMethodInsnNode(AbstractInsnNode insnNode, MethodMapping method) {
        method.validate();
        return checkMethodInsnNode(insnNode, method.opcode, method.owner, method.name, method.desc);
    }

    default boolean checkFieldInsnNode(AbstractInsnNode insnNode, int opcode, FieldMapping field) {
        return checkFieldInsnNode(insnNode, opcode, field.owner, field.name, field.desc);
    }

    default MethodInsnNode getNewMethodInsnNode(MethodMapping method) {
        method.validate();
        return new MethodInsnNode(method.opcode, method.owner, method.name, method.desc, method.opcode == INVOKEINTERFACE);
    }

    default FieldInsnNode getNewFieldInsnNode(int opcode, FieldMapping field) {
        return new FieldInsnNode(opcode, field.owner, field.name, field.desc);
    }

    default FieldInsnNode getNewConfigFieldInsnNode(String name) {
        return new FieldInsnNode(GETSTATIC, "fr/alexdoru/mwe/config/MWEConfig", name, "Z");
    }

    default void addGetterAndSetterMethod(ClassNode classNode, String methodName, FieldMapping field, String methodSignature) {
        addGetterMethod(classNode, "get" + methodName, field, methodSignature);
        addSetterMethod(classNode, "set" + methodName, field, methodSignature);
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

    default void addSetterMethod(ClassNode classNode, String methodName, FieldMapping field, String methodSignature) {
        addSetterMethod(classNode, methodName, field.owner, field.name, field.desc, methodSignature);
    }

    default void addSetterMethod(ClassNode classNode, String methodName, ClassMapping owner, String fieldName, String fieldDesc, String methodSignature) {
        addSetterMethod(classNode, methodName, owner.toString(), fieldName, fieldDesc, methodSignature);
    }

}
