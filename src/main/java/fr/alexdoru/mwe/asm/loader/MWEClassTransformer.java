package fr.alexdoru.mwe.asm.loader;

import fr.alexdoru.mwe.asm.transformers.*;
import fr.alexdoru.mwe.asm.transformers.externalmods.OptifinePlayerItemsLayerTransformer_RemoveHats;
import fr.alexdoru.mwe.asm.transformers.externalmods.OrangeSprintRendererTransformer_HideHUD;
import fr.alexdoru.mwe.asm.transformers.externalmods.SidebarmodReloadedTransformer_CustomSidebarLines;
import fr.alexdoru.mwe.asm.transformers.externalmods.SidebarmodRevampTransformer_CustomSidebarLines;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
        registerTransformer(new C08PacketPlayerBlockPlacementTransformer());
        registerTransformer(new ChatComponentStyleTransformer_ChatHeads());
        registerTransformer(new ChatComponentTextTransformer_ChatHeads());
        registerTransformer(new CommandHandlerTransformer_CaseCommand());
        registerTransformer(new EntityArrowTransformer());
        registerTransformer(new EntityFXTransformer_ClearView());
        registerTransformer(new EntityLivingBaseTransformer_RotationTracker());
        registerTransformer(new EntityOtherPlayerMPTransformer_FixAutoblockBypass());
        registerTransformer(new EntityOtherPlayerMPTransformer_LeatherArmor());
        registerTransformer(new EntityOtherPlayerMPTransformer_PositionTracker());
        registerTransformer(new EntityPlayerTransformer());
        registerTransformer(new EntityPlayerTransformer_FixAutoblockBypass());
        registerTransformer(new EntityPlayerSPTransformer_CommandListener());
        registerTransformer(new EntityRendererTransformer_CancelNightVision());
        registerTransformer(new EntityRendererTransformer_RenderOverlayHook());
        registerTransformer(new GuiChatTransformer_Accessor());
        registerTransformer(new GuiChatTransformer_TabCompletePlayers());
        registerTransformer(new GuiContainerTransformer_SafeInventory());
        registerTransformer(new GuiIngameForgeTransformer_FixActionBar());
        registerTransformer(new GuiIngameTransformer_CancelHunger());
        registerTransformer(new GuiIngameTransformer_CustomSidebarLines());
        registerTransformer(new GuiNewChatTransformer_ChatHeads());
        registerTransformer(new GuiNewChatTransformer_CleanChatLogs());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer_ColoredScores());
        registerTransformer(new GuiPlayerTabOverlayTransformer_FinalKills());
        registerTransformer(new GuiPlayerTabOverlayTransformer_FixDrawRect());
        registerTransformer(new GuiPlayerTabOverlayTransformer_HideHeaderFooter());
        registerTransformer(new GuiPlayerTabOverlayTransformer_HidePing());
        registerTransformer(new GuiPlayerTabOverlayTransformer_LongerTab());
        registerTransformer(new GuiPlayerTabOverlayTransformer_PlayerCount());
        registerTransformer(new GuiScreenBookTransformer_NewNickKey());
        registerTransformer(new GuiScreenTransformer_CustomChatClickEvent());
        registerTransformer(new GuiUtilRenderComponentsTransformer_ChatHeads());
        registerTransformer(new HitboxRenderTransformer());
        registerTransformer(new LayerArmorBaseTransformer_HitColor());
        registerTransformer(new LayerArrowTransformer());
        registerTransformer(new MinecraftTransformer_DebugMessages());
        registerTransformer(new MinecraftTransformer_DropProtection());
        registerTransformer(new MinecraftTransformer_WarpProtection());
        registerTransformer(new NetHandlerPlayClientTransformer_BlockBreakAnimListener());
        registerTransformer(new NetHandlerPlayClientTransformer_BlockChangeListener());
        registerTransformer(new NetHandlerPlayClientTransformer_PlayerMapTracker());
        registerTransformer(new NetHandlerPlayClientTransformer_TeamsListener());
        registerTransformer(new NetworkManagerTransformer_ServerPacketListener());
        registerTransformer(new NetworkPlayerInfo$1Transformer_ChatHeads());
        registerTransformer(new NetworkPlayerInfoTransformer_ChatHeads());
        registerTransformer(new NetworkPlayerInfoTransformer_CustomTab());
        registerTransformer(new RendererLivingEntity_HitColor());
        registerTransformer(new RenderGlobalTransformer_LimitDroppedItems());
        registerTransformer(new RenderGlobalTransformer_ListenDestroyedBlocks());
        registerTransformer(new RenderManagerTransformer_Hitboxes());
        registerTransformer(new RenderPlayerTransformer_ColoredHealth());
        registerTransformer(new RenderPlayerTransformer_RenegadeArrowCount());
        registerTransformer(new S19PacketEntityStatusTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new ScorePlayerTeamTransformer());
        registerTransformer(new OptifinePlayerItemsLayerTransformer_RemoveHats());
        registerTransformer(new OrangeSprintRendererTransformer_HideHUD());
        registerTransformer(new SidebarmodReloadedTransformer_CustomSidebarLines());
        registerTransformer(new SidebarmodRevampTransformer_CustomSidebarLines());
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
        if (!ASMLoadingPlugin.dumpClasses()) {
            return;
        }
        if (outputDir == null) {
            emptyClassOutputFolder();
        }
        final String fileName = transformedName.replace('.', File.separatorChar);
        final File classFile = new File(outputDir, fileName + ".class");
        final File bytecodeFile = new File(outputDir, fileName + "_BYTE.txt");
        final File asmifiedFile = new File(outputDir, fileName + "_ASM.txt");
        final File outDir = classFile.getParentFile();
        if (!outDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outDir.mkdirs();
        }
        if (classFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            classFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(classFile.toPath())) {
            output.write(data);
            ASMLoadingPlugin.logger.info("Saved transformed class (byte[]) to " + classFile.toPath());
        } catch (IOException e) {
            ASMLoadingPlugin.logger.error("Could not save transformed class (byte[]) " + transformedName, e);
        }
        if (bytecodeFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            bytecodeFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(bytecodeFile.toPath())) {
            final ClassReader classReader = new ClassReader(data);
            classReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(output)), 0);
            ASMLoadingPlugin.logger.info("Saved transformed class (bytecode) to " + bytecodeFile.toPath());
        } catch (IOException e) {
            ASMLoadingPlugin.logger.error("Could not save transformed class (bytecode) " + transformedName, e);
        }
        if (asmifiedFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            asmifiedFile.delete();
        }
        try (final OutputStream output = Files.newOutputStream(asmifiedFile.toPath())) {
            final ClassReader classReader = new ClassReader(data);
            classReader.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(output)), 0);
            ASMLoadingPlugin.logger.info("Saved transformed class (ASM) to " + asmifiedFile.toPath());
        } catch (IOException e) {
            ASMLoadingPlugin.logger.error("Could not save transformed class (ASM) " + transformedName, e);
        }
    }

}
