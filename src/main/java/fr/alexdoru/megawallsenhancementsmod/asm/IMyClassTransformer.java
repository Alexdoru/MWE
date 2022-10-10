package fr.alexdoru.megawallsenhancementsmod.asm;

import org.objectweb.asm.tree.ClassNode;

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

}
