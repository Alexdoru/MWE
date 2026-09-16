## Changelog

- squad hud : add config to automatically display N teamates following certain critera
- squad hud : add config to change background alpha
- add Name Format config category : lets you chose how the names are formatted in the tablist and on the squad HUD
- add configs to change the look of the icons (edit in the config file while the game is closed)
- add option to display the Mini potion HUD vertically by @PotatoOfPotato
- fix ReplayBookmarksOverlay following hypixel update

## Api

- add `ISquadInfoRenderer` interface allowing you to draw extra info on the squad hud
- add `ITabNameModifier` interface allowing you to customize the names in the tablist
- add `MWEApi.Names` : methods for querying formatted names
- add `ChatMessageSentEvent`, `WitherHealthDecayEvent`, `MegaWallsGameTimeEvent`
- add `FIRST_WITHER_DEATH` to `MegaWallsGameEvent`
- fix : fire MapEvent all the time

Use `/mwe` to open the config menu. To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru