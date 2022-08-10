package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.asm.transformers.*;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodReloaded_CustomSidebar;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodRevamp_GuiSidebar;
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
        registerTransformer(new EntityArrowTransformer());
        registerTransformer(new EntityFXTransformer());
        registerTransformer(new EntityPlayerTransformer());
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new GameProfileTransformer());
        registerTransformer(new GuiContainerTransformer());
        registerTransformer(new GuiIngameTransformer());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new GuiScreenBookTransformer());
        registerTransformer(new GuiScreenTransformer());
        registerTransformer(new LayerArrowTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new NetworkPlayerInfoTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new RenderManagerTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new SidebarmodReloaded_CustomSidebar());
        registerTransformer(new SidebarmodRevamp_GuiSidebar());
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
            InjectionStatus status = new InjectionStatus();
            classTransformer.transform(classNode, status).accept(classWriter);
            byte[] transformedByteArray = classWriter.toByteArray();
            if (!status.isTransformationSuccessfull()) {
                ASMLoadingPlugin.logger.error("Class transformation incomplete : " + getClassName(classTransformer.getTargetClassName()) + " missing " + status.getAmount_of_injection() + " injections");
            }
            return transformedByteArray;
        } catch (Exception e) {
            ASMLoadingPlugin.logger.error("Failed to transform " + classTransformer.getTargetClassName());
            e.printStackTrace();
        }

        return basicClass;

    }

    private String getClassName(String targetClassName) {
        String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

}
