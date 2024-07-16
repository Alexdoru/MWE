package fr.alexdoru.mwe.asm.mappings;

import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;

import static fr.alexdoru.mwe.asm.mappings.ClassMapping.*;

public enum FieldMapping {

    CLIENTCOMMANDHANDLER$INSTANCE(CLIENTCOMMANDHANDLER, "instance", CLIENTCOMMANDHANDLER),
    ENTITYARROW$ISINGROUND(ENTITYARROW, "i", "inGround", "Z"),
    ENTITYARROW$PINNEDTOPLAYER(ENTITYARROW, "pinnedToPlayer", "Z"),
    ENTITYPLAYER$ITEMINUSE(ENTITYPLAYER, "g", "itemInUse", ITEMSTACK),
    ENTITYPLAYER$MWCLASS(ENTITYPLAYER, "mwe$mwClass", MWCLASS),
    ENTITYPLAYER$PLAYERDATASAMPLES(ENTITYPLAYER, "mwe$PlayerDataSamples", PLAYERDATASAMPLES),
    ENUMCHATFORMATTING$YELLOW(ENUMCHATFORMATTING, "o", "YELLOW", ENUMCHATFORMATTING),
    GAMESETTINGS$ADVANCEDITEMTOOLTIPS(GAMESETTINGS, "y", "advancedItemTooltips", "Z"),
    GAMESETTINGS$KEYBINDDROP(GAMESETTINGS, "ah", "keyBindDrop", KEYBINDING),
    GAMESETTINGS$PAUSEONLOSTFOCUS(GAMESETTINGS, "z", "pauseOnLostFocus", "Z"),
    GUICHAT$INPUTFIELD(GUICHAT, "a", "inputField", GUITEXTFIELD),
    GUICHAT$SENTHISTORYCURSOR(GUICHAT, "h", "sentHistoryCursor", "I"),
    GUICHAT$WAITINGONAUTOCOMPLETE(GUICHAT, "r", "waitingOnAutocomplete", "Z"),
    GUICONTAINER$THESLOT(GUICONTAINER, "u", "theSlot", SLOT),
    GUIINGAME$DISPLAYEDSUBTITLE(GUIINGAME, "y", "displayedSubTitle", "Ljava/lang/String;"),
    GUINEWCHAT$CHATLINES(GUINEWCHAT, "h", "chatLines", "Ljava/util/List;"),
    GUINEWCHAT$DRAWNCHATLINES(GUINEWCHAT, "i", "drawnChatLines", "Ljava/util/List;"),
    GUIPLAYERTABOVERLAY$FOOTER(GUIPLAYERTABOVERLAY, "h", "footer", ICHATCOMPONENT),
    GUIPLAYERTABOVERLAY$HEADER(GUIPLAYERTABOVERLAY, "i", "header", ICHATCOMPONENT),
    GUISCREENBOOK$BOOKIMAGEHEIGHT(GUISCREENBOOK, "v", "bookImageHeight", "I"),
    GUISCREENBOOK$WIDTH(GUISCREENBOOK, "l", "width", "I"),
    INVENTORYPLAYER$CURRENTITEM(INVENTORYPLAYER, "c", "currentItem", "I"),
    MINECRAFT$GAMESETTINGS(MINECRAFT, "t", "gameSettings", GAMESETTINGS),
    MINECRAFT$RENDERMANAGER(MINECRAFT, "aa", "renderManager", RENDERMANAGER),
    MINECRAFT$THEWORLD(MINECRAFT, "f", "theWorld", WORLDCLIENT),
    NETHANDLERPLAYCLIENT$GAMECONTROLLER(NETHANDLERPLAYCLIENT, "f", "gameController", MINECRAFT),
    NETHANDLERPLAYCLIENT$PLAYERINFOMAP(NETHANDLERPLAYCLIENT, "i", "playerInfoMap", "Ljava/util/Map;"),
    NETWORKPLAYERINFO$1$INSTANCE(NETWORKPLAYERINFO$1, "a", "this$0", NETWORKPLAYERINFO),
    NETWORKPLAYERINFO$GAMEPROFILE(NETWORKPLAYERINFO, "a", "gameProfile", GAMEPROFILE),
    NETWORKPLAYERINFO$LOCATIONSKIN(NETWORKPLAYERINFO, "e", "locationSkin", RESOURCELOCATION),
    NETWORKPLAYERINFO$MWE$DISPLAYNAME(NETWORKPLAYERINFO, "mwe$displayName", ICHATCOMPONENT),
    NETWORKPLAYERINFO$MWE$FINALKILLS(NETWORKPLAYERINFO, "mwe$finalKills", "I"),
    POTION$NIGHTVISION(POTION, "r", "nightVision", POTION),
    RENDERERLIVINGENTITY$BRIGHTNESSBUFFER(RENDERERLIVINGENTITY, "g", "brightnessBuffer", FLOATBUFFER),
    RENDERGLOBAL$COUNTENTITIESRENDERED(RENDERGLOBAL, "S", "countEntitiesRendered", "I"),
    RENDERGLOBAL$ENTITYOUTLINEFRAMEBUFFER(RENDERGLOBAL, "A", "entityOutlineFramebuffer", FRAMEBUFFER),
    RENDERGLOBAL$ENTITYOUTLINESHADER(RENDERGLOBAL, "B", "entityOutlineShader", SHADERGROUP),
    RENDERMANAGER$DEBUGBOUNDINGBOX(RENDERMANAGER, "t", "debugBoundingBox", "Z"),
    RENDERMANAGER$LIVINGENTITY(RENDERMANAGER, "c", "livingPlayer", ENTITY),
    RENDERMANAGER$RENDEROUTLINES(RENDERMANAGER, "r", "renderOutlines", "Z"),
    S19PACKETENTITYSTATUS$ENTITYID(S19PACKETENTITYSTATUS, "a", "entityId", "I");

    public final String owner;
    public final String name;
    public final String desc;

    FieldMapping(ClassMapping owner, String mcpName, String desc) {
        this.owner = owner.toString();
        this.name = mcpName;
        this.desc = desc;
    }

    FieldMapping(ClassMapping owner, String mcpName, ClassMapping classMapping) {
        this.owner = owner.toString();
        this.name = mcpName;
        this.desc = "L" + classMapping + ";";
    }

    FieldMapping(ClassMapping owner, String obfName, String mcpName, String desc) {
        this.owner = owner.toString();
        this.name = ASMLoadingPlugin.isObf() ? obfName : mcpName;
        this.desc = desc;
    }

    FieldMapping(ClassMapping owner, String obfName, String mcpName, ClassMapping classMapping) {
        this.owner = owner.toString();
        this.name = ASMLoadingPlugin.isObf() ? obfName : mcpName;
        this.desc = "L" + classMapping + ";";
    }

}
