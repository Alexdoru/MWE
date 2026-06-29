<p align="center">
  <img src="https://img.shields.io/github/v/release/Alexdoru/MWE?logo=github" alt="GitHub Release">
  <img src="https://img.shields.io/github/downloads/Alexdoru/MWE/total?logo=github" alt="GitHub Downloads">
  <img src="https://github.com/Alexdoru/MWE/actions/workflows/build.yml/badge.svg" alt="Build Status">
</p>

**This is a mod for Minecraft Forge 1.8.9 that contains numerous features and improvements for pvp players and for Hypixel's Mega Walls. Use `/mwe` to open the config menu.**

## Download

Download the latest version of the mod from the **[releases page](https://github.com/Alexdoru/MWE/releases)**

## Installation

1. Be sure you have Java 8 64-bit installed
2. Download and run the installer of the latest [forge 1.8.9 version](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html)
3. Open your `.minecraft` folder, create a `mods` folder and place the mod inside
4. Open the minecraft launcher, select the forge 1.8.9 installation and start the game

Note : OrangeMarshall's Vanilla Enhancements is not compatible with this mod, the chat, tablist and hitbox related features from this mod will not work.

## Features

<details>
  <summary>Vanilla Enhancements</summary>

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
- **Change size** - Change the amount of players displayed in the tablist
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
- **Modern nametags** - Renders the nametags like in modern minecraft making them easier to see throught walls

#### Bug Fix

- **Fix actionbar text overlap** - Prevents the actionbar text from overlapping with the armor bar if the player has more than 2 rows of health

</details>

<details>
  <summary>PVP Stuff</summary>

### PVP Stuff

- **Armor HUD** - Renders your armor on the screen, render vertically or horizontally, choose to render durability, choose to only render when the durability is low
- **Potion HUD** - Renders your potion effects on the screen, render vertically or horizontally, choose the text color
- **Toggle Sprint** - Holds your sprint when you hold the forward key, there is a keybind to toggle it
- **Arrow hit HUD** - HUD that displays the health of players when shooting them with a bow
- **Speed HUD** - HUD displaying your speed
- **Mini Potion HUD** - Just a colored number with remaining duration of potion buffs
- **Sound warning low HP** - Plays a sound when your health drops below a certain threshold
- **Sword Drop Protection** - Prevents you from dropping your sword

</details>

<details>
  <summary>Hypixel</summary>

### Hypixel

- **Short coins messages** - on Hypixel, removes the booster info in the coin and token messages to make the messages smaller
- **Warp Protection** - Adds confirmation when clicking the "Play Again" paper if you have players in your squad

</details>

<details>
  <summary>Squad System</summary>

### Squad System

Add players to your squad using the `/squad` command, your squadmates will appear with a pink nametag and a distinctive icon in front of their name. You can use `/squad add <name> as <alias>` to change the name of a squadmate.

- **Squad Icons** - Toggle the icons in front of nametags for your squadmates
- **Squad HUD** - A mini tablist that will display only members of your squad
- **Colored Squadmates** - Your squadmates will render with a fixed color that you can choose for their nametag, hitbox, hurt color
- **Nick hider** - Display to yourself your own name when you are nicked in a squad
- **Keep First Letter** - Keeps the first letter of a squadmate when giving them an alias, so you can track them on the compass

</details>

<details>
  <summary>Hitbox mod, better f3+b</summary>

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

</details>

<details>
  <summary>Mega Walls features</summary>

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
- **Hide automaton title** - Hides the energy indicator subtitle when playing automaton
- **Hide repetitive msg** - Hides certain messages that spam the chat in Mega Walls
- **More strength particles** - Spawns particles around the player when a dreadlord or herobrine gets strength on kill
- **Renegade arrow count** - Displays the amount of pinned arrows on player nametags when playing Renegade
- **Command Keybinds** - Keybinds to run the following commands : `/enderchest`, `/kill`, `/surface`, `/teamchest`, `/warcry`
- **AFK kick sound** - Plays a sound when you are about to get kicked for AFK
- **Wither Outlines** - Renders a team colored outlines around withers
- **Deathmatch damage** - In case of draw, it prints the deathmatch damage directly in the chat when the game ends
- **Safe Inventory** - "Prevents hotkeying important kit items out of your inventory"
- **Squad Halo Player** - Adds to the squad the player you give your halo to when playing the Angel class
- **Wither alerts** - Plays an alert when a wither's health falls below a certain threshold
- Notification at 5 mins before a game ends, and 10 seconds before the walls fall if you are not on the game
- Shows class and kit in the chat message when you hit an arrow

</details>

<details>
  <summary>NoCheaters</summary>

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

</details>

<details>
  <summary>Hacker Detector</summary>

### Hacker Detector:

Detects players using certain cheats and warns you about these players. It can also automatically report them and save those players in NoCheaters.
Currently, has detection for : Autoblock, Fastbreak, Ghosthand, Keepsprint, Killaura, Noslowdown, Scaffold

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

</details>

<details>
  <summary>External</summary>

### External

- **Hide toggle sprint HUD** - Hides the toggle sprint HUD from OrangeMarshall's SimpleMod
- **Hide Optifine hats** - Hides cosmetic hats added by Optifine during certain seasons

</details>

<details>
  <summary>Commands</summary>

### Commands

Most commands require a working Hypixel API Key, use `/mwe set <key>` to set your key.

- `/addalias` - Give nicknames to players that will show in the tablist behind their name, use `/addalias` for details
- `/mwe` - Open the config menu
- `/nocheaters` - Shows reported players in chat, use `/nocheaters help` for details
- `/partydetection` - Lists the players that joined together
- `/plancke` - Checks stats of players for different games, use `/plancke` for details
- `/scangame` - In Mega Walls, reveals which accouns are alt accounts with high stats, most likely cheaters
- `/squad` - Add and remove players from your squad, use `/squad` for details
- `/stalk <name>` - Reveals what a player is currently doing on the Hypixel Network

</details>

## For developers

### Contributing

You can contribute to this project by making a [pull request](https://github.com/Alexdoru/MWE/pulls), please note that
features considered as black listed modifications according to the hypixel's server rules will be automatically
rejected.

### MWE Addon

The mod has a public API allowing you to create addon mods.
By making an addon mod, users can install latest version of official MWE and multiple addons of their liking.
Whereas if you modify MWE and distribute it, users are stuck with your modified MWE at whatever version it was made.
So they can't install latest version of MWE, and they can't install other modified MWE that have different features.

An example addon mod can be found [here](https://github.com/Alexdoru/ExampleMod1.8.9/tree/mwe-addon)

To make your addon you should only use the contents of
the [mwe/api](https://github.com/Alexdoru/MWE/tree/master/src/main/java/fr/alexdoru/mwe/api) package, the main class
is [MWEApi](https://github.com/Alexdoru/MWE/blob/master/src/main/java/fr/alexdoru/mwe/api/MWEApi.java).

If you need more api content to make you addon you can ask or make a pull request.