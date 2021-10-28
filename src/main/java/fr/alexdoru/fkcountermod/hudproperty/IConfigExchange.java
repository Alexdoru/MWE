package fr.alexdoru.fkcountermod.hudproperty;

public interface IConfigExchange {

	/**
	 * Is called for each HUD when the screen is closed.
	 *
	 * @param  pos  Provided by the API. The chosen position for the HUD.
	 * Preferably save the values in a configuration file.
	 */
	void save(ScreenPosition pos);

	/**
	 * Creates a new ScreenPosition object based on relative coordinates. 
	 * From 0 to 1. Example: 0.3 being 30% of the screen size.
	 *
	 * @return  The initial ScreenPosition position. 
	 * This is where the HUD will be rendered when opening a screen.
	 * Preferably load the values from a configuration file. 
	 * Can be null.
	 */
	ScreenPosition load();
	
}
