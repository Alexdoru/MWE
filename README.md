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
- [Feature list](https://github.com/Alexdoru/MWE#feature-list)
  - [Enhancements](https://github.com/Alexdoru/MWE#enhancements)
  - [Hitbox Mod, Better f3+b](https://github.com/Alexdoru/MWE#hitbox-mod-better-f3b)
  - [Mega Walls features](https://github.com/Alexdoru/MWE#mega-walls-features)
  - [Hacker Detector](https://github.com/Alexdoru/MWE#hacker-detector)
  - [NoCheaters](https://github.com/Alexdoru/MWE#nocheaters)
  - [Commands](https://github.com/Alexdoru/MWE#commands)

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
- **[Orange's Simple Mods-1.2 ](https://legacy.curseforge.com/minecraft/mc-mods/oranges-simplemods-collection/files)** - `/simpleconfig /simplehud` Armor Status HUD, Status effect HUD and Toggle Sprint
- **[My Sidebar Mod](https://github.com/Alexdoru/SidebarMod/releases/)** - `/sidebarmod` Enhances the sidebar

Note : OrangeMarshall's Vanilla Enhancements is not compatible with this mod, the chat, tablist and hitbox related features from this mod will not work.

## Feature list

### Enhancements

- **Arrow hit HUD** - HUD that displays the health of players when shooting them with a bow
- **Squad HUD** - A mini tablist that will display only members of your squad
- **Speed HUD** - HUD displaying your speed
- **Mini Potion HUD** - Just a colored number with remaining duration of potion buffs
- **Chat heads** - Display heads of players in front of chat messages (disabled with Feather client)
- **Cancel night vision** - Remove the blueish tint from night vision
- **Clear view** - Stop rendering particles that are too close (75cm) to the screen for a better visibility
- **Sound warning low HP** - Play a sound when your health drops below a certain threshold
- **Limit dropped items** - Dynamically modify the render distance for dropped items entities to preserve performance
- **Safe Inventory** - Prevent you from dropping your sword or from hotkeying important kit items in outside inventories in Mega Walls
- **Custom hurt color** - Change the color entities take when they get hurt
- **Color armor** - For players, their armor will be colored as well when they get hurt, as it does in 1.7
- **Team colored hit color** - For players, the hurt color will be the color of their team
- **Short coins messages** - On Hypixel, remove the booster info in the coin messages to make the message smaller
- **Ban reveal** - Reveals the name of the player getting banned when playing on hypixel
- **Colored scores above head** - Render the scores/health in colour, according to the score value/health and the player's maximum health points
- **Colored scores in tablist** - Render the scores/health in colour, according to the score value/health and the player's maximum health points
- **Hide tablist header/footer** - Hide the header and footer text of the tablist
- **Hide tablist ping** - Hide the ping values in the tablist if all the values are equal to 1
- **Display playercount in tablist** - Display the amount of players in your current lobby at the top of the tablist
- **Change tablist size** - Change the amount of players displayed in the tablist (disabled with Patcher)
- **Tabcomplete player names** - Ability to tab complete player names in the chat all the time
- **Hide toggle sprint HUD** - Hide the toggle sprint HUD from OrangeMarshall's SimpleMod
- **Fix tablist rect background** - Fix the background of the tablist not being symmetric and sometimes missing pixels on one side
- **Fix actionbar text overlap** - Prevent the actionbar text from overlapping with the armor bar if the player has more than 2 rows of health
- **Fix case commands tabcomplete** - Fix not being able to tabcomplete commands if the command contains uppercased letters
- **Hide Optifine hats** - Hide cosmetic hats added by Optifine during certain seasons
- **Command keybinds** - Keybinds to run the commands `/kill` & `/surface`
- **Clean chat logs ** - Remove formatting codes from the chat logs

### Hitbox mod, better f3+b:

Allows you to customize the look of the f3+b hitboxes.
- **Enable hitboxes for certain entity types** - players, grounded arrows, pinned arrows, flying arrows, dropped items, passive mobs, aggressive mobs, item frames, other entities
- **Team colored player hitbox** - The player hitbox will take the color of the player's team
- **Team colored arrow hitbox** - The arrow hitbox will take the color of the shooter's team
- **Custom hitbox color** - Select a custom color for all the hitboxes
- **Real size hitbox** - Render the actual hitbox of the player which is a bit larger than what default f3+b shows
- **Draw red eyeline** - Draw a red square at the eye level of entities
- **Draw blue vector** - Draw a blue line representing the look direction of entities
- **Blue line for players only** - Draws the blue line for players only
- **3m blue line** - Make the blue line 3 meters long, to match the reach of players
- **Hide close hitboxes** - Stop rendering hitboxes that are closer than a certain distance that you can choose
- **Hitboxes remain enable after restart** - Unlike vanilla minecraft you won't need to enable it everytime you play

### Mega Walls features

- **Colored Leather Armor** - Changes iron armor pieces to team colored leather armor
- **Base Location HUD** - Shows in which base you are located
- **Creeper primed TNT HUD** - Displays the cooldown of the primed TNT when playing Creeper
- **Energy display HUD** - Displays the amount of energy you have
- **Kill cooldown HUD** - Displays the cooldown on the `/kill` command
- **Phoenix bond HUD** - Displays the amount of hearts healed from a Phoenix bond
- **Strength HUD** - Displays the duration of the strength in Mega Walls when playing Dreadlord, Herobrine, Hunter, Zombie. Plays a sound and shows the countdown before getting strength with Hunter.
- **Wither death time HUD** - Displays the time left before the last wither standing automatically dies, HUD can be placed in the sidebar
- **Hide hunger title** - Stop rendering the hunger message in the middle of the screen during deathmatch
- **Hide repetitive msg** - Hide certain messages that spam the chat in Mega Walls
- **More strength particles** - Spawn particles around the player when a dreadlord or herobrine gets strength on kill
- **Renegade arrow count** - Display the amount of arrows you have pinned in them above player heads when playing Renegade
- **Nick hider** - Display to yourself your own name when you are nicked in Mega Walls
- **Pink Squadmates** - Squadmates' nametags will appear pink as well as their hitboxes
- **Command Keybinds** - Keybinds to run the following commands : `/kill`, `/surface`, `/teamchest`, `/enderchest`
- Notification at 5 mins before a game ends, and 10 seconds before the walls fall if you are not on the game
- Shows your stats for the game when a game ends
- Shows class and kit in the chat message when you hit an arrow
- Prevent clicking the "Play Again" button if you are in a party

#### Final Kill Counter:

Tracks final kills in Hypixel's Mega Walls.
- **Select HUD style** - Final kill HUD can take 3 form, classic (show finals per team), players (show finals per player), compact (show 4 colored numbers) and compact in sidebar
- **Final in tablist** - Display the amount of final kills per player in the tablist next to their name
- **Kill diff in chat** - When a player with final kills dies, it appends at the end of the chat message the amount of finals lost
- `/fks say` - Sends a chat message with the current final score for your teammates to see

### Hacker Detector:

Detects players using certain cheats and warns you about those players. It can also automatically report them and save those players in NoCheaters.
- **Autoblock check**
- **Fastbreak check**
- **Keepsprint check**
- **Killaura check** - checks attacking through blocks and players, check attacking while eating/drinking
- **Noslowdown check**
- **Scaffold check**

### NoCheaters:

Saves all the players you report with `/wdr` (not /report), and gives you warnings about those players when you come across them ingame. Use `/unwdr <name>` to remove a player.
- **Icons on names** - Add an icon in front of nametags and in the tablist for players you have reported
- **Warning message** - Print a warning message in chat when a reported player joins your lobby
- **Report suggestion** - Highlight certain chat messages sent by players that warn about other players cheating
- **Censor cheaters in chat** - Deletes or censors chat messages sent by players you have reported for cheating
- **Report HUD** - Displays a small text when the mod has a report to send, and which report it is typing
- You can use `/report name boosting` & `/report name crossteaming` as a one step command to report on Hypixel instead of using the book
- Shows who joined in party with a certain player when you `/wdr` them, to report the other members for boosting

### Commands

Most commands require a working Hypixel API Key, use `/mwe set <key>` to set your key.

- `/addalias` - Give nicknames to players that will show in the tablist behind their name, use `/addalias` for details
- `/mwe` - Open the config menu
- `/nocheaters` - Shows reported players in chat, use `/nocheaters help` for details
- `/plancke` - Checks stats of players for different games, use `/plancke` for details
- `/scangame` - In Mega Walls, reveals which accouns are alt accounts with high stats, most likely cheaters
- `/squad` - Add and remove players from your squad, use `/squad` for details
- `/stalk <name>` - Reveals what a player is currently doing on the Hypixel Network