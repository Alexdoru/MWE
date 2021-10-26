package fr.alexdoru.megawallsenhancementsmod.gui;

import java.io.IOException;

import fr.alexdoru.fkcountermod.gui.FKCounterConfigGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GeneralConfigGuiScreen extends MyGuiScreen { // TODO config gui

	private int ButtonsWidth = 150;
	private int ButtonsHeight = 20;
	private String title = "Config";

	@Override
	public void initGui() {

		/*
		 * Define the button list
		 */
		this.buttonList.add(new GuiButton(0, getxCenter()-ButtonsWidth/2, getyCenter()-ButtonsHeight/2-(ButtonsHeight+4), ButtonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "Final Kill Counter"));
		this.buttonList.add(new GuiButton(1, getxCenter()-ButtonsWidth/2, getyCenter()-ButtonsHeight/2, ButtonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "Mega Walls Enhancements"));
		this.buttonList.add(new GuiButton(2, getxCenter()-ButtonsWidth/2, getyCenter()-ButtonsHeight/2+(ButtonsHeight+4), ButtonsWidth, ButtonsHeight, EnumChatFormatting.GREEN + "No Cheaters"));
		this.buttonList.add(new GuiButton(3, getxCenter()-ButtonsWidth/2, getyCenter()-ButtonsHeight/2+(ButtonsHeight+4)*3, ButtonsWidth, ButtonsHeight, "Close"));

		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

		switch(button.id) {
		case 0:
			mc.displayGuiScreen(new FKCounterConfigGuiScreen());
			break;
		case 1:
			mc.displayGuiScreen(new MWEnConfigGuiScreen());
			break;
		case 2:
			mc.displayGuiScreen(new NoCheatersConfigGuiScreen());
			break;
		case 3:
			mc.displayGuiScreen(null);
			break;
		}
		super.actionPerformed(button);
		
	}
		
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
        {
        	int dilatation = 2;
            GlStateManager.translate((width / 2) - mc.fontRendererObj.getStringWidth(title), getyCenter() - (ButtonsHeight+4)*3, 0);
            GlStateManager.scale(dilatation, dilatation, dilatation);
            mc.fontRendererObj.drawString(title, 0, 0, Integer.parseInt("55FF55", 16));
        }
        GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

}
