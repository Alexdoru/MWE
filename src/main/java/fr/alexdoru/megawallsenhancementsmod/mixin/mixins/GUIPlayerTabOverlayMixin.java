package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.objectweb.asm.Opcodes.GETSTATIC;

@Mixin(GuiPlayerTabOverlay.class)
public class GUIPlayerTabOverlayMixin extends Gui {

    @Final
    @Shadow
    private Minecraft mc;

    // Replaced by patcher 1.7.0
    ///**
    // * makes the tablist show up to 100 players instead of 80 in vanilla
    // *
    // * @author Alexdoru
    // * @reason I want to see more people on the tablist
    // */
    //@SuppressWarnings("MethodReturnAlwaysConstant")
    //@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 80))
    //private int changeMaxPlayers(int original) {
    //    return 100;
    //}
    //
    ///**
    // * makes the columns show up to 25 players instead of 20 in vanilla
    // *
    // * @author Alexdoru
    // * @reason I want to see more people on the tablist
    // */
    //@SuppressWarnings("MethodReturnAlwaysConstant")
    //@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 20))
    //private int changeColumnLength(int original) {
    //    return 25;
    //}

    /**
     * draws the scoreboard values with colors
     *
     * @author Alexdoru
     * @reason adds colors to the scoreboard values, assuming the values are health of players it will draw them with colors according to your max health
     */
    @Redirect(
            method = "drawScoreboardValues",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/util/EnumChatFormatting;YELLOW:Lnet/minecraft/util/EnumChatFormatting;",
                    opcode = GETSTATIC)
    )
    private EnumChatFormatting addColorsToScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {

        if (ConfigHandler.useColoredScores) {

            float maxhealth;
            float playerhealth = mc.thePlayer.getMaxHealth();

            if (FKCounterMod.isInMwGame() && playerhealth == 20f) {
                maxhealth = 40f;
            } else {
                maxhealth = playerhealth;
            }

            float ifloat = (float) p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();

            if (ifloat > maxhealth) {
                return EnumChatFormatting.DARK_GREEN;
            } else if (ifloat > maxhealth * 3 / 4) {
                return EnumChatFormatting.GREEN;
            } else if (ifloat > maxhealth / 2) {
                return EnumChatFormatting.YELLOW;
            } else if (ifloat > maxhealth / 4) {
                return EnumChatFormatting.RED;
            } else {
                return EnumChatFormatting.DARK_RED;
            }

        } else {
            return EnumChatFormatting.YELLOW;
        }

    }

}
