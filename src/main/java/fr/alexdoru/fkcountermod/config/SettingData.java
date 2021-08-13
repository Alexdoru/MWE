package fr.alexdoru.fkcountermod.config;

import fr.alexdoru.fkcountermod.hudproperty.ScreenPosition;

public class SettingData {
	
	private ScreenPosition screenPos;
	
	public SettingData(double x, double y) {
		screenPos = ScreenPosition.fromRelativePosition(x, y);
	}
	
	public SettingData(int x, int y) {
		screenPos = ScreenPosition.fromAbsolutePosition(x, y);
	}
	
	public void setScreenPos(int x, int y) {
		screenPos = ScreenPosition.fromAbsolutePosition(x, y);
	}
	
	public void setScreenPos(double x, double y) {
		screenPos = ScreenPosition.fromRelativePosition(x, y);
	}
	
	public ScreenPosition getScreenPos() {
		return screenPos;
	}

}
