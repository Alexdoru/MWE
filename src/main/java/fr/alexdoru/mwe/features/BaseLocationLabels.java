package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class BaseLocationLabels {

    private static final double HEIGHT_ABOVE_SURFACE = 32D;
    // Labels further than this get pulled closer along the line of sight so they aren't cut by the far clipping plane
    private static final double MAX_RENDER_DISTANCE = 64D;
    private static final float SIZE_PER_BLOCK = 0.15F;
    private static final float MIN_SIZE = 1F;

    private final IScoreboardParser parser = ScoreboardTracker.getParser();
    private MWMap currentMap;

    @SubscribeEvent
    public void onMapEvent(MapEvent event) {
        currentMap = MWMap.fromName(event.mapName);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (currentMap != null && this.isActive()) {
            final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null || !isHoldingCompass(player)) {
                return;
            }

            final MWMap map = currentMap;
            final double centerX = (map.westLimit + map.eastLimit) / 2D;
            final double centerZ = (map.northLimit + map.southLimit) / 2D;
            final double widthX = map.eastLimit - map.westLimit;
            final double widthZ = map.southLimit - map.northLimit;
            final double y = map.surfaceLevel + HEIGHT_ABOVE_SURFACE;

            final Vec3 look = player.getLook(event.partialTicks);
            renderBaseLabel(player, look, map.northBase, centerX, y, map.northLimit - widthZ / 2D, event.partialTicks);
            renderBaseLabel(player, look, map.eastBase, map.eastLimit + widthX / 2D, y, centerZ, event.partialTicks);
            renderBaseLabel(player, look, map.southBase, centerX, y, map.southLimit + widthZ / 2D, event.partialTicks);
            renderBaseLabel(player, look, map.westBase, map.westLimit - widthX / 2D, y, centerZ, event.partialTicks);
        }
    }

    private boolean isActive() {
        return MWEConfig.baseLocationLabels && this.parser.isInMwGame() && (MWEConfig.baseLocationLabelsInDeathmatch || !this.parser.isDeathmatch());
    }

    private static void renderBaseLabel(EntityPlayer player, Vec3 look, MWTeam team, double x, double y, double z, float partialTicks) {
        final double camX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double camY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double camZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        final double dx = x - camX;
        final double dy = y - camY;
        final double dz = z - camZ;
        if (look.xCoord * dx + look.yCoord * dy + look.zCoord * dz <= 0D) {
            return;
        }
        final double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        final double renderDistance = Math.min(distance, MAX_RENDER_DISTANCE);
        final double ratio = distance == 0D ? 0D : renderDistance / distance;

        final String label = team.getColorPrefix() + team.getName() + " Base";
        final float size = Math.max(MIN_SIZE, (float) renderDistance * SIZE_PER_BLOCK);
        RenderHelper.renderLabelInWorld(label, camX + dx * ratio, camY + dy * ratio, camZ + dz * ratio, partialTicks, size);
    }

    private static boolean isHoldingCompass(EntityPlayer player) {
        final ItemStack stack = player.getHeldItem();
        return stack != null && stack.getItem() == Items.compass;
    }

}
