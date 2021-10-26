package fr.alexdoru.fkcountermod.gui;

import java.util.List;

import com.google.common.collect.Lists;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.config.ConfigSetting;
import fr.alexdoru.fkcountermod.gui.elements.ButtonFancy;
import fr.alexdoru.fkcountermod.gui.elements.ButtonTitle;
import fr.alexdoru.fkcountermod.gui.elements.ButtonToggle;
import fr.alexdoru.megawallsenhancementsmod.gui.MyGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FKCounterConfigGuiScreen extends MyGuiScreen {
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("fkcounter", "background.png");
		
	private int columns = 4;
	private int rows;
	
	private final int buttonSize = 50;
	private final int widthBetweenButtons = 10;
	private final int heightBetweenButtons = 30;
		
	@Override
	public void initGui() {
		List<ConfigSetting> settings = Lists.newArrayList(ConfigSetting.values());

		rows = (int) Math.ceil(settings.size()/((float)columns));

		buttonList.add(new ButtonFancy(100, width/2 + 45, height/2 - findMenuHeight()/2 + 8, 30, 14, "Move HUD", 0.5));
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				int index = i*columns + j;
				if(index < settings.size()) {
					addSettingButton(settings.get(index), i, j);
				}
			}
		}
		
		super.initGui();
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int rectWidth = findMenuWidth();
		int rectHeight = findMenuHeight();
		
		GlStateManager.enableBlend();
		GlStateManager.color(1,1,1,0.7F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawModalRectWithCustomSizedTexture(width/2 - rectWidth/2, height/2 - rectHeight/2, 0, 0, rectWidth, rectHeight, rectWidth, rectHeight);
		drawCenteredString(fontRendererObj, "FKCounter v" + FKCounterMod.VERSION, width/2, height/2 - rectHeight/2 + 10, 0xFFFFFF);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button instanceof ButtonToggle) {
			((ButtonToggle) button).getSetting().toggleValue();
			FKCounterGui.updateDisplayText();
			// TODO si toggle player et que compact est allumé faut allumer le normal
		}
		if(button instanceof ButtonFancy) {
			if(button.id == 100) {
				Minecraft.getMinecraft().displayGuiScreen(new LocationEditGuiScreen(FKCounterMod.getHudManager(), this));
			}
			FKCounterGui.updateDisplayText();
		}
		
	}
	
	@Override
	public void onGuiClosed() {
		FKCounterMod.getConfigHandler().saveConfig();
		super.onGuiClosed();
	}
	
	private void addSettingButton(ConfigSetting setting, int row, int column) {
		String title = setting.getTitle();
			
		int x = 0;
		
		if(columns%2==0) { // even
			x = getxCenter() + widthBetweenButtons/2 + (widthBetweenButtons + buttonSize)*(column-columns/2);
		} else { // odd
			x = getxCenter() - buttonSize/2 + (widthBetweenButtons + buttonSize)*(column-columns/2);
		}
		
		int y = getyCenter() - findMenuHeight()/2 + heightBetweenButtons + row*buttonSize;
		
		buttonList.add(new ButtonTitle(x, y + 5, 50, 10, title));
		buttonList.add(new ButtonToggle(x + 13, y + 20, setting));// TODO merge the two Button classes
	}
	
	private int findMenuWidth() {
		return buttonSize*columns + widthBetweenButtons*(columns - 1);
	}
	
	private int findMenuHeight() {
		return heightBetweenButtons + buttonSize*rows;
	}

}
