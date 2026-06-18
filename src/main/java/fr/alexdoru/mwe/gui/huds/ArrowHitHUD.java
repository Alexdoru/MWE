package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.interfaces.GuiNewChatAccessor;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import fr.alexdoru.mwe.features.NameFormatter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrowHitHUD extends AbstractRenderer {

    private static final Pattern PATTERN_ARROW_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP!");
    private static final Pattern PATTERN_RENEGADE_HIT = Pattern.compile("^(\\w+) is on ([0-9]*[.]?[0-9]+) HP, pinned by (\\d+) arrows.*");
    private static final Pattern PATTERN_LEAP_HIT = Pattern.compile("^You took ([0-9]*[.]?[0-9]+) recoil damage after traveling \\d+.\\d+ blocks!");
    private static final Pattern PATTERN_LEAP_DIRECT_HIT = Pattern.compile("^You landed a direct hit against (\\w+), taking ([0-9]*[.]?[0-9]+) recoil damage after traveling [0-9]*[.]?[0-9]+ blocks!");
    private static final Pattern PATTERN_REND = Pattern.compile("^Your Rend dealt [0-9]*[.]?[0-9]+ damage to \\w+.*");
    private static final Pattern PATTERN_REND_DAMAGE = Pattern.compile("([0-9]*[.]?[0-9]+) damage to (\\w+)");

    private String displayText = "";
    private long hitTime;
    private ResourceLocation skin;

    public ArrowHitHUD() {
        super(MWEConfig.arrowHitHUDPosition);
    }

    public boolean processMessage(ClientChatReceivedEvent event, String fmsg, String msg) {

        final Matcher matcherArrowHit = PATTERN_ARROW_HIT.matcher(msg);

        if (matcherArrowHit.matches()) {
            hitTime = System.currentTimeMillis();
            final String playername = matcherArrowHit.group(1);
            final String hitValue = matcherArrowHit.group(2);
            if (isDeadFromShot(playername, hitValue)) {
                displayText = EnumChatFormatting.GOLD + "Kill";
                skin = null;
            } else {
                displayText = getColor(hitValue) + hitValue;
                setPlayerHead(playername);
            }
            final String s = ScoreboardTracker.isInMwGame() ? fmsg.replaceFirst(playername, NameFormatter.getFormattedNameWithoutIcons(playername)) : fmsg;
            event.message = new ChatComponentText(s);
            ChatUtil.addSkinToComponent(event.message, playername);
            return true;
        }

        final Matcher matcherRenegadeHit = PATTERN_RENEGADE_HIT.matcher(msg);

        if (matcherRenegadeHit.matches()) {
            hitTime = System.currentTimeMillis();
            final String playername = matcherRenegadeHit.group(1);
            final String health = matcherRenegadeHit.group(2);
            final String arrowsPinned = matcherRenegadeHit.group(3);
            MWE.INSTANCE().getRenegadeTracker().addArrowOnPlayer(playername, Integer.parseInt(arrowsPinned));
            final boolean canKill = Float.parseFloat(health) <= (Float.parseFloat(arrowsPinned)) * 2.0f;
            displayText = getColor(health) + health + EnumChatFormatting.GRAY + " (" + (canKill ? EnumChatFormatting.GOLD : EnumChatFormatting.GREEN) + arrowsPinned + EnumChatFormatting.GRAY + ")";
            setPlayerHead(playername);
            final String s = ScoreboardTracker.isInMwGame() ? fmsg.replaceFirst(playername, NameFormatter.getFormattedNameWithoutIcons(playername)) : fmsg;
            event.message = new ChatComponentText(s);
            ChatUtil.addSkinToComponent(event.message, playername);
            return true;
        }

        final Matcher matcherLeapHit = PATTERN_LEAP_HIT.matcher(msg);

        if (matcherLeapHit.matches()) {
            hitTime = System.currentTimeMillis() + 1000L;
            displayText = EnumChatFormatting.GREEN + "-" + 2f * Float.parseFloat(matcherLeapHit.group(1));
            skin = null;
            return true;
        }

        final Matcher matcherLeapDirectHit = PATTERN_LEAP_DIRECT_HIT.matcher(msg);

        if (matcherLeapDirectHit.matches()) {
            hitTime = System.currentTimeMillis() + 1000L;
            final String playername = matcherLeapDirectHit.group(1);
            displayText = EnumChatFormatting.GREEN + "-" + 2f * Float.parseFloat(matcherLeapDirectHit.group(2));
            skin = null;
            final String s = ScoreboardTracker.isInMwGame() ? fmsg.replaceFirst(playername, NameFormatter.getFormattedNameWithoutIcons(playername)) : fmsg;
            event.message = new ChatComponentText(s);
            ChatUtil.addSkinToComponent(event.message, playername);
            return true;
        }

        final Matcher matcherRend = PATTERN_REND.matcher(msg);

        if (matcherRend.matches()) {
            hitTime = System.currentTimeMillis() + 1000L;
            final Matcher matcherRend2 = PATTERN_REND_DAMAGE.matcher(msg);
            float totalDamage = 0f;
            String s = fmsg;
            while (matcherRend2.find()) {
                final float damage = Float.parseFloat(matcherRend2.group(1));
                final String playername = matcherRend2.group(2);
                totalDamage += damage;
                MWE.INSTANCE().getRenegadeTracker().removeArrowsFrom(playername, (int) (damage / 2));
                s = ScoreboardTracker.isInMwGame() ? s.replaceFirst(playername, NameFormatter.getFormattedNameWithoutIcons(playername)) : s;
            }
            if (!s.equals(fmsg)) event.message = new ChatComponentText(s);
            displayText = EnumChatFormatting.GREEN + "-" + totalDamage;
            skin = null;
            return true;
        }

        return false;

    }

    private void setPlayerHead(String playername) {
        final NetworkPlayerInfo netInfo = NetPlayerInfoTracker.getPlayerInfo(playername);
        if (netInfo != null && netInfo.hasLocationSkin()) skin = netInfo.getLocationSkin();
    }

    private boolean isDeadFromShot(String playername, String hpValue) {
        if (!hpValue.equals("0")) {
            return false;
        }
        final Minecraft mc = Minecraft.getMinecraft();
        final List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
        if (chatLines.isEmpty()) {
            return false;
        }
        final ChatLine chatLine = chatLines.get(0);
        final String chatMessage = EnumChatFormatting.getTextWithoutFormattingCodes(chatLine.getChatComponent().getUnformattedText()).replace("^\\[[RBGY]\\] ", "");
        final int updatedCounter = chatLine.getUpdatedCounter();
        final int currentUpdateCounter = mc.ingameGUI.getUpdateCounter();
        final int counterDiff = Math.abs(currentUpdateCounter - updatedCounter);
        return chatMessage.startsWith(playername) && counterDiff <= 4;
    }

    private EnumChatFormatting getColor(String healthPoints) {
        return ColorUtil.getHPColor(Minecraft.getMinecraft().thePlayer.getMaxHealth(), Float.parseFloat(healthPoints));
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Minecraft mc = Minecraft.getMinecraft();
        this.rendererPosition.updateAbsolutePosition(resolution);
        int x = this.rendererPosition.getAbsoluteRenderX();
        int y = this.rendererPosition.getAbsoluteRenderY() - mc.fontRendererObj.FONT_HEIGHT;
        drawCenteredString(mc.fontRendererObj, displayText, x, y, 0xFFFFFF);
        if (MWEConfig.showHeadOnArrowHitHUD && skin != null) {
            x -= 4;
            y += mc.fontRendererObj.FONT_HEIGHT + 1;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            mc.getTextureManager().bindTexture(skin);
            Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
        }
    }

    @Override
    public void renderDummy() {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        drawCenteredString(fr, EnumChatFormatting.GREEN + "20.0", this.rendererPosition.getAbsoluteRenderX(), this.rendererPosition.getAbsoluteRenderY() - fr.FONT_HEIGHT, 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && currentTimeMillis - hitTime < 1000L;
    }

}
