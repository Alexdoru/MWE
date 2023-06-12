package fr.alexdoru.megawallsenhancementsmod.asm.mappings;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;

import static fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping.*;
import static org.objectweb.asm.Opcodes.*;

public enum MethodMapping {

    CHANGECURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "d", "changeCurrentItem", "(I)V"),
    CHATCOMPONENTSTYLE$APPENDSIBLING("a", "appendSibling", "(L" + ICHATCOMPONENT + ";)L" + ICHATCOMPONENT + ";"),
    CHATCOMPONENTTEXT$INIT(INVOKESPECIAL, CHATCOMPONENTTEXT, "<init>", "(Ljava/lang/String;)V"),
    CHECKHOTBARKEYS("b", "checkHotbarKeys", "(I)Z"),
    CLICKEVENT$GETVALUE(INVOKEVIRTUAL, CLICKEVENT, "b", "getValue", "()Ljava/lang/String;"),
    CLIENTCOMMANDHANDLER$AUTOCOMPLETE(INVOKEVIRTUAL, CLIENTCOMMANDHANDLER, "autoComplete", "(Ljava/lang/String;Ljava/lang/String;)V"),
    COLLECTION$REMOVE(INVOKEINTERFACE, COLLECTION, "remove", "(Ljava/lang/Object;)Z"),
    DISPLAYTITLE("a", "displayTitle", "(Ljava/lang/String;Ljava/lang/String;III)V"),
    DORENDERLAYER("a", "doRenderLayer", "(L" + ENTITYLIVINGBASE + ";FFFFFFF)V"),
    DRAWSCOREBOARDVALUES(INVOKESPECIAL, GUIPLAYERTABOVERLAY, "a", "drawScoreboardValues", "(L" + SCOREOBJECTIVE + ";ILjava/lang/String;IIL" + NETWORKPLAYERINFO + ";)V"),
    DROPONEITEM(INVOKEVIRTUAL, ENTITYPLAYERSP, "a", "dropOneItem", "(Z)L" + ENTITYITEM + ";"),
    EFFECTRENDERER$ADDBLOCKDESTROYEFFECTS(INVOKEVIRTUAL, EFFECTRENDERER, "a", "addBlockDestroyEffects", "(L" + BLOCKPOS + ";L" + IBLOCKSTATE + ";)V"),
    ENTITYPLAYERSP$SENDCHATMESSAGE("e", "sendChatMessage", "(Ljava/lang/String;)V"),
    FLOATBUFFER$PUT(INVOKEVIRTUAL, FLOATBUFFER, "put", "(F)L" + FLOATBUFFER + ";"),
    FONTRENDERER$DRAWSTRINGWITHSHADOW(INVOKEVIRTUAL, FONTRENDERER, "a", "drawStringWithShadow", "(Ljava/lang/String;FFI)I"),
    FONTRENDERER$GETSTRINGWIDTH(INVOKEVIRTUAL, FONTRENDERER, "a", "getStringWidth", "(Ljava/lang/String;)I"),
    FORMATPLAYERNAME(INVOKESTATIC, SCOREPLAYERTEAM, "a", "formatPlayerName", "(L" + TEAM + ";Ljava/lang/String;)Ljava/lang/String;"),
    GETCURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "h", "getCurrentItem", "()L" + ITEMSTACK + ";"),
    GETDISPLAYNAME("f_", "getDisplayName", "()L" + ICHATCOMPONENT + ";"),
    GETTABCOMPLETIONOPTION("a", "getTabCompletionOptions", "(L" + ICOMMANDSENDER + ";Ljava/lang/String;L" + BLOCKPOS + ";)Ljava/util/List;"),
    GLSTATEMANAGER$ALPHAFUNC(INVOKESTATIC, GLSTATEMANAGER, "a", "alphaFunc", "(IF)V"),
    GLSTATEMANAGER$COLOR(INVOKESTATIC, GLSTATEMANAGER, "c", "color", "(FFFF)V"),
    GLSTATEMANAGER$ENABLEBLEND(INVOKESTATIC, GLSTATEMANAGER, "l", "enableBlend", "()V"),
    GLSTATEMANAGER$POPMATRIX(INVOKESTATIC, GLSTATEMANAGER, "F", "popMatrix", "()V"),
    GLSTATEMANAGER$PUSHMATRIX(INVOKESTATIC, GLSTATEMANAGER, "E", "pushMatrix", "()V"),
    GUICHAT$ONAUTOCOMPLETERESPONSE("a", "onAutocompleteResponse", "([Ljava/lang/String;)V"),
    GUICHAT$SENDAUTOCOMPLETEREQUEST("a", "sendAutocompleteRequest", "(Ljava/lang/String;Ljava/lang/String;)V"),
    GUIINGAME$GETFONTRENDERER(INVOKEVIRTUAL, GUIINGAME, "f", "getFontRenderer", "()L" + FONTRENDERER + ";"),
    GUINEWCHAT$DRAWCHAT("a", "drawChat", "(I)V"),
    GUINEWCHAT$PRINTCHATMESSAGE(INVOKEVIRTUAL, GUINEWCHAT, "a", "printChatMessage", "(L" + ICHATCOMPONENT + ";)V"),
    GUIPLAYERTABOVERLAY$DRAWPING(INVOKEVIRTUAL, GUIPLAYERTABOVERLAY, "a", "drawPing", "(IIIL" + NETWORKPLAYERINFO + ";)V"),
    GUISCREENBOOK$DRAWSCREEN("a", "drawScreen", "(IIF)V"),
    GUISCREENBOOK$DRAWTEXTUREDMODALRECT(INVOKEVIRTUAL, GUISCREENBOOK, "b", "drawTexturedModalRect", "(IIIIII)V"),
    GUISCREENBOOK$INIT("<init>", "(L" + ENTITYPLAYER + ";L" + ITEMSTACK + ";Z)V"),
    GUISCREENBOOK$KEYTYPED("a", "keyTyped", "(CI)V"),
    GUIUTILRENDERCOMPONENTS$SPLITTEXT("a", "splitText", "(L" + ICHATCOMPONENT + ";IL" + FONTRENDERER + ";ZZ)Ljava/util/List;"),
    HANDLECOMPONENTCLICK("a", "handleComponentClick", "(L" + ICHATCOMPONENT + ";)Z"),
    HANDLEMOUSECLICK(INVOKEVIRTUAL, GUICONTAINER, "a", "handleMouseClick", "(L" + SLOT + ";III)V"),
    ISDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "isDebugBoundingBox", "()Z"),
    ISPOTIONACTIVE("a", "isPotionActive", "(L" + POTION + ";)Z"),
    LAYERARMORBASE$SHOULDCOMBINETEXTURES("b", "shouldCombineTextures", "()Z"),
    LIST$ADD(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z"),
    LISTFORMATTEDSTRINGTOWIDTH(INVOKEVIRTUAL, FONTRENDERER, "c", "listFormattedStringToWidth", "(Ljava/lang/String;I)Ljava/util/List;"),
    LOADRENDERER(INVOKEVIRTUAL, RENDERGLOBAL, "a", "loadRenderers", "()V"),
    MAP$PUT(INVOKEINTERFACE, MAP, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
    MAP$REMOVE(INVOKEINTERFACE, MAP, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;"),
    MATH$MAX(INVOKESTATIC, MATH, "max", "(II)I"),
    NETHANDLERPLAYCLIENT$HANDLEBLOCKBREAKANIM("a", "handleBlockBreakAnim", "(L" + S25PACKETBLOCKBREAKANIM + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLEENTITYTELEPORT("a", "handleEntityTeleport", "(L" + S18PACKETENTITYTELEPORT + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM("a", "handlePlayerListItem", "(L" + S38PACKETPLAYERLISTITEM + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLETEAMS("a", "handleTeams", "(L" + S3EPACKETTEAMS + ";)V"),
    NETHANDLERPLAYCLIENT$INIT("<init>", "(L" + MINECRAFT + ";L" + GUISCREEN + ";L" + NETWORKMANAGER + ";L" + GAMEPROFILE + ";)V"),
    NETWORKPLAYERINFO$ACCESS002(INVOKESTATIC, NETWORKPLAYERINFO, "a", "access$002", "(L" + NETWORKPLAYERINFO + ";L" + RESOURCELOCATION + ";)L" + RESOURCELOCATION + ";"),
    NETWORKPLAYERINFO$GETDISPLAYNAME("k", "getDisplayName", "()L" + ICHATCOMPONENT + ";"),
    NETWORKPLAYERINFO$GETGAMEPROFILE(INVOKEVIRTUAL, NETWORKPLAYERINFO, "a", "getGameProfile", "()L" + GAMEPROFILE + ";"),
    NETWORKPLAYERINFO$GETGAMETYPE(INVOKEVIRTUAL, NETWORKPLAYERINFO, "b", "getGameType", "()L" + WORLDSETTINGS$GAMETYPE + ";"),
    NETWORKPLAYERINFO$INIT("<init>", "(L" + S38PACKETPLAYERLISTITEM$ADDPLAYERDATA + ";)V"),
    PROFILER$ENDSECTION(INVOKEVIRTUAL, PROFILER, "b", "endSection", "()V"),
    RENDERDEBUGBOUNDINGBOX("b", "renderDebugBoundingBox", "(L" + ENTITY + ";DDDFF)V"),
    RENDERENTITIES("a", "renderEntities", "(L" + ENTITY + ";L" + ICAMERA + ";F)V"),
    RENDERENTITYSIMPLE(INVOKEVIRTUAL, RENDERMANAGER, "a", "renderEntitySimple", "(L" + ENTITY + ";F)Z"),
    RENDERERLIVINGENTITY$RENDERNAME("b", "renderName", "(L" + ENTITYLIVINGBASE + ";DDD)V"),
    RENDERERLIVINGENTITY$ROTATECORPSE("a", "rotateCorpse", "(L" + ENTITYLIVINGBASE + ";FFF)V"),
    RENDERERLIVINGENTITY$SETBRIGHTNESS("a", "setBrightness", "(L" + ENTITYLIVINGBASE + ";FZ)Z"),
    RENDERGAMEOVERLAY("a", "renderGameOverlay", "(F)V"),
    RENDERGLOBAL$PLAYAUXSFX("a", "playAuxSFX", "(L" + ENTITYPLAYER + ";IL" + BLOCKPOS + ";I)V"),
    RENDERMANAGER$INIT("<init>", "(L" + TEXTUREMANAGER + ";L" + RENDERITEM + ";)V"),
    RENDEROFFSETLIVINGLABEL("a", "renderOffsetLivingLabel", "(L" + ABSTRACTCLIENTPLAYER + ";DDDLjava/lang/String;FD)V"),
    RENDERPARTICLE("a", "renderParticle", "(L" + WORLDRENDERER + ";L" + ENTITY + ";FFFFFF)V"),
    RENDERPLAYERLIST("a", "renderPlayerlist", "(IL" + SCOREBOARD + ";L" + SCOREOBJECTIVE + ";)V"),
    RENDERRECORDOVERLAY(INVOKEVIRTUAL, GUIINGAMEFORGE, "renderRecordOverlay", "(IIF)V"),
    RENDERSCOREBOARD("a", "renderScoreboard", "(L" + SCOREOBJECTIVE + ";L" + SCALEDRESOLUTION + ";)V"),
    RIGHTCLICKMOUSE("ax", "rightClickMouse", "()V"),
    RUNTICK("s", "runTick", "()V"),
    SCORE$GETSCOREPOINTS(INVOKEVIRTUAL, SCORE, "c", "getScorePoints", "()I"),
    SCOREBOARD$ADDPLAYERTOTEAM("a", "addPlayerToTeam", "(Ljava/lang/String;Ljava/lang/String;)Z"),
    SCOREBOARD$REMOVEPLAYERFROMTEAM("a", "removePlayerFromTeam", "(Ljava/lang/String;L" + SCOREPLAYERTEAM + ";)V"),
    SCOREBOARD$REMOVETEAM("d", "removeTeam", "(L" + SCOREPLAYERTEAM + ";)V"),
    SCOREOBJECTIVE$GETDISPLAYNAME(INVOKEVIRTUAL, SCOREOBJECTIVE, "d", "getDisplayName", "()Ljava/lang/String;"),
    SCOREPLAYERTEAM$SETNAMESUFFIX(INVOKEVIRTUAL, SCOREPLAYERTEAM, "c", "setNameSuffix", "(Ljava/lang/String;)V"),
    SENDCHATMESSAGE(INVOKEVIRTUAL, GUISCREEN, "b", "sendChatMessage", "(Ljava/lang/String;Z)V"),
    SETDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "setDebugBoundingBox", "(Z)V"),
    STRINGBUILDER$APPEND_I(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$APPEND_STRING(INVOKEVIRTUAL, STRINGBUILDER, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$TOSTRING(INVOKEVIRTUAL, STRINGBUILDER, "toString", "()Ljava/lang/String;"),
    TESSELLATOR$DRAW(INVOKEVIRTUAL, TESSELLATOR, "b", "draw", "()V"),
    TESSELLATOR$GETINSTANCE(INVOKESTATIC, TESSELLATOR, "a", "getInstance", "()L" + TESSELLATOR + ";"),
    UPDATEFOGCOLOR("i", "updateFogColor", "(F)V"),
    UPDATELIGHTMAP("g", "updateLightmap", "(F)V"),
    WORLD$UPDATEENTITYWITHOPTIONALFORCE("a", "updateEntityWithOptionalForce", "(L" + ENTITY + ";Z)V");

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

    MethodMapping(int opcode, String owner, String mcpName, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = mcpName;
        this.desc = desc;
    }

}
