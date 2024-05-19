package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.MapUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.SkinUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoenixBondHUD extends AbstractRenderer {

    public static PhoenixBondHUD instance;
    private final List<PhxHealLine> textToRender = new ArrayList<>();
    private final List<PhxHealLine> dummyTextToRender = new ArrayList<>();
    private long timeStartRender;

    private static final Pattern INCOMING_BOND_PATTERN = Pattern.compile("^You were healed by (\\w{1,16})'s Spirit Bond for ([0-9]*[.]?[0-9]+)[❤♥]");
    private static final Pattern BOND_USED_PATTERN = Pattern.compile("^Your Spirit Bond healed");
    private static final Pattern PLAYERS_HEALED_PATTERN = Pattern.compile("(\\w{1,16})\\sfor\\s([0-9]*[.]?[0-9]+)[❤♥]");
    private static final Pattern SELF_HEALED_PATTERN = Pattern.compile("You are healed for\\s([0-9]*[.]?[0-9]+)[❤♥]");

    public PhoenixBondHUD() {
        super(ConfigHandler.phxBondHUDPosition);
        instance = this;
        this.dummyTextToRender.add(getLine(EnumChatFormatting.GREEN + "Player1", "10.5"));
        this.dummyTextToRender.add(getLine(EnumChatFormatting.GREEN + "Player2", "6.0"));
        this.dummyTextToRender.add(getLine(EnumChatFormatting.GREEN + "Player3", "3.5"));
        this.dummyTextToRender.add(getLine(EnumChatFormatting.GREEN + "Player4", "1.0"));
    }

    public boolean processMessage(IChatComponent component, String msg) {

        if (!ScoreboardTracker.isInMwGame) {
            return false;
        }

        final Matcher incomingBondMatcher = INCOMING_BOND_PATTERN.matcher(msg);

        if (incomingBondMatcher.find()) {
            textToRender.clear();
            timeStartRender = System.currentTimeMillis();
            final String playerHealingYou = incomingBondMatcher.group(1);
            final String amountHealed = incomingBondMatcher.group(2);
            this.textToRender.add(getLine(playerHealingYou, amountHealed));
            ChatUtil.addSkinToComponent(component, playerHealingYou);
            return true;
        }

        if (BOND_USED_PATTERN.matcher(msg).find()) {
            textToRender.clear();
            timeStartRender = System.currentTimeMillis();
            final Matcher selfHealedMatcher = SELF_HEALED_PATTERN.matcher(msg);
            if (selfHealedMatcher.find()) {
                final String amountHealed = selfHealedMatcher.group(1);
                this.textToRender.add(getLine(null, amountHealed));
                // to avoid matching "healed for x.x" as if "healed" was a playername
                msg = msg.replace(selfHealedMatcher.group(), "");
            }
            final Matcher playerHealedMatcher = PLAYERS_HEALED_PATTERN.matcher(msg);
            final Map<String, Float> map = new HashMap<>();
            while (playerHealedMatcher.find()) {
                final String playerName = playerHealedMatcher.group(1);
                final String amountHealed = playerHealedMatcher.group(2);
                map.put(playerName, Float.parseFloat(amountHealed));
            }
            final Map<String, Float> sortedMap = MapUtil.sortByDecreasingValue(map);
            for (final Map.Entry<String, Float> entry : sortedMap.entrySet()) {
                this.textToRender.add(getLine(entry.getKey(), entry.getValue().toString()));
            }
            return true;
        }

        return false;

    }

    private PhxHealLine getLine(String playername, String amountHealed) {
        final NetworkPlayerInfo networkPlayerInfo;
        final String formattedName;
        if (playername == null) { // myself
            networkPlayerInfo = mc.thePlayer == null ? null : mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            formattedName = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "You";
        } else {
            networkPlayerInfo = NetHandlerPlayClientHook.getPlayerInfo(playername);
            formattedName = getColoredName(playername, networkPlayerInfo);
        }
        return new PhxHealLine(
                getHealColor(Float.parseFloat(amountHealed)) + amountHealed + EnumChatFormatting.RED + "❤",
                SkinUtil.getSkin(networkPlayerInfo),
                formattedName);
    }

    private String getColoredName(String playername, NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null || networkPlayerInfo.getPlayerTeam() == null) {
            return SquadHandler.getSquadname(playername);
        }
        return networkPlayerInfo.getPlayerTeam().getColorPrefix() + SquadHandler.getSquadname(playername);
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition(resolution);
        this.renderPhxHUD(this.textToRender);
    }

    @Override
    public void renderDummy() {
        this.renderPhxHUD(this.dummyTextToRender);
    }

    private void renderPhxHUD(List<PhxHealLine> toRenderList) {
        int maxLeft = 0;
        int maxRight = 0;
        int maxTotal = 0;
        for (final PhxHealLine phxHealLine : toRenderList) {
            final int leftWidth = mc.fontRendererObj.getStringWidth(phxHealLine.hpHealed);
            final int rightWidth = mc.fontRendererObj.getStringWidth(phxHealLine.name);
            maxLeft = Math.max(maxLeft, leftWidth);
            maxRight = Math.max(maxRight, rightWidth);
            maxTotal = Math.max(maxTotal, leftWidth + rightWidth);
        }
        maxTotal += 10;
        final int x = this.guiPosition.getAbsoluteRenderX() - maxTotal / 2;
        int y = this.guiPosition.getAbsoluteRenderY() - mc.fontRendererObj.FONT_HEIGHT * toRenderList.size() / 2;
        GlStateManager.pushMatrix();
        {
            for (final PhxHealLine phxHealLine : toRenderList) {
                mc.fontRendererObj.drawStringWithShadow(phxHealLine.hpHealed, x + maxLeft - mc.fontRendererObj.getStringWidth(phxHealLine.hpHealed) - 1, y, 0xFFFFFF);
                mc.fontRendererObj.drawStringWithShadow(phxHealLine.name, x + maxLeft + 9, y, 0xFFFFFF);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                mc.getTextureManager().bindTexture(phxHealLine.skin);
                Gui.drawScaledCustomSizeModalRect(x + maxLeft, y, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                Gui.drawScaledCustomSizeModalRect(x + maxLeft, y, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                y += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showPhxBondHUD && timeStartRender + 5000L > currentTimeMillis;
    }

    private EnumChatFormatting getHealColor(float heal) {
        if (heal > 7.5) {
            return EnumChatFormatting.GREEN;
        } else if (heal > 4.5) {
            return EnumChatFormatting.YELLOW;
        } else if (heal > 2) {
            return EnumChatFormatting.GOLD;
        } else {
            return EnumChatFormatting.RED;
        }
    }

    private static class PhxHealLine {
        public final String hpHealed;
        public final ResourceLocation skin;
        public final String name;

        public PhxHealLine(String hpHealed, ResourceLocation skin, String name) {
            this.hpHealed = hpHealed;
            this.skin = skin;
            this.name = name;
        }
    }

}
