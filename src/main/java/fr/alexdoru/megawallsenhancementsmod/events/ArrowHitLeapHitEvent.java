package fr.alexdoru.megawallsenhancementsmod.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.alexdoru.megawallsenhancementsmod.gui.ArrowHitGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArrowHitLeapHitEvent {

	private static Minecraft mc = Minecraft.getMinecraft();
	private static String HPvalue;
	private static long lasthittime;
	private static long renhittime;
	private static String Color;
	private static int arrowspinned;

	public static boolean processMessage(String rawMessage) {
		
		Matcher HitMessageMatcher0 = Pattern.compile("(\\w+) is on 0 HP.").matcher(rawMessage);
		Matcher HitMessageMatcher1 = Pattern.compile("(\\w+) is on (\\d+) HP.").matcher(rawMessage);
		Matcher HitMessageMatcher2 = Pattern.compile("(\\w+) is on (\\d+).(\\d+) HP.").matcher(rawMessage);		
		Matcher HitMessageMatcher3 = Pattern.compile("(\\w+) is on (\\d+) HP, pinned by (\\d+) arrows.").matcher(rawMessage);// renegade
		Matcher HitMessageMatcher4 = Pattern.compile("(\\w+) is on (\\d+).(\\d+) HP, pinned by (\\d+) arrows.").matcher(rawMessage);// renegade
		Matcher DirectHitLeapMatcher = Pattern.compile("You took (\\d+).(\\d+) recoil damage after traveling (\\d+).(\\d+) blocks!").matcher(rawMessage);//leap
		Matcher HitLeapMatcher = Pattern.compile("You landed a direct hit against (\\w+), taking (\\d+).(\\d+) recoil damage after traveling (\\d+).(\\d+) blocks!").matcher(rawMessage);//leap

		if(HitMessageMatcher0.matches()) {

			HPvalue = "Kill";
			lasthittime = System.currentTimeMillis();
			Color = "FFAA00"; // Gold
			return true;

		} else if(HitMessageMatcher3.matches()) { // renegade hits

			HPvalue = HitMessageMatcher3.group(2);
			arrowspinned = Integer.parseInt(HitMessageMatcher3.group(3));
			renhittime = System.currentTimeMillis();
			setColor(HPvalue);
			return true;

		} else if(HitMessageMatcher4.matches()) { // renegade hits

			HPvalue = HitMessageMatcher4.group(2) + "." + HitMessageMatcher4.group(3); // HP
			arrowspinned = Integer.parseInt(HitMessageMatcher4.group(4));
			renhittime = System.currentTimeMillis();
			setColor(HPvalue);
			return true;

		} else if(HitMessageMatcher1.matches()) {

			HPvalue = HitMessageMatcher1.group(2);
			lasthittime = System.currentTimeMillis();
			setColor(HPvalue);
			return true;

		} else if(HitMessageMatcher2.matches()) {

			HPvalue = HitMessageMatcher2.group(2) + "." + HitMessageMatcher2.group(3);
			lasthittime = System.currentTimeMillis();
			setColor(HPvalue);
			return true;

		} else if(DirectHitLeapMatcher.matches()) {

			HPvalue = "-" + String.valueOf(2f*Float.parseFloat(DirectHitLeapMatcher.group(1) +"."+ DirectHitLeapMatcher.group(2)));
			lasthittime = System.currentTimeMillis() + 1000;
			Color = "55FF55"; // Green
			return true;

		} else if(HitLeapMatcher.matches()) {

			HPvalue = "-" + String.valueOf(2f*Float.parseFloat(HitLeapMatcher.group(2) +"."+ HitLeapMatcher.group(3)));
			lasthittime = System.currentTimeMillis() + 1000;
			Color = "55FF55"; // Green
			return true;

		}
		
		return false;

	}

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {

		if(event.type != ElementType.EXPERIENCE) { 
			return;
		} 

		long time = System.currentTimeMillis();

		if(time - lasthittime < 1000) {

			new ArrowHitGui(mc,HPvalue,Color);
			return;

		} else if(time - renhittime < 1000) {

			new ArrowHitGui(mc,HPvalue,Color,arrowspinned);
			return;

		}

	}

	/**
	 * Sets the HP color depending of the HP input
	 * @param hpvalue
	 */
	private static void setColor(String hpvalue) {

		float maxhealth = mc.thePlayer.getMaxHealth();			
		float floathpvalue = Float.parseFloat(hpvalue);

		if(floathpvalue > maxhealth) {
			Color = "00AA00"; // Dark_Green
		} else if (floathpvalue > maxhealth*3/4) {
			Color = "55FF55"; // Green
		} else if (floathpvalue > maxhealth/2) {
			Color = "FFFF55"; // Yellow
		} else if (floathpvalue > maxhealth/4) {
			Color = "FF5555"; // Red
		} else {
			Color = "AA0000"; // Dark_Red
		}

	}

}
