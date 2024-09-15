<div style="text-align: center;">
<a href="https://github.com/Alexdoru/MWE/releases" target="_blank">
<img alt="release" src="https://img.shields.io/github/v/release/Alexdoru/MWE?color=0843B2&style=for-the-badge" />
</a>
<a href="https://github.com/Alexdoru/MWE/releases" target="_blank">
<img alt="downloads" src="https://img.shields.io/github/downloads/Alexdoru/MWE/total?color=0843B2&style=for-the-badge" />
</a>
</div>

**This is a mod for Minecraft Forge 1.8.9 that contains numerous features and improvements for pvp players and for Hypixel's Mega Walls. Use `/mwe` to open the config menu.**

## Summary
- [Download](https://github.com/Alexdoru/MWE#download)
- [Installation](https://github.com/Alexdoru/MWE#installation)
- [Features](https://github.com/Alexdoru/MWE#features)
  - [Vanilla Enhancements](https://github.com/Alexdoru/MWE#vanilla-enhancements)
  - [PVP Stuff](https://github.com/Alexdoru/MWE#pvp-stuff)
  - [Hypixel](https://github.com/Alexdoru/MWE#hypixel)
  - [Squad System](https://github.com/Alexdoru/MWE#squad-system)
  - [Hitbox Mod, Better f3+b](https://github.com/Alexdoru/MWE#hitbox-mod-better-f3b)
  - [Mega Walls features](https://github.com/Alexdoru/MWE#mega-walls-features)
  - [NoCheaters](https://github.com/Alexdoru/MWE#nocheaters)
  - [Hacker Detector](https://github.com/Alexdoru/MWE#hacker-detector)
  - [External](https://github.com/Alexdoru/MWE#external)
  - [Commands](https://github.com/Alexdoru/MWE#commands)
- [License and Contribution](https://github.com/Alexdoru/MWE#license-and-contribution)

## Download

Download the latest release from the **[releases page](https://github.com/Alexdoru/MWE/releases)**

## Installation

1. Be sure you have Java 8 64-bit installed
2. Download and run the installer of the latest [forge 1.8.9 version](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html)
3. Open your `.minecraft` folder, create a `mods` folder and place the mod inside
4. Open the minecraft launcher, select the forge 1.8.9 installation and start the game

#### Other mods I recommend using :
- **[Optifine](https://optifine.net/downloads)** - Enhances performance
- **[Sk1er's Patcher](https://sk1er.club/mods/patcher)** - `/patcher` Minecraft QOLs, performance improvements and vanilla bug fixes
- **[Sk1er's Old Animation](https://discord.gg/sk1er)** - `/oldanimations` Brings back 1.7 animations, available in beta on their discord server
- **[My Sidebar Mod](https://github.com/Alexdoru/SidebarMod/releases/)** - `/sidebarmod` Enhances the sidebar

Note : OrangeMarshall's Vanilla Enhancements is not compatible with this mod, the chat, tablist and hitbox related features from this mod will not work.

## Features

### Vanilla Enhancements

#### Chat

- **Chat heads** - Displays heads of players in front of chat messages
- **Chat Copy** - Left/Right click to copy a chat message, hold shift to copy one line
- **Chat Search** - Adds a search bar in the chat to search chat messages, press Ctrl + F to enter chat search, Ctrl + Shift + F to use Regex search, the search icon can be hidden when it's not used, and it can be moved for compatibility
- **Longer chat** - Extends the chat size to 32 000 lines
- **Case command fix** - Fix command handler only supporting commands written in all lower case
- **Forge command fix** - Fix forge trying to run commands even if you don't put a `/` in front of your message
- **Tabcomplete player names** - Ability to tab complete player names in the chat all the time
- **Clean chat logs** - Removes formatting codes from the chat logs

#### Hurt Color

- **Custom color** - Change the color entities take when they get hurt
- **Team colored color** - For players, the hurt color will be the color of their team
- **Color armor** - For players, their armor will be colored as well when they get hurt, as it does in 1.7
- **Wither Hurt Color** - Gives a team colored hurt color to withers if they have a team

#### Tablist

- **Colored scores** - Render the scores/health in color, according to the score value/health and the player's maximum health points
- **Change size** - Change the amount of players displayed in the tablist (disabled with Patcher)
- **Display playercount** - Display the amount of players in your current lobby at the top of the tablist
- **Hide header/footer** - Hide the header and footer text of the tablist
- **Hide ping** - Hide the ping values in the tablist if all the values are equal to 1
- **Choose column spacing** - Choose the spacing between columns in the tablist
- **Fix background** - Fix the background of the tablist not being symmetric and sometimes missing pixels on one side

#### Performance

- **Limit dropped items** - Dynamically modifies the render distance for dropped items entities to preserve performance

#### Render

- **Cancel night vision** - Removes the blueish tint from night vision
- **Clear view** - Stops rendering particles that are too close (75cm) to the screen for a better visibility
- **Colored scores above head** - Renders the scores/health above player heads in color, according to the score value/health and the player's maximum health points

#### Bug Fix

- **Fix actionbar text overlap** - Prevents the actionbar text from overlapping with the armor bar if the player has more than 2 rows of health

### PVP Stuff

- **Armor HUD** - Renders your armor on the screen, render vertically or horizontally, choose to render durability, choose to only render when the durability is low
- **Potion HUD** - Renders your potion effects on the screen, render vertically or horizontally, choose the text color
- **Toggle Sprint** - Holds your sprint when you hold the forward key, there is a keybind to toggle it
- **Arrow hit HUD** - HUD that displays the health of players when shooting them with a bow
- **Speed HUD** - HUD displaying your speed
- **Mini Potion HUD** - Just a colored number with remaining duration of potion buffs
- **Sound warning low HP** - Plays a sound when your health drops below a certain threshold
- **Sword Drop Protection** - Prevents you from dropping your sword

### Hypixel

- **Short coins messages** - on Hypixel, removes the booster info in the coin and token messages to make the messages smaller
- **Warp Protection** - Adds confirmation when clicking the "Play Again" paper if you have players in your squad

### Squad System

Add players to your squad using the `/squad` command, your squadmates will appear with a pink nametag and a distinctive icon in front of their name. You can use `/squad add <name> as <alias>` to change the name of a squadmate.

- **Squad Icons** - Toggle the icons in front of nametags for your squadmates
- **Squad HUD** - A mini tablist that will display only members of your squad
- **Pink Squadmates** - Nametags of squadmates will appear pink as well as their hitboxes and hurt color
- **Nick hider** - Display to yourself your own name when you are nicked in a squad
- **Keep First Letter** - Keeps the first letter of a squadmate when giving them an alias, so you can track them on the compass

### Hitbox mod, better f3+b:

Allows you to customize the look of the f3+b hitboxes.
- **Enable hitboxes for certain entity types** - players, grounded arrows, pinned arrows, flying arrows, dropped items, passive mobs, aggressive mobs, item frames, withers, other entities
- **Team colored player hitbox** - The player hitbox will take the color of the player's team
- **Team colored wither hitbox** - The wither hitbox will take the color of the wither's team
- **Team colored arrow hitbox** - The arrow hitbox will take the color of the shooter's team
- **Custom hitbox color** - Select a custom color for all the hitboxes
- **Real size hitbox** - Render the actual hitbox of the player which is a bit larger than what default f3+b shows
- **Draw red eyeline** - Draw a red square at the eye level of entities
- **Draw blue vector** - Draw a blue line representing the look direction of entities
- **Blue line for players only** - Draws the blue line for players only
- **3m blue line** - Make the blue line 3m long, to match the reach of players
- **Hide close hitboxes** - Stops rendering hitboxes that are closer than a certain distance that you can choose
- **Hitboxes remain enable after restart** - Unlike vanilla minecraft you won't need to enable it everytime you play

### Mega Walls features

- **Base Location HUD** - Shows in which base you are located
- **Creeper primed TNT HUD** - Displays the cooldown of the primed TNT when playing Creeper
- **Energy display HUD** - Displays the amount of energy you have
- **Kill cooldown HUD** - Displays the cooldown on the `/kill` command
- **Phoenix bond HUD** - Displays the amount of hearts healed from a Phoenix bond
- **Strength HUD** - Displays the duration of the strength in Mega Walls when playing Dreadlord, Herobrine, Hunter, Zombie. Plays a sound and shows the countdown before getting strength with Hunter.
- **Warcry HUD** - Displays the warcry cooldown
- **Wither death time HUD** - Displays the time left before the last wither standing automatically dies, HUD can be placed in the sidebar
- **Colored Leather Armor** - Changes iron armor pieces to team colored leather armor
- **Hide hunger title** - Stop rendering the hunger message in the middle of the screen during deathmatch
- **Hide repetitive msg** - Hides certain messages that spam the chat in Mega Walls
- **More strength particles** - Spawns particles around the player when a dreadlord or herobrine gets strength on kill
- **Renegade arrow count** - Displays the amount of arrows you have pinned in them above player heads when playing Renegade
- **Command Keybinds** - Keybinds to run the following commands : `/enderchest`, `/kill`, `/surface`, `/teamchest`, `/warcry`
- **AFK kick sound** - Plays a sound when you are about to get kicked for AFK
- **Wither Outlines** - Renders a team colored outlines around withers
- **Deathmatch damage** - In case of draw, it prints the deathmatch damage directly in the chat when the game ends
- **Safe Inventory** - "Prevents hotkeying important kit items out of your inventory"
- **Squad Halo Player** - Adds to the squad the player you give your halo to when playing the Angel class
- Notification at 5 mins before a game ends, and 10 seconds before the walls fall if you are not on the game
- Shows class and kit in the chat message when you hit an arrow

### NoCheaters:

NoCheaters saves players reported via `/wdr name` (not /report) and warns you about them ingame.
To remove a player from your report list use : `/unwdr name` or click the name on the warning message.
You can see all the players you have reported using `/nocheaters reportlist`.
Players reported for blatant cheats will have a red icon, others will have a yellow icon.
You can define in the config which cheats give a red icon or no icon at all.

- **Icons on names** - Adds an icon in front of nametags and in the tablist for players you have reported
- **Warning message** - Prints a warning message in chat when a reported player joins your lobby
- **Ban reveal** - Reveals the name of the player getting banned when playing on hypixel
- **Delete old reports** - Choose if you want to delete old reports and if so after how many days
- **Report suggestion** - Highlights certain chat messages sent by players that warn about other players cheating
- **Censor cheaters in chat** - Deletes or censors chat messages sent by players you have reported
- You can use `/report name boosting` & `/report name crossteaming` as a one step command to report on Hypixel instead of using the book
- Shows who joined in party with a certain player when you `/wdr` them, to report the other members for boosting

### Hacker Detector:

Detects players using certain cheats and warns you about these players. It can also automatically report them and save those players in NoCheaters.
Currently has detection for : Autoblock, Fastbreak, Ghosthand, Keepsprint, Killaura, Noslowdown, Scaffold

- **Add to report list** - Adds detected players to NoCheater's reportlist
- **Show flag message** - Shows a flag message in the chat when it detets someone
- **Compact flag messages** - Deletes previous flag message when printing a new identical flag message
- **Show single flag message** - Prints flag messages only once per game per player
- **Sound when flagging** - Plays a sound when it flags a player
- **Show report button** - Shows report buttons on flag messages
- **Flag message prefix** - Lets you chose the prefix of flags messages
- **Auto-report cheaters** - Sends a /report automatically to Hypixel when it flags a cheater, only works for Mega Walls
- **Replay killaura flags** - In the replay only, it will print a message when someone attacks another player through blocks
- **Report HUD** - Displays a small text when the mod has a report to send, and which report it is typing
Fix Autoblock animation bypass : certain cheating clients send certain packets that make you not see them blocking their sword when they are in reality constantly blocking their sword while attacking.

### External

- **Hide toggle sprint HUD** - Hides the toggle sprint HUD from OrangeMarshall's SimpleMod
- **Hide Optifine hats** - Hides cosmetic hats added by Optifine during certain seasons

### Commands

Most commands require a working Hypixel API Key, use `/mwe set <key>` to set your key.

- `/addalias` - Give nicknames to players that will show in the tablist behind their name, use `/addalias` for details
- `/mwe` - Open the config menu
- `/nocheaters` - Shows reported players in chat, use `/nocheaters help` for details
- `/plancke` - Checks stats of players for different games, use `/plancke` for details
- `/scangame` - In Mega Walls, reveals which accouns are alt accounts with high stats, most likely cheaters
- `/squad` - Add and remove players from your squad, use `/squad` for details
- `/stalk <name>` - Reveals what a player is currently doing on the Hypixel Network

## License and Contribution

This project doesn't have a license which means it defaults to exclusive copyright.

You can fork this project, read its code, but you are not allowed to ditribute it. Read the license.

If you want to contribute to this project you can make a pull request.

If you want to add features that have nothing to do with this project, consider creating your own mod instead of forking this project "because it's a nice mod".