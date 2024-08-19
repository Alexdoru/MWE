package fr.alexdoru.mwe.asm.loader;

import fr.alexdoru.mwe.asm.transformers.*;
import fr.alexdoru.mwe.asm.transformers.externalmods.*;
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

    public MWEClassTransformer() {
        register(new C08PacketPlayerBlockPlacementTransformer());
        register(new ChatComponentStyleTransformer_ChatHeads());
        register(new ChatComponentTextTransformer_ChatHeads());
        register(new ChatLineTransformer_Accessor());
        register(new ClientCommandHandlerTransformer_FixSlash());
        register(new CommandHandlerTransformer_FixCaseCommand());
        register(new EntityArrowTransformer());
        register(new EntityFXTransformer_ClearView());
        register(new EntityLivingBaseTransformer_RotationTracker());
        register(new EntityOtherPlayerMPTransformer_FixAutoblockBypass());
        register(new EntityOtherPlayerMPTransformer_LeatherArmor());
        register(new EntityOtherPlayerMPTransformer_PositionTracker());
        register(new EntityPlayerTransformer());
        register(new EntityPlayerTransformer_FixAutoblockBypass());
        register(new EntityPlayerSPTransformer_CommandListener());
        register(new EntityRendererTransformer_CancelNightVision());
        register(new EntityRendererTransformer_RenderOverlayHook());
        register(new EntityWitherTransformer_Color());
        register(new GuiChatTransformer_Accessor());
        register(new GuiChatTransformer_CopyMessages());
        register(new GuiChatTransformer_SearchBox());
        register(new GuiChatTransformer_TabCompletePlayers());
        register(new GuiContainerTransformer_ListenClicks());
        register(new GuiContainerTransformer_SafeInventory());
        register(new GuiIngameForgeTransformer_FixActionBar());
        register(new GuiIngameTransformer_CancelHunger());
        register(new GuiIngameTransformer_CustomSidebarLines());
        register(new GuiNewChatTransformer_Accessor());
        register(new GuiNewChatTransformer_ChatHeads());
        register(new GuiNewChatTransformer_CleanChatLogs());
        register(new GuiNewChatTransformer_CopyMessages());
        register(new GuiNewChatTransformer_SearchBox());
        register(new GuiNewChatTransformer_LongerChat());
        register(new GuiPlayerTabOverlayTransformer_ColoredScores());
        register(new GuiPlayerTabOverlayTransformer_ColumnSpacing());
        register(new GuiPlayerTabOverlayTransformer_FinalKills());
        register(new GuiPlayerTabOverlayTransformer_FixDrawRect());
        register(new GuiPlayerTabOverlayTransformer_HideHeaderFooter());
        register(new GuiPlayerTabOverlayTransformer_HidePing());
        register(new GuiPlayerTabOverlayTransformer_LongerTab());
        register(new GuiPlayerTabOverlayTransformer_PlayerCount());
        register(new GuiScreenBookTransformer_NewNickKey());
        register(new GuiScreenTransformer_CustomChatClickEvent());
        register(new GuiScreenTransformer_Invoker());
        register(new GuiUtilRenderComponentsTransformer_ChatHeads());
        register(new HitboxRenderTransformer());
        register(new LayerArmorBaseTransformer_HitColor());
        register(new LayerArrowTransformer());
        register(new MinecraftTransformer_DebugMessages());
        register(new MinecraftTransformer_DropProtection());
        register(new MinecraftTransformer_WarpProtection());
        register(new NetHandlerPlayClientTransformer_BlockBreakAnimListener());
        register(new NetHandlerPlayClientTransformer_BlockChangeListener());
        register(new NetHandlerPlayClientTransformer_EntityMetadata());
        register(new NetHandlerPlayClientTransformer_PlayerMapTracker());
        register(new NetHandlerPlayClientTransformer_TeamsListener());
        register(new NetworkManagerTransformer_ServerPacketListener());
        register(new NetworkPlayerInfo$1Transformer_ChatHeads());
        register(new NetworkPlayerInfoTransformer_ChatHeads());
        register(new NetworkPlayerInfoTransformer_CustomTab());
        register(new RendererLivingEntityTransformer_HitColor());
        register(new RendererLivingEntityTransformer_ColorOutlines());
        register(new RenderGlobalTransformer_EntityOutlines());
        register(new RenderGlobalTransformer_LimitDroppedItems());
        register(new RenderGlobalTransformer_ListenDestroyedBlocks());
        register(new RenderManagerTransformer_Accessor());
        register(new RenderManagerTransformer_Hitboxes());
        register(new RenderPlayerTransformer_ColoredHealth());
        register(new RenderPlayerTransformer_RenegadeArrowCount());
        register(new S19PacketEntityStatusTransformer());
        register(new ScorePlayerTeamTransformer());
        register(new EntityCullingTransformer_FixOutlineCulling());
        register(new OptifinePlayerItemsLayerTransformer_RemoveHats());
        register(new OrangeSprintRendererTransformer_HideHUD());
        register(new SidebarmodReloadedTransformer_CustomSidebarLines());
        register(new SidebarmodRevampTransformer_CustomSidebarLines());
        if ("01/04".equals(new SimpleDateFormat("dd/MM").format(new Date().getTime())))
            register(new RendererLivingEntityTransformer_AprilFun());
    }

    private void register(MWETransformer transformer) {
        for (final String className : transformer.getTargetClassName()) {
            transformers.computeIfAbsent(className, k -> new ArrayList<>()).add(transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null || !transformers.containsKey(transformedName)) {
            return bytes;
        }
        final long l = System.nanoTime();
        try {
            final ClassReader classReader = new ClassReader(bytes);
            final ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            boolean transformed = false;
            for (final MWETransformer transformer : transformers.get(transformedName)) {
                final InjectionStatus status = new InjectionStatus();
                transformer.transform(classNode, status);
                if (status.isSkippingTransformation()) {
                    debugLog("Skipping application of " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
                    continue;
                }
                transformed = true;
                if (status.isTransformationSuccessful()) {
                    debugLog("Applied " + stripClassName(transformer.getClass().getName()) + " to " + transformedName);
                } else {
                    ASMLoadingPlugin.logger.error("Class transformation incomplete, transformer " + stripClassName(transformer.getClass().getName()) + " missing " + status.getInjectionCount() + " injections in " + transformedName);
                }
            }
            if (!transformed) {
                debugLog("Skipping transformation of " + transformedName);
                return bytes;
            }
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            final byte[] transformedBytes = classWriter.toByteArray();
            final long l2 = (System.nanoTime() - l) / 1_000_000L;
            debugLog("Transformed " + transformedName + " in " + l2 + "ms");
            saveTransformedClass(transformedBytes, transformedName);
            return transformedBytes;
        } catch (Throwable t) {
            ASMLoadingPlugin.logger.error("Failed to transform " + transformedName, t);
            return bytes;
        }
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static void debugLog(String msg) {
        if (ASMLoadingPlugin.classDump()) {
            ASMLoadingPlugin.logger.info(msg);
        } else {
            ASMLoadingPlugin.logger.debug(msg);
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
        if (!ASMLoadingPlugin.classDump() && !ASMLoadingPlugin.moreClassDump()) {
            return;
        }
        if (outputDir == null) {
            emptyClassOutputFolder();
        }
        final String fileName = transformedName.replace('.', File.separatorChar);
        writeClassFile(data, transformedName, fileName);
        if (ASMLoadingPlugin.moreClassDump()) {
            writeBytecodeFile(data, transformedName, fileName);
            writeASMFile(data, transformedName, fileName);
        }
    }

    private void writeClassFile(byte[] data, String transformedName, String fileName) {
        final File classFile = new File(outputDir, fileName + ".class");
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
    }

    private void writeBytecodeFile(byte[] data, String transformedName, String fileName) {
        final File bytecodeFile = new File(outputDir, fileName + "_BYTE.txt");
        final File outDir = bytecodeFile.getParentFile();
        if (!outDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outDir.mkdirs();
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
    }

    private void writeASMFile(byte[] data, String transformedName, String fileName) {
        final File asmifiedFile = new File(outputDir, fileName + "_ASM.txt");
        final File outDir = asmifiedFile.getParentFile();
        if (!outDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outDir.mkdirs();
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
