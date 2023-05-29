package fr.alexdoru.megawallsenhancementsmod.asm;

import fr.alexdoru.megawallsenhancementsmod.asm.transformers.*;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.OrangeSimpleMod_SprintRenderer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodReloaded_CustomSidebarTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodRevamp_GuiSidebarTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ClassTransformer implements IClassTransformer {

    private final HashMap<String, List<IMyClassTransformer>> transformerHashMap = new HashMap<>();

    /**
     * Register the IMyClassTransformer(s) here
     */
    public ClassTransformer() {
        registerTransformer(new CommandHandlerTransformer());
        registerTransformer(new EntityArrowTransformer());
        registerTransformer(new EntityFXTransformer());
        registerTransformer(new EntityPlayerTransformer());
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new GuiChatTransformer());
        registerTransformer(new GuiContainerTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new GuiIngameTransformer_CancelHunger());
        registerTransformer(new GuiIngameTransformer_Sidebar());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer_ColoredScores());
        registerTransformer(new GuiPlayerTabOverlayTransformer_FinalKills());
        registerTransformer(new GuiPlayerTabOverlayTransformer_FixMissplacedDrawRect());
        registerTransformer(new GuiPlayerTabOverlayTransformer_HideHeaderFooter());
        registerTransformer(new GuiPlayerTabOverlayTransformer_HidePing());
        registerTransformer(new GuiPlayerTabOverlayTransformer_LongerTab());
        registerTransformer(new GuiPlayerTabOverlayTransformer_PlayerCount());
        registerTransformer(new GuiScreenBookTransformer());
        registerTransformer(new GuiScreenTransformer());
        registerTransformer(new LayerArmorBaseTransformer_HitColor());
        registerTransformer(new LayerArrowTransformer());
        registerTransformer(new MinecraftTransformer_DebugMessages());
        registerTransformer(new MinecraftTransformer_DropProtection());
        registerTransformer(new MinecraftTransformer_WarpProtection());
        //registerTransformer(new NetHandlerPlayClientTransformer_BlockBreakAnimListener());
        //registerTransformer(new NetHandlerPlayClientTransformer_TeleportListener());
        registerTransformer(new NetHandlerPlayClientTransformer_TeamsListener());
        registerTransformer(new NetHandlerPlayClientTransformer_PlayerMapTracker());
        registerTransformer(new NetworkPlayerInfoTransformer());
        registerTransformer(new RendererLivingEntityTransformer_NametagRange());
        registerTransformer(new RendererLivingEntity_HitColor());
        registerTransformer(new RenderGlobalTransformer_LimitDroppedItems());
        registerTransformer(new RenderGlobalTransformer_ListenDestroyedBlocks());
        registerTransformer(new RenderManagerTransformer());
        registerTransformer(new RenderPlayerTransformer_ColoredHealth());
        registerTransformer(new RenderPlayerTransformer_RenegadeArrowCount());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new ScorePlayerTeamTransformer());
        registerTransformer(new OrangeSimpleMod_SprintRenderer());
        registerTransformer(new SidebarmodReloaded_CustomSidebarTransformer());
        registerTransformer(new SidebarmodRevamp_GuiSidebarTransformer());
        registerTransformer(new WorldTransformer());
        if ("01/04".equals(new SimpleDateFormat("dd/MM").format(new Date().getTime())))
            registerTransformer(new RendererLivingEntityTransformer_AprilFun());
    }

    private void registerTransformer(IMyClassTransformer classTransformer) {
        final List<IMyClassTransformer> list = transformerHashMap.get(classTransformer.getTargetClassName());
        if (list == null) {
            final List<IMyClassTransformer> newList = new ArrayList<>();
            newList.add(classTransformer);
            transformerHashMap.put(classTransformer.getTargetClassName(), newList);
        } else {
            list.add(classTransformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        final List<IMyClassTransformer> transformerList = transformerHashMap.get(transformedName);
        if (transformerList == null) return basicClass;
        final long l = System.currentTimeMillis();
        for (final IMyClassTransformer transformer : transformerList) {
            try {
                final ClassNode classNode = new ClassNode();
                final ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);
                final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                final InjectionStatus status = new InjectionStatus();
                transformer.transform(classNode, status);
                if (status.isSkippingTransformation()) {
                    continue;
                }
                classNode.accept(classWriter);
                if (!status.isTransformationSuccessfull()) {
                    ASMLoadingPlugin.logger.error("Class transformation incomplete, transformer " + stripClassName(transformer.getClass().getName()) + " missing " + status.getInjectionCount() + " injections in " + transformedName);
                } else {
                    debugLog("Applied " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
                }
                basicClass = classWriter.toByteArray();
            } catch (Exception e) {
                ASMLoadingPlugin.logger.error("Failed to apply " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
            }
        }
        final long l2 = System.currentTimeMillis() - l;
        debugLog("Transformed " + transformedName + " in " + l2 + "ms");
        return basicClass;
    }

    private String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private void debugLog(String msg) {
        if (ASMLoadingPlugin.isObf) {
            ASMLoadingPlugin.logger.debug(msg);
        } else {
            ASMLoadingPlugin.logger.info(msg);
        }
    }

}
