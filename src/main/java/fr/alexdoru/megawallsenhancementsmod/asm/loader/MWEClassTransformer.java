package fr.alexdoru.megawallsenhancementsmod.asm.loader;

import fr.alexdoru.megawallsenhancementsmod.asm.transformers.*;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.OrangeSimpleMod_SprintRenderer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodReloaded_CustomSidebarTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.transformers.externalmods.SidebarmodRevamp_GuiSidebarTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MWEClassTransformer implements IClassTransformer {

    private File outputDir = null;
    private final HashMap<String, List<MWETransformer>> transformers = new HashMap<>();

    /**
     * Register the MWETransformer(s) here
     */
    public MWEClassTransformer() {
        registerTransformer(new ChatComponentStyleTransformer_ChatHeads());
        registerTransformer(new ChatComponentTextTransformer_ChatHeads());
        registerTransformer(new CommandHandlerTransformer());
        registerTransformer(new EntityArrowTransformer());
        registerTransformer(new EntityFXTransformer());
        registerTransformer(new EntityOtherPlayerMPTransformer_FixAutoblockBypass());
        registerTransformer(new EntityLivingBaseTransformer_RotationTracker());
        registerTransformer(new EntityOtherPlayerMPTransformer_PositionTracker());
        registerTransformer(new EntityPlayerTransformer());
        registerTransformer(new EntityPlayerSPTransformer_CommandListener());
        //registerTransformer(new EntityPlayerSPTransformer_HealthListener());
        registerTransformer(new EntityRendererTransformer_NightVision());
        registerTransformer(new EntityRendererTransformer_RenderOverlayHook());
        registerTransformer(new GuiChatTransformer());
        registerTransformer(new GuiContainerTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new GuiIngameTransformer_CancelHunger());
        registerTransformer(new GuiIngameTransformer_Sidebar());
        registerTransformer(new GuiNewChatTransformer_ChatHeads());
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
        registerTransformer(new GuiUtilRenderComponentsTransformer_ChatHeads());
        registerTransformer(new HitboxRenderTransformer());
        registerTransformer(new LayerArmorBaseTransformer_HitColor());
        registerTransformer(new LayerArrowTransformer());
        registerTransformer(new MinecraftTransformer_DebugMessages());
        registerTransformer(new MinecraftTransformer_DropProtection());
        registerTransformer(new MinecraftTransformer_WarpProtection());
        //registerTransformer(new NetHandlerPlayClientTransformer_BlockBreakAnimListener());
        //registerTransformer(new NetHandlerPlayClientTransformer_EquipmentListener());
        registerTransformer(new NetHandlerPlayClientTransformer_PlayerMapTracker());
        registerTransformer(new NetHandlerPlayClientTransformer_TeamsListener());
        //registerTransformer(new NetHandlerPlayClientTransformer_TeleportListener());
        registerTransformer(new NetworkManagerTransformer_PacketListener());
        registerTransformer(new NetworkPlayerInfo$1Transformer_ChatHeads());
        registerTransformer(new NetworkPlayerInfoTransformer());
        registerTransformer(new NetworkPlayerInfoTransformer_ChatHeads());
        registerTransformer(new RendererLivingEntityTransformer_NametagRange());
        registerTransformer(new RendererLivingEntity_HitColor());
        registerTransformer(new RenderGlobalTransformer_LimitDroppedItems());
        registerTransformer(new RenderGlobalTransformer_ListenDestroyedBlocks());
        registerTransformer(new RenderManagerTransformer());
        registerTransformer(new RenderPlayerTransformer_ColoredHealth());
        registerTransformer(new RenderPlayerTransformer_RenegadeArrowCount());
        registerTransformer(new S19PacketEntityStatusTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new ScorePlayerTeamTransformer());
        registerTransformer(new OrangeSimpleMod_SprintRenderer());
        registerTransformer(new SidebarmodReloaded_CustomSidebarTransformer());
        registerTransformer(new SidebarmodRevamp_GuiSidebarTransformer());
        //registerTransformer(new WorldTransformer());
        if ("01/04".equals(new SimpleDateFormat("dd/MM").format(new Date().getTime())))
            registerTransformer(new RendererLivingEntityTransformer_AprilFun());
    }

    private void registerTransformer(MWETransformer classTransformer) {
        for (final String clazz : classTransformer.getTargetClassName()) {
            transformers.computeIfAbsent(clazz, k -> new ArrayList<>()).add(classTransformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        final List<MWETransformer> transformerList = transformers.get(transformedName);
        if (transformerList == null) return basicClass;
        final long l = System.currentTimeMillis();
        try {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            boolean skip = true;
            for (final MWETransformer transformer : transformerList) {
                final InjectionStatus status = new InjectionStatus();
                transformer.transform(classNode, status);
                if (status.isSkippingTransformation()) {
                    debugLog("Skipping application of " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
                    continue;
                }
                skip = false;
                if (status.isTransformationSuccessful()) {
                    debugLog("Applied " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
                } else {
                    ASMLoadingPlugin.logger.error("Class transformation incomplete, transformer " + stripClassName(transformer.getClass().getName()) + " missing " + status.getInjectionCount() + " injections in " + transformedName);
                }
            }
            if (skip) {
                debugLog("Skipping transformation of " + transformedName);
                return basicClass;
            }
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            basicClass = classWriter.toByteArray();
        } catch (Throwable t) {
            ASMLoadingPlugin.logger.error("Failed to transform " + transformedName, t);
        }
        final long l2 = System.currentTimeMillis() - l;
        debugLog("Transformed " + transformedName + " in " + l2 + "ms");
        saveTransformedClass(basicClass, transformedName);
        return basicClass;
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static void debugLog(String msg) {
        if (ASMLoadingPlugin.isObf()) {
            ASMLoadingPlugin.logger.debug(msg);
        } else {
            ASMLoadingPlugin.logger.info(msg);
        }
    }

    private void emptyClassOutputFolder() {
        outputDir = new File(Launch.minecraftHome, "ASM_MWE");
        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException ignored) {}
        if (!outputDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdirs();
        }
    }

    private void saveTransformedClass(final byte[] data, final String transformedName) {
        if (ASMLoadingPlugin.isObf()) {
            return;
        }
        if (outputDir == null) {
            emptyClassOutputFolder();
        }
        final File outFile = new File(outputDir, transformedName.replace('.', File.separatorChar) + ".class");
        final File outDir = outFile.getParentFile();
        if (!outDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outDir.mkdirs();
        }
        if (outFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outFile.delete();
        }
        try {
            final OutputStream output = Files.newOutputStream(outFile.toPath());
            output.write(data);
            output.close();
        } catch (IOException e) {
            ASMLoadingPlugin.logger.error("Could not save transformed class " + transformedName, e);
        }
    }

}
