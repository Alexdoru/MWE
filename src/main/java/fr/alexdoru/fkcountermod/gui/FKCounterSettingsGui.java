package fr.alexdoru.fkcountermod.gui;

import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.config.ConfigSetting;
import fr.alexdoru.fkcountermod.gui.elements.ButtonFancy;
import fr.alexdoru.fkcountermod.gui.elements.ButtonTitle;
import fr.alexdoru.fkcountermod.gui.elements.ButtonToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FKCounterSettingsGui extends GuiScreen {
	
	private static final ResourceLocation BACKGROUND = new ResourceLocation("fkcounter", "background.png");
	private static final ResourceLocation SHADER = new ResourceLocation("fkcounter", "shaders/blur.json");
		
	private int columns = 3;
	private int rows;
	
	@Override
	public void initGui() {
		List<ConfigSetting> settings = Lists.newArrayList(ConfigSetting.values());
		
		rows = (int) Math.ceil(settings.size()/3.0);
		
		buttonList.add(new ButtonFancy(100, width/2 + 45, height/2 - findMenuHeight()/2 + 8, 30, 14, "Move HUD", 0.5));
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				int index = i*columns + j;
				if(index < settings.size()) {
					addSetting(settings.get(index), i, j);
				}
			}
		}
		
		Method loadShaderMethod = null;
		try {
            loadShaderMethod = EntityRenderer.class.getDeclaredMethod("loadShader", ResourceLocation.class);
        } catch (NoSuchMethodException e) {
            try {
                loadShaderMethod = EntityRenderer.class.getDeclaredMethod("func_175069_a", ResourceLocation.class);
            } catch (NoSuchMethodException e1) { }
        }
		
		if(loadShaderMethod != null) {
			loadShaderMethod.setAccessible(true);
			try {
				loadShaderMethod.invoke(mc.entityRenderer, SHADER);
			} catch (Exception e) { }
		}
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int rectWidth = findMenuWidth();
		int rectHeight = findMenuHeight();
		
		GlStateManager.enableBlend();
		//drawRect(width/2 - rectWidth/2, height/2 - rectHeight/2, width/2 + rectWidth/2, height/2 + rectHeight/2, new Color(0, 0, 0, 127).getRGB());
		GlStateManager.color(1,1,1,0.7F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawModalRectWithCustomSizedTexture(width/2 - rectWidth/2, height/2 - rectHeight/2, 0, 0, rectWidth, rectHeight, rectWidth, rectHeight);
		
		//drawBorders();
		
		drawCenteredString(fontRendererObj, "FKCounter v" + FKCounterMod.VERSION, width/2, height/2 - rectHeight/2 + 10, 0xFFFFFF);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button instanceof ButtonToggle) {
			((ButtonToggle) button).getSetting().toggleValue();
		}
		if(button instanceof ButtonFancy) {
			if(button.id == 100) {
				Minecraft.getMinecraft().displayGuiScreen(new LocationEditGui(FKCounterMod.getHudManager(), this));
			}
		}
	}
	
	@Override
	public void onGuiClosed() {
		FKCounterMod.getConfigHandler().saveConfig();
		mc.entityRenderer.stopUseShader();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void addSetting(ConfigSetting setting, int row, int column) {
		String title = setting.getTitle();
		int midX = width/2;
		int x = 0;
		if(column == 0) {
			x = midX - 25 - 10 - 50;
		} else if(column == 1) {
			x = midX - 25;
		} else if(column == 2) {
			x = midX + 25 + 10;
		}
		
		int y = height/2 - findMenuHeight()/2 + 30 + row*50;
		
		buttonList.add(new ButtonTitle(x, y + 5, 50, 10, title));
		buttonList.add(new ButtonToggle(x + 13, y + 20, setting));
	}
	
	private int findMenuWidth() {
		int w = 0;
		w = 50*columns + 10*(columns - 1);
		return w;
	}
	
	private int findMenuHeight() {
		int h = 0;
		h = 30 + 50*rows;
		return h;
	}

}
