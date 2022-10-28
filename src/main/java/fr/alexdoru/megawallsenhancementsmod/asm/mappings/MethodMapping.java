package fr.alexdoru.megawallsenhancementsmod.asm.mappings;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;

import static fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping.*;
import static org.objectweb.asm.Opcodes.*;

public enum MethodMapping {

    CHANGECURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "d", "changeCurrentItem", "(I)V"),
    CHECKHOTBARKEYS("b", "checkHotbarKeys", "(I)Z"),
    CLICKEVENT$GETVALUE(INVOKEVIRTUAL, CLICKEVENT, "b", "getValue", "()Ljava/lang/String;"),
    COLLECTION$REMOVE(INVOKEINTERFACE, COLLECTION, "remove", "(Ljava/lang/Object;)Z"),
    DISPLAYTITLE("a", "displayTitle", "(Ljava/lang/String;Ljava/lang/String;III)V"),
    DORENDERLAYER("a", "doRenderLayer", "(L" + ENTITYLIVINGBASE + ";FFFFFFF)V"),
    DRAWSCOREBOARDVALUES("a", "drawScoreboardValues", "(L" + SCOREOBJECTIVE + ";ILjava/lang/String;IIL" + NETWORKPLAYERINFO + ";)V"),
    DRAWSTRINGWITHSHADOW(INVOKEVIRTUAL, FONTRENDERER, "a", "drawStringWithShadow", "(Ljava/lang/String;FFI)I"),
    DROPONEITEM(INVOKEVIRTUAL, ENTITYPLAYERSP, "a", "dropOneItem", "(Z)L" + ENTITYITEM + ";"),
    FORMATPLAYERNAME(INVOKESTATIC, SCOREPLAYERTEAM, "a", "formatPlayerName", "(L" + TEAM + ";Ljava/lang/String;)Ljava/lang/String;"),
    GETCURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "h", "getCurrentItem", "()L" + ITEMSTACK + ";"),
    GETDISPLAYNAME("f_", "getDisplayName", "()L" + ICHATCOMPONENT + ";"),
    GETTABCOMPLETIONOPTION("a", "getTabCompletionOptions", "(L" + ICOMMANDSENDER + ";Ljava/lang/String;L" + BLOCKPOS + ";)Ljava/util/List;"),
    GUISCREENBOOK$DRAWSCREEN("a", "drawScreen", "(IIF)V"),
    GUISCREENBOOK$DRAWTEXTUREDMODALRECT(INVOKEVIRTUAL, GUISCREENBOOK, "b", "drawTexturedModalRect", "(IIIIII)V"),
    GUISCREENBOOK$INIT("<init>", "(L" + ENTITYPLAYER + ";L" + ITEMSTACK + ";Z)V"),
    GUISCREENBOOK$KEYTYPED("a", "keyTyped", "(CI)V"),
    HANDLECOMPONENTCLICK("a", "handleComponentClick", "(L" + ICHATCOMPONENT + ";)Z"),
    HANDLEMOUSECLICK(INVOKEVIRTUAL, GUICONTAINER, "a", "handleMouseClick", "(L" + SLOT + ";III)V"),
    ISDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "isDebugBoundingBox", "()Z"),
    ISPOTIONACTIVE("a", "isPotionActive", "(L" + POTION + ";)Z"),
    LISTFORMATTEDSTRINGTOWIDTH(INVOKEVIRTUAL, FONTRENDERER, "c", "listFormattedStringToWidth", "(Ljava/lang/String;I)Ljava/util/List;"),
    LOADRENDERER(INVOKEVIRTUAL, RENDERGLOBAL, "a", "loadRenderers", "()V"),
    MAP$PUT(INVOKEINTERFACE, MAP, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
    MAP$REMOVE(INVOKEINTERFACE, MAP, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;"),
    NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM("a", "handlePlayerListItem", "(L" + S38PACKETPLAYERLISTITEM + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLETEAMS("a", "handleTeams", "(L" + S3EPACKETTEAMS + ";)V"),
    NETHANDLERPLAYCLIENT$INIT("<init>", "(L" + MINECRAFT + ";L" + GUISCREEN + ";L" + NETWORKMANAGER + ";L" + GAMEPROFILE + ";)V"),
    NETWORKPLAYERINFO$GETGAMEPROFILE(INVOKEVIRTUAL, NETWORKPLAYERINFO, "a", "getGameProfile", "()L" + GAMEPROFILE + ";"),
    NETWORKPLAYERINFO$INIT("<init>", "(L" + S38PACKETPLAYERLISTITEM$ADDPLAYERDATA + ";)V"),
    RENDERDEBUGBOUNDINGBOX("b", "renderDebugBoundingBox", "(L" + ENTITY + ";DDDFF)V"),
    RENDERENTITIES("a", "renderEntities", "(L" + ENTITY + ";L" + ICAMERA + ";F)V"),
    RENDERENTITYSIMPLE(INVOKEVIRTUAL, RENDERMANAGER, "a", "renderEntitySimple", "(L" + ENTITY + ";F)Z"),
    RENDERGAMEOVERLAY("a", "renderGameOverlay", "(F)V"),
    RENDERMANAGER$INIT("<init>", "(L" + TEXTUREMANAGER + ";L" + RENDERITEM + ";)V"),
    RENDEROFFSETLIVINGLABEL("a", "renderOffsetLivingLabel", "(L" + ABSTRACTCLIENTPLAYER + ";DDDLjava/lang/String;FD)V"),
    RENDERPARTICLE("a", "renderParticle", "(L" + WORLDRENDERER + ";L" + ENTITY + ";FFFFFF)V"),
    RENDERPLAYERLIST("a", "renderPlayerlist", "(IL" + SCOREBOARD + ";L" + SCOREOBJECTIVE + ";)V"),
    RENDERRECORDOVERLAY(INVOKEVIRTUAL, GUIINGAMEFORGE, "renderRecordOverlay", "(IIF)V"),
    RENDERSCOREBOARD("a", "renderScoreboard", "(L" + SCOREOBJECTIVE + ";L" + SCALEDRESOLUTION + ";)V"),
    RIGHTCLICKMOUSE("ax", "rightClickMouse", "()V"),
    RUNTICK("s", "runTick", "()V"),
    SCOREBOARD$ADDPLAYERTOTEAM("a", "addPlayerToTeam", "(Ljava/lang/String;Ljava/lang/String;)Z"),
    SCOREBOARD$REMOVEPLAYERFROMTEAM("a", "removePlayerFromTeam", "(Ljava/lang/String;L" + SCOREPLAYERTEAM + ";)V"),
    SCOREBOARD$REMOVETEAM("d", "removeTeam", "(L" + SCOREPLAYERTEAM + ";)V"),
    SCOREPLAYERTEAM$SETNAMESUFFIX(INVOKEVIRTUAL, SCOREPLAYERTEAM, "c", "setNameSuffix", "(Ljava/lang/String;)V"),
    SENDCHATMESSAGE(INVOKEVIRTUAL, GUISCREEN, "b", "sendChatMessage", "(Ljava/lang/String;Z)V"),
    SETDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "setDebugBoundingBox", "(Z)V"),
    STRINGBUILDER$TOSTRING(INVOKEVIRTUAL, STRINGBUILDER, "toString", "()Ljava/lang/String;"),
    UPDATEFOGCOLOR("i", "updateFogColor", "(F)V"),
    UPDATELIGHTMAP("g", "updateLightmap", "(F)V");

    public final int opcode;
    public final String owner;
    public final String name;
    public final String desc;

    MethodMapping(String mcpName, String desc) {
        this.opcode = -1;
        this.owner = null;
        this.name = mcpName;
        this.desc = desc;
    }

    MethodMapping(String obfName, String mcpName, String desc) {
        this.opcode = -1;
        this.owner = null;
        this.name = ASMLoadingPlugin.isObf ? obfName : mcpName;
        this.desc = desc;
    }

    MethodMapping(int opcode, ClassMapping owner, String mcpName, String desc) {
        this.opcode = opcode;
        this.owner = owner.toString();
        this.name = mcpName;
        this.desc = desc;
    }

    MethodMapping(int opcode, ClassMapping owner, String obfName, String mcpName, String desc) {
        this.opcode = opcode;
        this.owner = owner.toString();
        this.name = ASMLoadingPlugin.isObf ? obfName : mcpName;
        this.desc = desc;
    }

}
