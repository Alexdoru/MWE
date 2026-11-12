## Changelog

- new : Base location labels - in world labels indicating which base is where when holding the compass during Mega Walls games
- new : add config to show the class count HUD at the bottom of the tablist
- new : add config to sort classes per team in the class count HUD
- add tab completion for dead players during Mega Walls games
- fix : hitbox of pinned arrows now take the color of the player instead of being white
- delete capture the flag mini-game

## Api

- api : add `ColorUtil`, `RenderUtil`, `StringUtil`, `TabCompletionUtil` and `MapUtil`
- api : add `IClassInfo`, `IMethodInfo` and `IFieldInfo` to handle ASM mappings, and move the ASM helper methods to `IClassNodeTransformer`

Use `/mwe` to open the config menu. To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru