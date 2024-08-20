New Config Menu! however it reset all your configs sorry D: your previous configs are still in the old file : `mwenhancements.cfg`

- PVP Mods :
   - Armor HUD : renders your armor on the screen, render vertically or horizontally, choose to render durability, choose to only render when the durability is low
   - Potion HUD : renders your potion effects on the screen, render vertically or horizontally, choose the text color
   - Toggle Sprint : holds your sprint when you hold the forward key, there is a keybind to toggle it
- Vanilla
    - Chat copy : Left/Right click to copy a chat message, hold shift to copy one line
    - Chat search : add search bar to search chat messages, press Ctrl + F to enter chat search, Ctrl + Shift + F to use Regex search, the search icon can be hidden when it's not used, and it can be moved for compatibility
    - Longer chat : setting to extend the chat size to 32 000 lines
    - forge command fix : fix forge trying to run commands even if you don't put a `/` in front of your message
    - case command fix : fix command handler only supporting commands in all lower case
    - add setting to choose the spacing in between columns in the tablist
    - add setting to give a team colored hurt color to withers
    - add setting to remove the formatting codes from the chat logs
- Mega Walls
    - add setting to print the deathmatch damage directly in the chat if a game ends in a draw
    - add setting to render a team colored outlines around withers
    - add color setting for Speed hud, Energy HUD, and Kill cooldown hud
    - add Warcry cooldown HUD
    - play sound when you are about to get kicked for AFK
    - add `/warcry` keybind
- Hacker Detector :
    - Ghosthand check : detects players mining blocks located behind other players
    - add setting to play a sound when there is a flag
    - add setting to show/hide the report buttons on the flag messages
    - add setting to choose a custom prefix for the flag messages, only available from the config file
    - add setting to show debug killaura flags for replay mode only
- NoCheaters :
    - `/nocheaters reportlist` now works without an API key
    - add ability to edit the list of cheats that give a red icon, only available from the config file
    - add ability to edit the list of cheats that give no icon at all, only available from the config file
- Hitbox Mod :
    - add setting to toggle wither hitbox
    - add setting to render a team colored wither hitbox if the wither has a color
- Bug fix :
    - fix colored leather armor not changing color properly on Sheep players
    - fix showing players that are still connected in the ban reveal message
    - fix chat scrolling on its own if chat is open when compacting messages
    - fix giving your Angel halo not updating the color to pink squadmate

Use `/mwe` to open the config menu. To install the mod you need to use forge and drop the .jar file in your `.minecraft\mods` folder.
See more information about installation [here](https://github.com/Alexdoru/MegaWallsEnhancements#installation).
Send me a message on Discord if you have any crash, question or issue : Alexdoru