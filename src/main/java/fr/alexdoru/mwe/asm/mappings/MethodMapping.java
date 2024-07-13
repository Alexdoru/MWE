package fr.alexdoru.mwe.asm.mappings;

import fr.alexdoru.mwe.asm.loader.ASMLoadingPlugin;

import static fr.alexdoru.mwe.asm.mappings.ClassMapping.*;
import static org.objectweb.asm.Opcodes.*;

public enum MethodMapping {

    C08PACKETPLAYERBLOCKPLACEMENT$INIT3("<init>", "(L" + BLOCKPOS + ";IL" + ITEMSTACK + ";FFF)V"),
    CHATCOMPONENTSTYLE$APPENDSIBLING("a", "appendSibling", "(L" + ICHATCOMPONENT + ";)L" + ICHATCOMPONENT + ";"),
    CHATCOMPONENTTEXT$INIT(INVOKESPECIAL, CHATCOMPONENTTEXT, "<init>", "(Ljava/lang/String;)V"),
    CLICKEVENT$GETVALUE(INVOKEVIRTUAL, CLICKEVENT, "b", "getValue", "()Ljava/lang/String;"),
    COLLECTION$REMOVE(INVOKEINTERFACE, COLLECTION, "remove", "(Ljava/lang/Object;)Z"),
    COMMANDHANDLER$GETTABCOMPLETIONOPTION("a", "getTabCompletionOptions", "(L" + ICOMMANDSENDER + ";Ljava/lang/String;L" + BLOCKPOS + ";)Ljava/util/List;"),
    EFFECTRENDERER$ADDBLOCKDESTROYEFFECTS(INVOKEVIRTUAL, EFFECTRENDERER, "a", "addBlockDestroyEffects", "(L" + BLOCKPOS + ";L" + IBLOCKSTATE + ";)V"),
    ENTITY$SETCURRENTITEMORARMOR(INVOKEVIRTUAL, ENTITY, "c", "setCurrentItemOrArmor", "(IL" + ITEMSTACK + ";)V"),
    ENTITY$SETPOSITIONANDROTATION2("a", "setPositionAndRotation2", "(DDDFFIZ)V"),
    ENTITY$SETROTATIONYAWHEAD("f", "setRotationYawHead", "(F)V"),
    ENTITYFX$RENDERPARTICLE("a", "renderParticle", "(L" + WORLDRENDERER + ";L" + ENTITY + ";FFFFFF)V"),
    ENTITYLIVINGBASE$ISPOTIONACTIVE("a", "isPotionActive", "(L" + POTION + ";)Z"),
    ENTITYPLAYER$ONUPDATE("t_", "onUpdate", "()V"),
    ENTITYPLAYERSP$DROPONEITEM(INVOKEVIRTUAL, ENTITYPLAYERSP, "a", "dropOneItem", "(Z)L" + ENTITYITEM + ";"),
    ENTITYPLAYERSP$ISSPECTATOR(INVOKEVIRTUAL, ENTITYPLAYERSP, "v", "isSpectator", "()Z"),
    ENTITYPLAYERSP$SENDCHATMESSAGE("e", "sendChatMessage", "(Ljava/lang/String;)V"),
    ENTITYRENDERER$UPDATECAMERAANDRENDER("a", "updateCameraAndRender", "(FJ)V"),
    ENTITYRENDERER$UPDATEFOGCOLOR("i", "updateFogColor", "(F)V"),
    ENTITYRENDERER$UPDATELIGHTMAP("g", "updateLightmap", "(F)V"),
    ENUMCHATFORMATTING$GETTEXTWITHOUTFORMATTINGCODES(INVOKESTATIC, ENUMCHATFORMATTING, "a", "getTextWithoutFormattingCodes", "(Ljava/lang/String;)Ljava/lang/String;"),
    FLOATBUFFER$PUT(INVOKEVIRTUAL, FLOATBUFFER, "put", "(F)L" + FLOATBUFFER + ";"),
    FONTRENDERER$DRAWSTRINGWITHSHADOW(INVOKEVIRTUAL, FONTRENDERER, "a", "drawStringWithShadow", "(Ljava/lang/String;FFI)I"),
    FONTRENDERER$GETSTRINGWIDTH(INVOKEVIRTUAL, FONTRENDERER, "a", "getStringWidth", "(Ljava/lang/String;)I"),
    FONTRENDERER$LISTFORMATTEDSTRINGTOWIDTH(INVOKEVIRTUAL, FONTRENDERER, "c", "listFormattedStringToWidth", "(Ljava/lang/String;I)Ljava/util/List;"),
    GLSTATEMANAGER$ENABLEBLEND(INVOKESTATIC, GLSTATEMANAGER, "l", "enableBlend", "()V"),
    GUICHAT$ONAUTOCOMPLETERESPONSE("a", "onAutocompleteResponse", "([Ljava/lang/String;)V"),
    GUICHAT$SENDAUTOCOMPLETEREQUEST("a", "sendAutocompleteRequest", "(Ljava/lang/String;Ljava/lang/String;)V"),
    GUICONTAINER$CHECKHOTBARKEYS("b", "checkHotbarKeys", "(I)Z"),
    GUICONTAINER$HANDLEMOUSECLICK(INVOKEVIRTUAL, GUICONTAINER, "a", "handleMouseClick", "(L" + SLOT + ";III)V"),
    GUIINGAME$DISPLAYTITLE("a", "displayTitle", "(Ljava/lang/String;Ljava/lang/String;III)V"),
    GUIINGAME$GETFONTRENDERER(INVOKEVIRTUAL, GUIINGAME, "f", "getFontRenderer", "()L" + FONTRENDERER + ";"),
    GUIINGAME$RENDERGAMEOVERLAY(INVOKEVIRTUAL, GUIINGAME, "a", "renderGameOverlay", "(F)V"),
    GUIINGAME$RENDERSCOREBOARD("a", "renderScoreboard", "(L" + SCOREOBJECTIVE + ";L" + SCALEDRESOLUTION + ";)V"),
    GUIINGAMEFORGE$RENDERRECORDOVERLAY(INVOKEVIRTUAL, GUIINGAMEFORGE, "renderRecordOverlay", "(IIF)V"),
    GUINEWCHAT$DRAWCHAT("a", "drawChat", "(I)V"),
    GUINEWCHAT$GETCHATCOMPONENT("a", "getChatComponent", "(II)L" + ICHATCOMPONENT + ";"),
    GUINEWCHAT$PRINTCHATMESSAGE(INVOKEVIRTUAL, GUINEWCHAT, "a", "printChatMessage", "(L" + ICHATCOMPONENT + ";)V"),
    GUINEWCHAT$PRINTCHATMESSAGEWITHOPTIONALDELETION("a", "printChatMessageWithOptionalDeletion", "(L" + ICHATCOMPONENT + ";I)V"),
    GUIPLAYERTABOVERLAY$DRAWPING(INVOKEVIRTUAL, GUIPLAYERTABOVERLAY, "a", "drawPing", "(IIIL" + NETWORKPLAYERINFO + ";)V"),
    GUIPLAYERTABOVERLAY$DRAWSCOREBOARDVALUES(INVOKESPECIAL, GUIPLAYERTABOVERLAY, "a", "drawScoreboardValues", "(L" + SCOREOBJECTIVE + ";ILjava/lang/String;IIL" + NETWORKPLAYERINFO + ";)V"),
    GUIPLAYERTABOVERLAY$RENDERPLAYERLIST("a", "renderPlayerlist", "(IL" + SCOREBOARD + ";L" + SCOREOBJECTIVE + ";)V"),
    GUISCREEN$HANDLECOMPONENTCLICK("a", "handleComponentClick", "(L" + ICHATCOMPONENT + ";)Z"),
    GUISCREEN$SENDCHATMESSAGE(INVOKEVIRTUAL, GUISCREEN, "b", "sendChatMessage", "(Ljava/lang/String;Z)V"),
    GUISCREENBOOK$DRAWSCREEN("a", "drawScreen", "(IIF)V"),
    GUISCREENBOOK$DRAWTEXTUREDMODALRECT(INVOKEVIRTUAL, GUISCREENBOOK, "b", "drawTexturedModalRect", "(IIIIII)V"),
    GUISCREENBOOK$INIT("<init>", "(L" + ENTITYPLAYER + ";L" + ITEMSTACK + ";Z)V"),
    GUISCREENBOOK$KEYTYPED("a", "keyTyped", "(CI)V"),
    GUIUTILRENDERCOMPONENTS$SPLITTEXT("a", "splitText", "(L" + ICHATCOMPONENT + ";IL" + FONTRENDERER + ";ZZ)Ljava/util/List;"),
    ICHATCOMPONENT$GETUNFORMATTEDTEXT(INVOKEINTERFACE, ICHATCOMPONENT, "c", "getUnformattedText", "()Ljava/lang/String;"),
    INVENTORYPLAYER$CHANGECURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "d", "changeCurrentItem", "(I)V"),
    INVENTORYPLAYER$GETCURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "h", "getCurrentItem", "()L" + ITEMSTACK + ";"),
    LAYERARMORBASE$SHOULDCOMBINETEXTURES("b", "shouldCombineTextures", "()Z"),
    LAYERARROW$DORENDERLAYER("a", "doRenderLayer", "(L" + ENTITYLIVINGBASE + ";FFFFFFF)V"),
    LIST$ADD(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z"),
    MAP$PUT(INVOKEINTERFACE, MAP, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
    MAP$REMOVE(INVOKEINTERFACE, MAP, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;"),
    MATH$MAX(INVOKESTATIC, MATH, "max", "(II)I"),
    MINECRAFT$RIGHTCLICKMOUSE("ax", "rightClickMouse", "()V"),
    MINECRAFT$RUNTICK("s", "runTick", "()V"),
    NETHANDLERPLAYCLIENT$HANDLEBLOCKBREAKANIM("a", "handleBlockBreakAnim", "(L" + S25PACKETBLOCKBREAKANIM + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLEBLOCKCHANGE("a", "handleBlockChange", "(L" + S23PACKETBLOCKCHANGE + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLEMULTIBLOCKCHANGE("a", "handleMultiBlockChange", "(L" + S22PACKETMULTIBLOCKCHANGE + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM("a", "handlePlayerListItem", "(L" + S38PACKETPLAYERLISTITEM + ";)V"),
    NETHANDLERPLAYCLIENT$HANDLETEAMS("a", "handleTeams", "(L" + S3EPACKETTEAMS + ";)V"),
    NETHANDLERPLAYCLIENT$INIT("<init>", "(L" + MINECRAFT + ";L" + GUISCREEN + ";L" + NETWORKMANAGER + ";L" + GAMEPROFILE + ";)V"),
    NETWORKMANAGER$CHANNELREAD0("a", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;L" + PACKET + ";)V"),
    NETWORKMANAGER$SENDPACKET("a", "sendPacket", "(L" + PACKET + ";)V"),
    NETWORKPLAYERINFO$1$SKINAVAILABLE("a", "skinAvailable", null),
    NETWORKPLAYERINFO$ACCESS002(INVOKESTATIC, NETWORKPLAYERINFO, "a", "access$002", "(L" + NETWORKPLAYERINFO + ";L" + RESOURCELOCATION + ";)L" + RESOURCELOCATION + ";"),
    NETWORKPLAYERINFO$GETDISPLAYNAME("k", "getDisplayName", "()L" + ICHATCOMPONENT + ";"),
    NETWORKPLAYERINFO$GETGAMETYPE(INVOKEVIRTUAL, NETWORKPLAYERINFO, "b", "getGameType", "()L" + WORLDSETTINGS$GAMETYPE + ";"),
    PACKETTHREADUTIL$CHECKTHREADANDENQUEUE(INVOKESTATIC, PACKETTHREADUTIL, "a", "checkThreadAndEnqueue", "(L" + PACKET + ";L" + INETHANDLER + ";L" + ITHREADLISTENER + ";)V"),
    RENDERERLIVINGENTITY$ROTATECORPSE("a", "rotateCorpse", "(L" + ENTITYLIVINGBASE + ";FFF)V"),
    RENDERERLIVINGENTITY$SETBRIGHTNESS("a", "setBrightness", "(L" + ENTITYLIVINGBASE + ";FZ)Z"),
    RENDERGLOBAL$LOADRENDERER(INVOKEVIRTUAL, RENDERGLOBAL, "a", "loadRenderers", "()V"),
    RENDERGLOBAL$PLAYAUXSFX("a", "playAuxSFX", "(L" + ENTITYPLAYER + ";IL" + BLOCKPOS + ";I)V"),
    RENDERGLOBAL$RENDERENTITIES("a", "renderEntities", "(L" + ENTITY + ";L" + ICAMERA + ";F)V"),
    RENDERMANAGER$INIT("<init>", "(L" + TEXTUREMANAGER + ";L" + RENDERITEM + ";)V"),
    RENDERMANAGER$ISDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "isDebugBoundingBox", "()Z"),
    RENDERMANAGER$RENDERDEBUGBOUNDINGBOX("b", "renderDebugBoundingBox", "(L" + ENTITY + ";DDDFF)V"),
    RENDERMANAGER$RENDERENTITYSIMPLE(INVOKEVIRTUAL, RENDERMANAGER, "a", "renderEntitySimple", "(L" + ENTITY + ";F)Z"),
    RENDERMANAGER$SETDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "setDebugBoundingBox", "(Z)V"),
    RENDERPLAYER$RENDEROFFSETLIVINGLABEL("a", "renderOffsetLivingLabel", "(L" + ABSTRACTCLIENTPLAYER + ";DDDLjava/lang/String;FD)V"),
    SCORE$GETSCOREPOINTS(INVOKEVIRTUAL, SCORE, "c", "getScorePoints", "()I"),
    SCOREBOARD$ADDPLAYERTOTEAM("a", "addPlayerToTeam", "(Ljava/lang/String;Ljava/lang/String;)Z"),
    SCOREBOARD$REMOVEPLAYERFROMTEAM("a", "removePlayerFromTeam", "(Ljava/lang/String;L" + SCOREPLAYERTEAM + ";)V"),
    SCOREBOARD$REMOVETEAM("d", "removeTeam", "(L" + SCOREPLAYERTEAM + ";)V"),
    SCOREOBJECTIVE$GETDISPLAYNAME(INVOKEVIRTUAL, SCOREOBJECTIVE, "d", "getDisplayName", "()Ljava/lang/String;"),
    SCOREPLAYERTEAM$FORMATPLAYERNAME(INVOKESTATIC, SCOREPLAYERTEAM, "a", "formatPlayerName", "(L" + TEAM + ";Ljava/lang/String;)Ljava/lang/String;"),
    SCOREPLAYERTEAM$SETNAMESUFFIX(INVOKEVIRTUAL, SCOREPLAYERTEAM, "c", "setNameSuffix", "(Ljava/lang/String;)V"),
    STRINGBUILDER$APPEND_I(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$APPEND_STRING(INVOKEVIRTUAL, STRINGBUILDER, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$TOSTRING(INVOKEVIRTUAL, STRINGBUILDER, "toString", "()Ljava/lang/String;"),
    TESSELLATOR$DRAW(INVOKEVIRTUAL, TESSELLATOR, "b", "draw", "()V"),
    TESSELLATOR$GETINSTANCE(INVOKESTATIC, TESSELLATOR, "a", "getInstance", "()L" + TESSELLATOR + ";"),
    WORLDCLIENT$SENDBLOCKBREAKPROGRESS(INVOKEVIRTUAL, WORLDCLIENT, "c", "sendBlockBreakProgress", "(IL" + BLOCKPOS + ";I)V");

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
        this.name = ASMLoadingPlugin.isObf() ? obfName : mcpName;
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
        this.name = ASMLoadingPlugin.isObf() ? obfName : mcpName;
        this.desc = desc;
    }

    MethodMapping(int opcode, String owner, String mcpName, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = mcpName;
        this.desc = desc;
    }

}
