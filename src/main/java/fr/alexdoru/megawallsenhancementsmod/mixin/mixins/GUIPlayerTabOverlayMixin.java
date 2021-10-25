package fr.alexdoru.megawallsenhancementsmod.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import fr.alexdoru.fkcountermod.FKCounterMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

@Mixin(GuiPlayerTabOverlay.class)
public class GUIPlayerTabOverlayMixin extends Gui {

	@Shadow
	private Minecraft mc;

	@Shadow
	private GuiIngame guiIngame;

	@Shadow
	private long lastTimeOpened;
	
	
	/**
	 * makes the tablist show up to 100 players instead of 80 in vanilla
	 * @author Alexdoru
	 * @reason I want to see more people on the tablist
	 */
	@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 80))
	private int changeMaxPlayers(int original) {
	    return 100;
	}
	
	/**
	 * makes the columns show up to 25 players instead of 20 in vanilla
	 * @author Alexdoru
	 * @reason I want to see more people on the tablist
	 */
	@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 20))
	private int changeColumnLength(int original) {
	    return 25;
	}
	
	/**
	 * draws the scoreboard values with colors
	 * @author Alexdoru
	 * @reason adds colors to the scoreboard values, assuming the values are health of players it will draw them with colors according to your max health
	 */
	@Overwrite
	private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_)
	{		
		int i = p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();

		if (p_175247_1_.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS)
		{
			this.mc.getTextureManager().bindTexture(net.minecraft.client.gui.Gui.icons);

			if (this.lastTimeOpened == p_175247_6_.func_178855_p())
			{
				if (i < p_175247_6_.func_178835_l())
				{
					p_175247_6_.func_178846_a(Minecraft.getSystemTime());
					p_175247_6_.func_178844_b((long)(this.guiIngame.getUpdateCounter() + 20));
				}
				else if (i > p_175247_6_.func_178835_l())
				{
					p_175247_6_.func_178846_a(Minecraft.getSystemTime());
					p_175247_6_.func_178844_b((long)(this.guiIngame.getUpdateCounter() + 10));
				}
			}

			if (Minecraft.getSystemTime() - p_175247_6_.func_178847_n() > 1000L || this.lastTimeOpened != p_175247_6_.func_178855_p())
			{
				p_175247_6_.func_178836_b(i);
				p_175247_6_.func_178857_c(i);
				p_175247_6_.func_178846_a(Minecraft.getSystemTime());
			}

			p_175247_6_.func_178843_c(this.lastTimeOpened);
			p_175247_6_.func_178836_b(i);
			int j = MathHelper.ceiling_float_int((float)Math.max(i, p_175247_6_.func_178860_m()) / 2.0F);
			int k = Math.max(MathHelper.ceiling_float_int((float)(i / 2)), Math.max(MathHelper.ceiling_float_int((float)(p_175247_6_.func_178860_m() / 2)), 10));
			boolean flag = p_175247_6_.func_178858_o() > (long)this.guiIngame.getUpdateCounter() && (p_175247_6_.func_178858_o() - (long)this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;

			if (j > 0)
			{
				float f = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)k, 9.0F);

				if (f > 3.0F)
				{
					for (int l = j; l < k; ++l)
					{
						this.drawTexturedModalRect((float)p_175247_4_ + (float)l * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);
					}

					for (int j1 = 0; j1 < j; ++j1)
					{
						this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, flag ? 25 : 16, 0, 9, 9);

						if (flag)
						{
							if (j1 * 2 + 1 < p_175247_6_.func_178860_m())
							{
								this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, 70, 0, 9, 9);
							}

							if (j1 * 2 + 1 == p_175247_6_.func_178860_m())
							{
								this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, 79, 0, 9, 9);
							}
						}

						if (j1 * 2 + 1 < i)
						{
							this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, j1 >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (j1 * 2 + 1 == i)
						{
							this.drawTexturedModalRect((float)p_175247_4_ + (float)j1 * f, (float)p_175247_2_, j1 >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				}
				else
				{
					float f1 = MathHelper.clamp_float((float)i / 20.0F, 0.0F, 1.0F);
					int i1 = (int)((1.0F - f1) * 255.0F) << 16 | (int)(f1 * 255.0F) << 8;
					String s = "" + (float)i / 2.0F;

					if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s + "hp") >= p_175247_4_)
					{
						s = s + "hp";
					}

					this.mc.fontRendererObj.drawStringWithShadow(s, (float)((p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRendererObj.getStringWidth(s) / 2), (float)p_175247_2_, i1);
				}
			}
		}
		else
		{
			/****************** MODIFIED WITH MIXINS - START ******************/

			float maxhealth;
			float playerhealth = mc.thePlayer.getMaxHealth();

			if(FKCounterMod.isInMwGame() && playerhealth == 20.0f) {        		
				maxhealth = 40.0f;
			} else {
				maxhealth = playerhealth;
			}

			float ifloat = (float)i;
			String s1 = "";

			if(ifloat > maxhealth) {
				s1 += EnumChatFormatting.DARK_GREEN;		
			} else if (ifloat > maxhealth*3/4) {
				s1 += EnumChatFormatting.GREEN;				
			} else if (ifloat > maxhealth/2) {
				s1 += EnumChatFormatting.YELLOW;				
			} else if (ifloat > maxhealth/4) {
				s1 += EnumChatFormatting.RED;				
			} else {
				s1 += EnumChatFormatting.DARK_RED;
			}

			s1 += "" + i;

			/****************** MODIFIED WITH MIXINS - END ********************/
			this.mc.fontRendererObj.drawStringWithShadow(s1, (float)(p_175247_5_ - this.mc.fontRendererObj.getStringWidth(s1)), (float)p_175247_2_, 16777215);           
		}
	}

}
