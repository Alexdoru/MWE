Use `/mwe` to open the config menu

- Enhancements :
  - add Mini Potion HUD : just a colored number with remaining duration of potion buffs
  - add option to render player head on ArrowHitHUD
  - add separate settings to show squad/warning icons on names
  - fix hitbox colors in replay
  - fix not showing a chat head on all chat messages
- Mega Walls :
  - add Base Location HUD : shows in which base you are located
  - fix weird crash on certain clients when you hit someone in dm
- Hacker detector :
  - add Killaura check through blocks and through players
  - add Killaura check attacking while eating/drinking
  - add Scaffold check
  - improve Autoblock check, Noslowdown check, Fastbreak check
  - fix chat compacting of flag message if the playername changes (for example when they gain levels in The Pit)
- NoCheaters :
  - changed file with report data from `wdred.txt` -> `WDRList.json`, transfer should be automatic
  - fix not saving players to NoCheaters when using /wdr but don't have an API key

To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru