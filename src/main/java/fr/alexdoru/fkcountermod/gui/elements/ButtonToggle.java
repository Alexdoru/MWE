package fr.alexdoru.fkcountermod.gui.elements;

import fr.alexdoru.fkcountermod.config.ConfigSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class ButtonToggle extends GuiButton {
	
	private static final ResourceLocation TOGGLE_ON = new ResourceLocation("fkcounter", "toggleon.png");
	private static final ResourceLocation TOGGLE_OFF = new ResourceLocation("fkcounter", "toggleoff.png");

	ConfigSetting setting;
	
	public ButtonToggle(int x, int y, ConfigSetting setting) {
		super(0, x, y, "");
		this.setting = setting;
		this.width = 24;
		this.height = 24;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		
		if(setting.getValue()) {
			mc.getTextureManager().bindTexture(TOGGLE_ON);
		} else {
			mc.getTextureManager().bindTexture(TOGGLE_OFF);
		}
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
	}
	
	public ConfigSetting getSetting() {
		return setting;
	}
	
}
