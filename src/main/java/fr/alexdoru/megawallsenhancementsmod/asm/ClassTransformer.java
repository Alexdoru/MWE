package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.asm.transformers.*;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodReloaded_CustomSidebar;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodRevamp_GuiSidebar;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.fkcountermod.KillCounterTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

public class ClassTransformer implements IClassTransformer {

    private final HashMap<String, IMyClassTransformer> transformerHashMap = new HashMap<>();

    /**
     * Register the IMyClassTransformer(s) here
     */
    public ClassTransformer() {
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new GuiIngameTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new NetworkPlayerInfoTransformer());
        registerTransformer(new RenderManagerTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new SidebarmodReloaded_CustomSidebar());
        registerTransformer(new SidebarmodRevamp_GuiSidebar());
        registerTransformer(new KillCounterTransformer());
    }

    private void registerTransformer(IMyClassTransformer classTransformer) {
        transformerHashMap.put(classTransformer.getTargetClassName(), classTransformer);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (basicClass == null) {
            return null;
        }

        IMyClassTransformer classTransformer = transformerHashMap.get(transformedName);

        if (classTransformer == null) {
            return basicClass;
        }

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classTransformer.transform(classNode).accept(classWriter);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;

    }

}
