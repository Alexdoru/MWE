package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.api.asm.IClassNodeTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

public interface MWETransformer extends IClassNodeTransformer {

    default void addInterface(ClassNode classNode, String interfaceName) {
        classNode.interfaces.add("fr/alexdoru/mwe/asm/interfaces/" + interfaceName);
    }

    default String getHookClass(String className) {
        return "fr/alexdoru/mwe/asm/hooks/" + className;
    }

    default FieldInsnNode getNewConfigFieldInsnNode(String name) {
        return new FieldInsnNode(GETSTATIC, "fr/alexdoru/mwe/config/MWEConfig", name, "Z");
    }

}
