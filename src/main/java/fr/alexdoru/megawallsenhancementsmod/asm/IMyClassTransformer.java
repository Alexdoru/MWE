package fr.alexdoru.megawallsenhancementsmod.asm;

import org.objectweb.asm.tree.ClassNode;

public interface IMyClassTransformer {

    /**
     * Should return the de-obfuscated name of the targeted class. Example : "net.minecraft.client.Minecraft"
     */
    String getTargetClassName();

    /**
     * Should return the transformed ClassNode
     */
    ClassNode transform(ClassNode classNode);

}
