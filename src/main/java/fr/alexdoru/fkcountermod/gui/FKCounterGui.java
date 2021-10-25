package fr.alexdoru.fkcountermod.gui;

import java.awt.Color;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.config.ConfigSetting;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.hudproperty.IRenderer;
import fr.alexdoru.fkcountermod.hudproperty.ScreenPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

public class FKCounterGui extends Gui implements IRenderer {

	/**
	 * used as an example when in the settings 
	 */
	private static final String DUMMY_TEXT = EnumChatFormatting.RED + "RED" + EnumChatFormatting.WHITE + ": 1\n"
			+ EnumChatFormatting.GREEN + "GREEN" + EnumChatFormatting.WHITE + ": 2\n"
			+ EnumChatFormatting.YELLOW + "YELLOW" + EnumChatFormatting.WHITE + ": 3\n"
			+ EnumChatFormatting.BLUE + "BLUE" + EnumChatFormatting.WHITE + ": 4";
	/**
	 * used as an example when in the settings 
	 */
	private static final String DUMMY_TEXT_COMPACT = EnumChatFormatting.RED + "1" + EnumChatFormatting.GRAY + " / "
			+ EnumChatFormatting.GREEN + "2" + EnumChatFormatting.GRAY + " / "
			+ EnumChatFormatting.YELLOW + "3" + EnumChatFormatting.GRAY + " / "
			+ EnumChatFormatting.BLUE + "4";

	private boolean dummy = false;
	private static String displayText = "";

	@Override
	public void save(ScreenPosition pos) {
		int x = pos.getAbsoluteX();
		int y = pos.getAbsoluteY();

		ConfigSetting.FKCOUNTER_HUD.getData().setScreenPos(x, y);
		FKCounterMod.getConfigHandler().saveConfig();
	}

	@Override
	public ScreenPosition load() {
		return ConfigSetting.FKCOUNTER_HUD.getData().getScreenPos();
	}

	@Override
	public int getHeight() {
		if(ConfigSetting.COMPACT_HUD.getValue()) {
			return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
		} else {
			return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT*4;
		}
	}

	@Override
	public int getWidth() {

		if(dummy) {
			if(ConfigSetting.COMPACT_HUD.getValue()) {
				return Minecraft.getMinecraft().fontRendererObj.getStringWidth(DUMMY_TEXT_COMPACT);
			}
			else {
				return Minecraft.getMinecraft().fontRendererObj.getStringWidth("YELLOW: 3");
			}
		}

		int maxwidth = 0;
		for(String line : getDisplayText().split("\n")) {

			int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(line);
			if(width > maxwidth) {
				maxwidth = width;				
			}

		}
		return maxwidth;
	}

	@Override
	public void render(ScreenPosition position) { // TODO ca se décale pendant les games

		dummy = false;

		int x = position.getAbsoluteX();
		int y = position.getAbsoluteY();

		if(ConfigSetting.DRAW_BACKGROUND.getValue()) {
			drawRect(x - 1, y - 1, x + getWidth(), y + getHeight(), new Color(0, 0, 0, 64).getRGB());
		}

		drawMultilineString(getDisplayText(), x, y);

	}

	@Override
	public void renderDummy(ScreenPosition position) {
		dummy = true;

		int x = position.getAbsoluteX();
		int y = position.getAbsoluteY();

		int width = getWidth();
		int height = getHeight();

		drawRect(x - 1, y - 1, x + width + 1, y + height + 1, new Color(255, 255, 255, 127).getRGB());
		drawHorizontalLine(x - 1, x + width + 1, y - 1, Color.RED.getRGB());
		drawHorizontalLine(x - 1, x + width + 1, y + height + 1, Color.RED.getRGB());
		drawVerticalLine(x - 1, y - 1, y + height + 1, Color.RED.getRGB());
		drawVerticalLine(x + width + 1, y - 1, y + height + 1, Color.RED.getRGB());

		if(ConfigSetting.COMPACT_HUD.getValue()) {
			drawMultilineString(DUMMY_TEXT_COMPACT, x, y);
		}
		else {
			drawMultilineString(DUMMY_TEXT, x, y);
		}
	}

	@Override
	public boolean isEnabled() {
		return (ConfigSetting.FKCOUNTER_HUD.getValue() && FKCounterMod.isInMwGame() && FKCounterMod.getKillCounter().getGameId() != null);
	}

	private void drawMultilineString(String msg, int x, int y) {

		for(String line : msg.split("\n")) {
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(line, x, y, 0); // TODO ajouter une config pour choisir avec ou sans shadow
			y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
		}
	}

	private String getDisplayText() {
		return displayText;
	}

	public static void updateDisplayText() {

		if(KillCounter.getGameId()!= null) {

			if(ConfigSetting.COMPACT_HUD.getValue()) {

				displayText = KillCounter.getRedPrefix() + KillCounter.getKills(KillCounter.RED_TEAM) + EnumChatFormatting.GRAY + " / "
						+ KillCounter.getGreenPrefix() + KillCounter.getKills(KillCounter.GREEN_TEAM) + EnumChatFormatting.GRAY + " / "
						+ KillCounter.getYellowPrefix() + KillCounter.getKills(KillCounter.YELLOW_TEAM) + EnumChatFormatting.GRAY + " / "
						+ KillCounter.getBluePrefix() + KillCounter.getKills(KillCounter.BLUE_TEAM);

			} else {

				displayText = KillCounter.getRedPrefix() + "RED" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.RED_TEAM) + "\n"
						+ KillCounter.getGreenPrefix() + "GREEN" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.GREEN_TEAM) + "\n"
						+ KillCounter.getYellowPrefix() + "YELLOW" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.YELLOW_TEAM) + "\n"
						+ KillCounter.getBluePrefix() + "BLUE" + EnumChatFormatting.WHITE + ": " + KillCounter.getKills(KillCounter.BLUE_TEAM);

			}

		}

	}

}
