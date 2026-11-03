package fr.alexdoru.mwe.asm.mappings;

import fr.alexdoru.mwe.api.asm.IMethodInfo;
import fr.alexdoru.mwe.asm.MWELoadingPlugin;

import static fr.alexdoru.mwe.asm.mappings.ClassMapping.*;
import static org.objectweb.asm.Opcodes.*;

public enum MethodMapping implements IMethodInfo {

    C08PACKETPLAYERBLOCKPLACEMENT$INIT3("<init>", "(" + BLOCKPOS.desc() + "I" + ITEMSTACK.desc() + "FFF)V"),
    CHATCOMPONENTSTYLE$APPENDSIBLING("a", "appendSibling", "(" + ICHATCOMPONENT.desc() + ")" + ICHATCOMPONENT.desc()),
    CHATCOMPONENTTEXT$INIT(INVOKESPECIAL, CHATCOMPONENTTEXT, "<init>", "(Ljava/lang/String;)V"),
    CHATLINE$INIT(INVOKESPECIAL, CHATLINE, "<init>", "(I" + ICHATCOMPONENT.desc() + "I)V"),
    CLICKEVENT$GETVALUE(INVOKEVIRTUAL, CLICKEVENT, "b", "getValue", "()Ljava/lang/String;"),
    COMMANDHANDLER$EXECUTECOMMAND("a", "executeCommand", "(" + ICOMMANDSENDER.desc() + "Ljava/lang/String;)I"),
    COMMANDHANDLER$GETTABCOMPLETIONOPTION("a", "getTabCompletionOptions", "(" + ICOMMANDSENDER.desc() + "Ljava/lang/String;" + BLOCKPOS.desc() + ")Ljava/util/List;"),
    COMMANDHANDLER$REGISTERCOMMAND("a", "registerCommand", "(" + ICOMMAND.desc() + ")" + ICOMMAND.desc()),
    EFFECTRENDERER$ADDBLOCKDESTROYEFFECTS(INVOKEVIRTUAL, EFFECTRENDERER, "a", "addBlockDestroyEffects", "(" + BLOCKPOS.desc() + IBLOCKSTATE.desc() + ")V"),
    ENTITY$SETCURRENTITEMORARMOR(INVOKEVIRTUAL, ENTITY, "c", "setCurrentItemOrArmor", "(I" + ITEMSTACK.desc() + ")V"),
    ENTITY$SETPOSITIONANDROTATION2("a", "setPositionAndRotation2", "(DDDFFIZ)V"),
    ENTITY$SETROTATIONYAWHEAD("f", "setRotationYawHead", "(F)V"),
    ENTITYFX$RENDERPARTICLE("a", "renderParticle", "(" + WORLDRENDERER.desc() + ENTITY.desc() + "FFFFFF)V"),
    ENTITYLIVINGBASE$GETDISPLAYNAME(INVOKEVIRTUAL, ENTITYLIVINGBASE, "f_", "getDisplayName", "()" + ICHATCOMPONENT.desc()),
    ENTITYLIVINGBASE$ISPOTIONACTIVE("a", "isPotionActive", "(" + POTION.desc() + ")Z"),
    ENTITYPLAYER$ONITEMUSEFINISH(INVOKEVIRTUAL, ENTITYPLAYER, "s", "onItemUseFinish", "()V"),
    ENTITYPLAYER$ONUPDATE("t_", "onUpdate", "()V"),
    ENTITYPLAYERSP$DROPONEITEM(INVOKEVIRTUAL, ENTITYPLAYERSP, "a", "dropOneItem", "(Z)" + ENTITYITEM.desc()),
    ENTITYPLAYERSP$ISSPECTATOR(INVOKEVIRTUAL, ENTITYPLAYERSP, "v", "isSpectator", "()Z"),
    ENTITYPLAYERSP$ONLIVINGUPDATE("m", "onLivingUpdate", "()V"),
    ENTITYPLAYERSP$SENDCHATMESSAGE("e", "sendChatMessage", "(Ljava/lang/String;)V"),
    ENTITYRENDERER$UPDATECAMERAANDRENDER("a", "updateCameraAndRender", "(FJ)V"),
    ENTITYRENDERER$UPDATEFOGCOLOR("i", "updateFogColor", "(F)V"),
    ENTITYRENDERER$UPDATELIGHTMAP("g", "updateLightmap", "(F)V"),
    FLOATBUFFER$PUT(INVOKEVIRTUAL, FLOATBUFFER, "put", "(F)" + FLOATBUFFER.desc()),
    FONTRENDERER$DRAWSTRINGWITHSHADOW(INVOKEVIRTUAL, FONTRENDERER, "a", "drawStringWithShadow", "(Ljava/lang/String;FFI)I"),
    FONTRENDERER$GETSTRINGWIDTH(INVOKEVIRTUAL, FONTRENDERER, "a", "getStringWidth", "(Ljava/lang/String;)I"),
    FONTRENDERER$LISTFORMATTEDSTRINGTOWIDTH(INVOKEVIRTUAL, FONTRENDERER, "c", "listFormattedStringToWidth", "(Ljava/lang/String;I)Ljava/util/List;"),
    FORGEEVENTFACTORY$ONITEMUSETICK(INVOKESTATIC, FORGEEVENTFACTORY, "onItemUseTick", "(" + ENTITYPLAYER.desc() + ITEMSTACK.desc() + "I)I"),
    GLSTATEMANAGER$ENABLEBLEND(INVOKESTATIC, GLSTATEMANAGER, "l", "enableBlend", "()V"),
    GUICHAT$DRAWSCREEN("a", "drawScreen", "(IIF)V"),
    GUICHAT$HANDLECOMPONENTCLICK(INVOKEVIRTUAL, GUICHAT, "a", "handleComponentClick", "(" + ICHATCOMPONENT.desc() + ")Z"),
    GUICHAT$INITGUI("b", "initGui", "()V"),
    GUICHAT$KEYTYPED("a", "keyTyped", "(CI)V"),
    GUICHAT$MOUSECLICKED("a", "mouseClicked", "(III)V"),
    GUICHAT$ONAUTOCOMPLETERESPONSE("a", "onAutocompleteResponse", "([Ljava/lang/String;)V"),
    GUICHAT$ONGUICLOSED("m", "onGuiClosed", "()V"),
    GUICHAT$SENDAUTOCOMPLETEREQUEST("a", "sendAutocompleteRequest", "(Ljava/lang/String;Ljava/lang/String;)V"),
    GUICONTAINER$CHECKHOTBARKEYS("b", "checkHotbarKeys", "(I)Z"),
    GUICONTAINER$DRAWSLOT("a", "drawSlot", "(" + SLOT.desc() + ")V"),
    GUICONTAINER$HANDLEMOUSECLICK(INVOKEVIRTUAL, GUICONTAINER, "a", "handleMouseClick", "(" + SLOT.desc() + "III)V"),
    GUIINGAME$DISPLAYTITLE("a", "displayTitle", "(Ljava/lang/String;Ljava/lang/String;III)V"),
    GUIINGAME$GETFONTRENDERER(INVOKEVIRTUAL, GUIINGAME, "f", "getFontRenderer", "()" + FONTRENDERER.desc()),
    GUIINGAME$RENDERGAMEOVERLAY(INVOKEVIRTUAL, GUIINGAME, "a", "renderGameOverlay", "(F)V"),
    GUIINGAME$RENDERSCOREBOARD("a", "renderScoreboard", "(" + SCOREOBJECTIVE.desc() + SCALEDRESOLUTION.desc() + ")V"),
    GUIINGAMEFORGE$RENDERRECORDOVERLAY(INVOKEVIRTUAL, GUIINGAMEFORGE, "renderRecordOverlay", "(IIF)V"),
    GUINEWCHAT$CLEARCHATMESSAGES("a", "clearChatMessages", "()V"),
    GUINEWCHAT$DELETECHATLINE("c", "deleteChatLine", "(I)V"),
    GUINEWCHAT$DRAWCHAT("a", "drawChat", "(I)V"),
    GUINEWCHAT$GETCHATCOMPONENT("a", "getChatComponent", "(II)" + ICHATCOMPONENT.desc()),
    GUINEWCHAT$PRINTCHATMESSAGE(INVOKEVIRTUAL, GUINEWCHAT, "a", "printChatMessage", "(" + ICHATCOMPONENT.desc() + ")V"),
    GUINEWCHAT$PRINTCHATMESSAGEWITHOPTIONALDELETION("a", "printChatMessageWithOptionalDeletion", "(" + ICHATCOMPONENT.desc() + "I)V"),
    GUINEWCHAT$SCROLL("b", "scroll", "(I)V"),
    GUINEWCHAT$SETCHATLINE("a", "setChatLine", "(" + ICHATCOMPONENT.desc() + "IIZ)V"),
    GUIPLAYERTABOVERLAY$DRAWPING(INVOKEVIRTUAL, GUIPLAYERTABOVERLAY, "a", "drawPing", "(III" + NETWORKPLAYERINFO.desc() + ")V"),
    GUIPLAYERTABOVERLAY$DRAWSCOREBOARDVALUES(INVOKESPECIAL, GUIPLAYERTABOVERLAY, "a", "drawScoreboardValues", "(" + SCOREOBJECTIVE.desc() + "ILjava/lang/String;II" + NETWORKPLAYERINFO.desc() + ")V"),
    GUIPLAYERTABOVERLAY$RENDERPLAYERLIST("a", "renderPlayerlist", "(I" + SCOREBOARD.desc() + SCOREOBJECTIVE.desc() + ")V"),
    GUISCREEN$DRAWHOVERINGTEXT(INVOKEVIRTUAL, GUISCREEN, "a", "drawHoveringText", "(Ljava/util/List;II)V"),
    GUISCREEN$HANDLECOMPONENTCLICK("a", "handleComponentClick", "(" + ICHATCOMPONENT.desc() + ")Z"),
    GUISCREEN$SENDCHATMESSAGE(INVOKEVIRTUAL, GUISCREEN, "b", "sendChatMessage", "(Ljava/lang/String;Z)V"),
    GUISCREENBOOK$DRAWSCREEN("a", "drawScreen", "(IIF)V"),
    GUISCREENBOOK$DRAWTEXTUREDMODALRECT(INVOKEVIRTUAL, GUISCREENBOOK, "b", "drawTexturedModalRect", "(IIIIII)V"),
    GUISCREENBOOK$KEYTYPED("a", "keyTyped", "(CI)V"),
    GUITEXTFIELD$DRAWTEXTBOX(INVOKEVIRTUAL, GUITEXTFIELD, "g", "drawTextBox", "()V"),
    GUIUTILRENDERCOMPONENTS$SPLITTEXT("a", "splitText", "(" + ICHATCOMPONENT.desc() + "I" + FONTRENDERER.desc() + "ZZ)Ljava/util/List;"),
    ICHATCOMPONENT$GETFORMATTEDTEXT(INVOKEINTERFACE, ICHATCOMPONENT, "d", "getFormattedText", "()Ljava/lang/String;"),
    INVENTORYPLAYER$CHANGECURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "d", "changeCurrentItem", "(I)V"),
    INVENTORYPLAYER$GETCURRENTITEM(INVOKEVIRTUAL, INVENTORYPLAYER, "h", "getCurrentItem", "()" + ITEMSTACK.desc()),
    KEYBINDING$ISKEYDOWN(INVOKEVIRTUAL, KEYBINDING, "d", "isKeyDown", "()Z"),
    LAYERARMORBASE$SHOULDCOMBINETEXTURES("b", "shouldCombineTextures", "()Z"),
    LAYERARROW$DORENDERLAYER("a", "doRenderLayer", "(" + ENTITYLIVINGBASE.desc() + "FFFFFFF)V"),
    LIST$ADD(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z"),
    LIST$ADD_AT_INDEX(INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V"),
    LOGGER$INFO(INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Ljava/lang/String;)V"),
    MAP$PUT(INVOKEINTERFACE, MAP, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
    MAP$REMOVE(INVOKEINTERFACE, MAP, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;"),
    MATH$MAX(INVOKESTATIC, MATH, "max", "(II)I"),
    MINECRAFT$RIGHTCLICKMOUSE("ax", "rightClickMouse", "()V"),
    MINECRAFT$RUNTICK("s", "runTick", "()V"),
    NETHANDLERPLAYCLIENT$HANDLEBLOCKBREAKANIM("a", "handleBlockBreakAnim", "(" + S25PACKETBLOCKBREAKANIM.desc() + ")V"),
    NETHANDLERPLAYCLIENT$HANDLEBLOCKCHANGE("a", "handleBlockChange", "(" + S23PACKETBLOCKCHANGE.desc() + ")V"),
    NETHANDLERPLAYCLIENT$HANDLEENTITYMETADATA("a", "handleEntityMetadata", "(" + S1CPACKETENTITYMETADATA.desc() + ")V"),
    NETHANDLERPLAYCLIENT$HANDLEMULTIBLOCKCHANGE("a", "handleMultiBlockChange", "(" + S22PACKETMULTIBLOCKCHANGE.desc() + ")V"),
    NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM("a", "handlePlayerListItem", "(" + S38PACKETPLAYERLISTITEM.desc() + ")V"),
    NETHANDLERPLAYCLIENT$HANDLETEAMS("a", "handleTeams", "(" + S3EPACKETTEAMS.desc() + ")V"),
    NETWORKMANAGER$CHANNELREAD0("a", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;" + PACKET.desc() + ")V"),
    NETWORKMANAGER$SENDPACKET("a", "sendPacket", "(" + PACKET.desc() + ")V"),
    NETWORKPLAYERINFO$1$SKINAVAILABLE("a", "skinAvailable", null),
    NETWORKPLAYERINFO$ACCESS002(INVOKESTATIC, NETWORKPLAYERINFO, "a", "access$002", "(" + NETWORKPLAYERINFO.desc() + RESOURCELOCATION.desc() + ")" + RESOURCELOCATION.desc()),
    NETWORKPLAYERINFO$GETDISPLAYNAME("k", "getDisplayName", "()" + ICHATCOMPONENT.desc()),
    NETWORKPLAYERINFO$GETGAMETYPE(INVOKEVIRTUAL, NETWORKPLAYERINFO, "b", "getGameType", "()" + WORLDSETTINGS$GAMETYPE.desc()),
    PACKETTHREADUTIL$CHECKTHREADANDENQUEUE(INVOKESTATIC, PACKETTHREADUTIL, "a", "checkThreadAndEnqueue", "(" + PACKET.desc() + INETHANDLER.desc() + ITHREADLISTENER.desc() + ")V"),
    RENDER$RENDERLIVINGLABEL("a", "renderLivingLabel", "(" + ENTITY.desc() + "Ljava/lang/String;DDDI)V"),
    RENDER$SHOULDRENDER(INVOKEVIRTUAL, RENDER, "a", "shouldRender", "(" + ENTITY.desc() + ICAMERA.desc() + "DDD)Z"),
    RENDERERLIVINGENTITY$RENDERNAME("b", "renderName", "(" + ENTITYLIVINGBASE.desc() + "DDD)V"),
    RENDERERLIVINGENTITY$ROTATECORPSE("a", "rotateCorpse", "(" + ENTITYLIVINGBASE.desc() + "FFF)V"),
    RENDERERLIVINGENTITY$SETBRIGHTNESS("a", "setBrightness", "(" + ENTITYLIVINGBASE.desc() + "FZ)Z"),
    RENDERERLIVINGENTITY$SETSCORETEAMCOLOR("c", "setScoreTeamColor", "(" + ENTITYLIVINGBASE.desc() + ")Z"),
    RENDERGLOBAL$ISRENDERENTITYOUTLINES(INVOKEVIRTUAL, RENDERGLOBAL, "d", "isRenderEntityOutlines", "()Z"),
    RENDERGLOBAL$LOADRENDERER(INVOKEVIRTUAL, RENDERGLOBAL, "a", "loadRenderers", "()V"),
    RENDERGLOBAL$PLAYAUXSFX("a", "playAuxSFX", "(" + ENTITYPLAYER.desc() + "I" + BLOCKPOS.desc() + "I)V"),
    RENDERGLOBAL$RENDERENTITIES("a", "renderEntities", "(" + ENTITY.desc() + ICAMERA.desc() + "F)V"),
    RENDERMANAGER$ISDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "isDebugBoundingBox", "()Z"),
    RENDERMANAGER$RENDERDEBUGBOUNDINGBOX("b", "renderDebugBoundingBox", "(" + ENTITY.desc() + "DDDFF)V"),
    RENDERMANAGER$SETDEBUGBOUNDINGBOX(INVOKEVIRTUAL, RENDERMANAGER, "b", "setDebugBoundingBox", "(Z)V"),
    RENDERPLAYER$RENDEROFFSETLIVINGLABEL("a", "renderOffsetLivingLabel", "(" + ABSTRACTCLIENTPLAYER.desc() + "DDDLjava/lang/String;FD)V"),
    RENDGERGLOBAL$RENDERENTITYOUTLINEFRAMEBUFFER("c", "renderEntityOutlineFramebuffer", "()V"),
    SCORE$GETSCOREPOINTS(INVOKEVIRTUAL, SCORE, "c", "getScorePoints", "()I"),
    SCOREBOARD$GETOBJECTIVEINDISPLAYSLOT(INVOKEVIRTUAL, SCOREBOARD, "a", "getObjectiveInDisplaySlot", "(I)" + SCOREOBJECTIVE.desc()),
    SCOREOBJECTIVE$GETDISPLAYNAME(INVOKEVIRTUAL, SCOREOBJECTIVE, "d", "getDisplayName", "()Ljava/lang/String;"),
    SCOREPLAYERTEAM$FORMATPLAYERNAME(INVOKESTATIC, SCOREPLAYERTEAM, "a", "formatPlayerName", "(" + TEAM.desc() + "Ljava/lang/String;)Ljava/lang/String;"),
    STRINGBUILDER$APPEND_I(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$APPEND_STRING(INVOKEVIRTUAL, STRINGBUILDER, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
    STRINGBUILDER$TOSTRING(INVOKEVIRTUAL, STRINGBUILDER, "toString", "()Ljava/lang/String;"),
    TESSELLATOR$DRAW(INVOKEVIRTUAL, TESSELLATOR, "b", "draw", "()V"),
    TESSELLATOR$GETINSTANCE(INVOKESTATIC, TESSELLATOR, "a", "getInstance", "()" + TESSELLATOR.desc()),
    WORLDCLIENT$SENDBLOCKBREAKPROGRESS(INVOKEVIRTUAL, WORLDCLIENT, "c", "sendBlockBreakProgress", "(I" + BLOCKPOS.desc() + "I)V");

    private final int opcode;
    public final String owner;
    public final String name;
    public final String desc;

    MethodMapping(int opcode, String owner, String clearName, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = clearName;
        this.desc = desc;
    }

    MethodMapping(String name, String desc) {
        this(-1, (String) null, name, desc);
    }

    MethodMapping(String obfName, String clearName, String desc) {
        this(-1, (String) null, MWELoadingPlugin.isObf() ? obfName : clearName, desc);
    }

    MethodMapping(int opcode, ClassMapping owner, String clearName, String desc) {
        this(opcode, owner.name, clearName, desc);
    }

    MethodMapping(int opcode, ClassMapping owner, String obfName, String clearName, String desc) {
        this(opcode, owner.name, MWELoadingPlugin.isObf() ? obfName : clearName, desc);
    }

    @Override
    public int opcode() {
        if (opcode == -1) {
            throw new IllegalArgumentException("MethodMapping doesn't have opcode defined");
        }
        return opcode;
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public String methodName() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

}
