## Changelog

- new : mw class selector overlay : renders your selected skins, prestige levels (default:off) and classpoints (default:
  off)
- new : class in pre game lobby HUD : shows how many of each class is in the current lobby
- change tab complete to return alias of players as well
- fix crash related to a null MapEvent
- fix armor hud rendering in the foreground in the hud edit screen

## Api

- add mega walls Skins enum
- add ContainerSlotRenderEvent : event that is fired when a GuiContainer slot is drawn
- add ModUpdater to auto-update your addon, see how to use it in
  the [example mod addon](https://github.com/Alexdoru/ExampleMod1.8.9/commit/9cb00f3a78c885af05d1042f6e730aea626aa9bd)
- fixed crash happening if your addon's name is sorted before MWE by changing the example mod'
  s [AddonBoostrap](https://github.com/Alexdoru/ExampleMod1.8.9/blob/mwe-addon/src/main/java/com/exampleaddon/AddonBootstrap.java)
  registration methods

Use `/mwe` to open the config menu. To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru