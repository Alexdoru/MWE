package fr.alexdoru.fkcountermod.gui.elements;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

public class ButtonTitle extends GuiButton {
	
	public ButtonTitle(int x, int y, int width, int height, String text) {
		super(0, x, y, width, height, text);
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GL11.glScalef(0.5f, 0.5f, 0.5F);
		this.drawCenteredString(mc.fontRendererObj, displayString, (xPosition+width/2)*2, yPosition*2, 0xFFFFFF);
		GL11.glScalef(2f, 2f, 2f);
	}
	
	public void playPressSound(SoundHandler soundHandlerIn) { }

}
