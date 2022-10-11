package fr.alexdoru.megawallsenhancementsmod.asm;

import static fr.alexdoru.megawallsenhancementsmod.asm.ClassMapping.*;

public enum FieldMapping {

    ENTITYPLAYER$GAMEPROFILE(ENTITYPLAYER, "bH", "gameProfile", GAMEPROFILE),
    ENUMCHATFORMATTING$YELLOW(ENUMCHATFORMATTING, "o", "YELLOW", ENUMCHATFORMATTING),
    GAMESETTINGS$ADVANCEDITEMTOOLTIPS(GAMESETTINGS, "y", "advancedItemTooltips", "Z"),
    GAMESETTINGS$PAUSEONLOSTFOCUS(GAMESETTINGS, "z", "pauseOnLostFocus", "Z"),
    GUICONTAINER$THESLOT(GUICONTAINER, "u", "theSlot", SLOT),
    GUIINGAME$DISPLAYEDSUBTITLE(GUIINGAME, "y", "displayedSubTitle", "Ljava/lang/String;"),
    GUISCREENBOOK$BOOKIMAGEHEIGHT(GUISCREENBOOK, "v", "bookImageHeight", "I"),
    GUISCREENBOOK$WIDTH(GUISCREENBOOK, "l", "width", "I"),
    INVENTORYPLAYER$CURRENTITEM(INVENTORYPLAYER, "c", "currentItem", "I"),
    MINECRAFT$GAMESETTINGS(MINECRAFT, "t", "gameSettings", GAMESETTINGS),
    MINECRAFT$RENDERMANAGER(MINECRAFT, "aa", "renderManager", RENDERMANAGER),
    NETHANDLERPLAYCLIENT$PLAYERINFOMAP(NETHANDLERPLAYCLIENT, "i", "playerInfoMap", "Ljava/util/Map;"),
    NETWORKPLAYERINFO$DISPLAYNAME(NETWORKPLAYERINFO, "h", "displayName", ICHATCOMPONENT),
    NETWORKPLAYERINFO$GAMEPROFILE(NETWORKPLAYERINFO, "a", "gameProfile", GAMEPROFILE),
    POTION$NIGHTVISION(POTION, "r", "nightVision", POTION),
    RENDERGLOBAL$COUNTENTITIESRENDERED(RENDERGLOBAL, "S", "countEntitiesRendered", "I"),
    RENDERMANAGER$DEBUGBOUNDINGBOX(RENDERMANAGER, "t", "debugBoundingBox", "Z");

    public final String owner;
    public final String name;
    public final String desc;

    FieldMapping(ClassMapping owner, String obfName, String mcpName, String desc) {
        this.owner = owner.toString();
        this.name = ASMLoadingPlugin.isObf ? obfName : mcpName;
        this.desc = desc;
    }

    FieldMapping(ClassMapping owner, String obfName, String mcpName, ClassMapping desc) {
        this.owner = owner.toString();
        this.name = ASMLoadingPlugin.isObf ? obfName : mcpName;
        this.desc = "L" + desc + ";";
    }

}
