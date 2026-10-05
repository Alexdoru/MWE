package fr.alexdoru.mwe.features.ctf;

import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class FlagObjective {

    private static final ResourceLocation FLAG_TEXTURE = new ResourceLocation("mwe", "flag_128x128.png");
    private static final double LABEL_Y_SPACING = 9F * 1.15F * 0.02666667F;
    private static final int PARTICLES_PER_SPAWN = 32;
    private static final double PARTICLE_ANGLE_OFFSET = 2D * Math.PI / (double) PARTICLES_PER_SPAWN;
    private static final int MAX_CAPTURE_POINT = 800;
    private static final int MAX_PROGRESS_PER_TICK = 40;
    private static final int CAPTURE_RATE_PER_PLAYER = 10;

    private final MWMap map;
    private final double startingX;
    private final double startingY;
    private final double startingZ;
    private final double radius;
    private final BlockPos centerPos;

    private final List<String> inWorldLabel = new ArrayList<>(Arrays.asList("§7Capture the", "§7objective"));
    private final Map<MWTeam, Integer> playersInRadius = new EnumMap<>(MWTeam.class);
    private MWTeam capturingTeam;
    private int captureProgress;
    private int particleColor = 0xFFFFFF;
    private double particleAngle;
    private int tickCount;

    public FlagObjective(MWMap map, Random rand) {
        this(map, computeStartingPos(map, rand));
    }

    private FlagObjective(MWMap map, double[] xz) {
        this(map, xz[0], xz[1]);
    }

    FlagObjective(MWMap map, double x, double z) {
        this.map = map;
        this.startingX = x;
        this.startingY = map.surfaceLevel;
        this.startingZ = z;
        this.radius = isFlagAtMid() ? 10 : 8;
        this.centerPos = new BlockPos(startingX, startingY, startingZ);
    }

    private static double @NotNull [] computeStartingPos(MWMap map, Random rand) {
        //             NORTH -Z
        //   WEST -X             EAST +X
        //             SOUTH +Z
        final int lengthPad = 5;
        final int widthPad = 2;
        final int deapth = 4;
        final int westEastLen = (int) map.eastLimit - (int) map.westLimit;
        final int northSouthLen = (int) map.southLimit - (int) map.northLimit;
        final double x, z;
        switch (rand.nextInt(5)) {
            case 0: { // North
                x = map.westLimit + lengthPad + (rand.nextInt(westEastLen - 2 * lengthPad));
                z = map.northLimit - widthPad - (rand.nextInt(deapth));
                break;
            }
            case 1: { // East
                x = map.eastLimit + widthPad + (rand.nextInt(deapth));
                z = map.northLimit + lengthPad + (rand.nextInt(northSouthLen - 2 * lengthPad));
                break;
            }
            case 2: { // South
                x = map.westLimit + lengthPad + (rand.nextInt(westEastLen - 2 * lengthPad));
                z = map.southLimit + widthPad + (rand.nextInt(deapth));
                break;
            }
            case 3: { // West
                x = map.westLimit - widthPad - (rand.nextInt(deapth));
                z = map.northLimit + lengthPad + (rand.nextInt(northSouthLen - 2 * lengthPad));
                break;
            }
            case 4:
            default: { // Middle
                x = map.westLimit + rand.nextInt(westEastLen);
                z = map.northLimit + rand.nextInt(northSouthLen);
                break;
            }
        }
        return new double[]{x, z};
    }

    @SubscribeEvent
    public void on(RenderWorldLastEvent event) {
        double renderY = startingY + 2.5D;
        if (isPlayerInRange(Minecraft.getMinecraft().thePlayer, 64D)) {
            for (int i = this.inWorldLabel.size() - 1; i >= 0; i--) {
                RenderHelper.renderLabelInWorld(this.inWorldLabel.get(i), startingX, renderY, startingZ, event.partialTicks);
                renderY += LABEL_Y_SPACING;
            }
        }
        if (MWEConfig.captureTheFlagIcon) {
            RenderHelper.renderTextureInWorld(FLAG_TEXTURE, startingX, renderY + 0.5D, startingZ, event.partialTicks);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && mc.theWorld != null && mc.thePlayer != null) {
            tickCount++;
            if (MWEConfig.captureTheFlagParticles && isPlayerInRange(mc.thePlayer, 48D)) {
                spawnCircleParticles(mc.theWorld);
            }
            if (tickCount % 5 == 0 && mc.theWorld.isAreaLoaded(centerPos, (int) this.radius)) {
                countPlayersInRadius(mc.theWorld);
                updateCaptureProgress();
                updateFlagAttributes(mc.thePlayer);
            }
        }
    }

    private void countPlayersInRadius(World world) {
        playersInRadius.clear();
        final char blue = MWTeam.BLUE.getColorChar();
        final char green = MWTeam.GREEN.getColorChar();
        final char red = MWTeam.RED.getColorChar();
        final char yellow = MWTeam.YELLOW.getColorChar();
        int bluePlayerCount = 0;
        int greenPlayerCount = 0;
        int redPlayerCount = 0;
        int yellowPlayerCount = 0;
        for (final EntityPlayer player : world.playerEntities) {
            if (isPlayerInRange(player, this.radius) && player.posY < this.startingY + 6D) {
                final char c = ((EntityPlayerAccessor) player).getPlayerTeamColor();
                if (c == blue) {
                    bluePlayerCount++;
                } else if (c == green) {
                    greenPlayerCount++;
                } else if (c == red) {
                    redPlayerCount++;
                } else if (c == yellow) {
                    yellowPlayerCount++;
                }
            }
        }
        if (bluePlayerCount > 0) playersInRadius.put(MWTeam.BLUE, bluePlayerCount);
        if (greenPlayerCount > 0) playersInRadius.put(MWTeam.GREEN, greenPlayerCount);
        if (redPlayerCount > 0) playersInRadius.put(MWTeam.RED, redPlayerCount);
        if (yellowPlayerCount > 0) playersInRadius.put(MWTeam.YELLOW, yellowPlayerCount);
    }

    private void updateCaptureProgress() {
        if (playersInRadius.isEmpty()) {
            return;
        }

        MWTeam leader = null;
        int firstScore = 0;
        int secondScore = 0;
        int capturingTeamScore = 0;

        for (final Map.Entry<MWTeam, Integer> entry : playersInRadius.entrySet()) {
            final MWTeam team = entry.getKey();
            final int count = entry.getValue();
            if (capturingTeam != null && team == capturingTeam) {
                capturingTeamScore = count;
            }
            if (count > firstScore) {
                secondScore = firstScore;
                leader = team;
                firstScore = count;
            } else if (count > secondScore) {
                secondScore = count;
            }
        }

        final boolean oneTeamLeads = firstScore > secondScore;

        if (oneTeamLeads) {
            this.updateCaptureProgress(leader, firstScore - secondScore);
        } else {
            if (capturingTeam == null || firstScore == capturingTeamScore) {
                return;
            }
            this.updateCaptureProgress(capturingTeam, -(firstScore - capturingTeamScore));
        }

    }

    private void updateCaptureProgress(@NotNull MWTeam capturingTeam, int playerDiff) {
        final int capturePoints = MathHelper.clamp_int(playerDiff * CAPTURE_RATE_PER_PLAYER, -MAX_PROGRESS_PER_TICK, MAX_PROGRESS_PER_TICK);
        if (this.capturingTeam == null) {
            this.capturingTeam = capturingTeam;
            captureProgress += capturePoints;
        } else if (this.capturingTeam == capturingTeam) {
            captureProgress += capturePoints;
        } else { // owningTeam != capturingTeam
            captureProgress -= capturePoints;
            if (captureProgress < 0) {
                this.capturingTeam = capturingTeam;
                captureProgress = Math.abs(captureProgress);
            } else if (captureProgress == 0) {
                this.capturingTeam = null;
            }
        }
        captureProgress = MathHelper.clamp_int(captureProgress, 0, MAX_CAPTURE_POINT);
    }

    private void updateFlagAttributes(EntityPlayer thePlayer) {
        this.particleColor = this.computeParticleColor();
        this.inWorldLabel.clear();
        if (this.capturingTeam == null) {
            this.inWorldLabel.add("§7Capture the");
            this.inWorldLabel.add("§7objective");
        } else {
            final MWTeam ownTeam = MWTeam.ofPlayer(thePlayer.getUniqueID());
            if (ownTeam != null) {
                if (this.capturingTeam == ownTeam) {
                    this.inWorldLabel.add("§7Defend");
                } else {
                    this.inWorldLabel.add("§7Attack");
                }
            }
            this.inWorldLabel.add(this.capturingTeam.formattedName());
            final int dots = 10 * captureProgress / MAX_CAPTURE_POINT;
            final String s = EnumChatFormatting.GRAY + "[" + this.capturingTeam.getColorPrefix() + StringUtil.getRepetitionOf('■', dots) + EnumChatFormatting.GRAY + StringUtil.getRepetitionOf('■', 10 - dots) + "]";
            this.inWorldLabel.add(s);
        }
    }

    private int computeParticleColor() {
        if (capturingTeam == null) {
            return 0xFFFFFF;
        } else {
            final int color = ColorUtil.getColorInt(capturingTeam.getColorChar());
            final int r = color >> 16 & 0xFF;
            final int g = color >> 8 & 0xFF;
            final int b = color & 0xFF;
            final int red = r + (0xFF - r) * (MAX_CAPTURE_POINT - captureProgress) / MAX_CAPTURE_POINT;
            final int green = g + (0xFF - g) * (MAX_CAPTURE_POINT - captureProgress) / MAX_CAPTURE_POINT;
            final int blue = b + (0xFF - b) * (MAX_CAPTURE_POINT - captureProgress) / MAX_CAPTURE_POINT;
            return red << 16 | green << 8 | blue;
        }
    }

    private void spawnCircleParticles(World world) {
        for (int i = 0; i < PARTICLES_PER_SPAWN; i++) {
            final double angle = particleAngle + i * PARTICLE_ANGLE_OFFSET;
            final double x = startingX + radius * Math.cos(angle);
            final double y = startingY + 0.1D;
            final double z = startingZ + radius * Math.sin(angle);
            Minecraft.getMinecraft().effectRenderer.addEffect(new FlagParticleEffect(world, x, y, z, particleColor));
        }
        particleAngle += PARTICLE_ANGLE_OFFSET / 10D;
        if (particleAngle > Math.PI) {
            particleAngle -= Math.PI;
        }
    }

    private boolean isPlayerInRange(@NotNull EntityPlayer player, double dist) {
        return player.getDistanceSq(startingX, player.posY, startingZ) < dist * dist;
    }

    public @Nullable FlagObjective getOppositeSideFlag() {
        if (!isFlagAtMid()) {
            final double middleX = (map.eastLimit - map.westLimit) / 2 + map.westLimit;
            final double middleZ = (map.southLimit - map.northLimit) / 2 + map.northLimit;
            final double oppositeX = (startingX - middleX) * (-1) + middleX;
            final double oppositeZ = (startingZ - middleZ) * (-1) + middleZ;
            return new FlagObjective(map, oppositeX, oppositeZ);
        }
        return null;
    }

    private boolean isFlagAtMid() {
        return this.map.isPosAtMiddle(startingX, startingZ);
    }

    public void printLocation() {
        if (this.map.isPosAtMiddle(startingX, startingZ)) {
            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + "A flag has spawned in the middle of the map.");
        } else {
            final MWTeam base = this.map.getBaseAt(startingX, startingZ);
            if (base != null) {
                ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + "A flag has spawned in front of the " + base.formattedName() + EnumChatFormatting.YELLOW + " base.");
            }
        }
    }

}