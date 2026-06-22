## Changelog

- new : add config to hide the automaton energy subtitle
- renegade : render pinned arrows on nametag when > 10 meters
- change `/addalias` command to list alias in current lobby
- add separate configs for each hacker detector check
- add config for the warp protection feature
- add setting to show full name on potion hud
- add separate configs for updater : check for updater and automatic download
- show formatted names on assists
- make `/squad` command case-insensitive
- fix : support gold sword in energy HUD
- fix : reset warcry hud when dying
- fix : fix hacker detector being disabled in Atlas

## For developers

Added a public API allowing you to create addon mods.
By making an addon mod, users can install latest version of official MWE and multiple addons of their liking.
Whereas if you modify MWE and distribute it, users are stuck with your modified MWE at whatever version it was made.
So they can't install latest version of MWE, and they can't install other modified MWE that have different features.

To make your addon you should only use the contents of
the [mwe/api](https://github.com/Alexdoru/MWE/tree/master/src/main/java/fr/alexdoru/mwe/api) package, the main class
is [MWEApi](https://github.com/Alexdoru/MWE/blob/master/src/main/java/fr/alexdoru/mwe/api/MWEApi.java).

If you need more api content to make you addon you can ask or make a pull request.

Use `/mwe` to open the config menu. To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru