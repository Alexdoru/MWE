package fr.alexdoru.megawallsenhancementsmod.asm.mappings;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;

public enum ClassMapping {

    ABSTRACTCLIENTPLAYER("bet", "net/minecraft/client/entity/AbstractClientPlayer"),
    AXISALIGNEDBB("aug", "net/minecraft/util/AxisAlignedBB"),
    BLOCKPOS("cj", "net/minecraft/util/BlockPos"),
    CHATLINE("ava", "net/minecraft/client/gui/ChatLine"),
    CLICKEVENT("et", "net/minecraft/event/ClickEvent"),
    COLLECTION("java/util/Collection"),
    ENTITY("pk", "net/minecraft/entity/Entity"),
    ENTITYARROW("wq", "net/minecraft/entity/projectile/EntityArrow"),
    ENTITYFX("beb", "net/minecraft/client/particle/EntityFX"),
    ENTITYITEM("uz", "net/minecraft/entity/item/EntityItem"),
    ENTITYLIVINGBASE("pr", "net/minecraft/entity/EntityLivingBase"),
    ENTITYPLAYER("wn", "net/minecraft/entity/player/EntityPlayer"),
    ENTITYPLAYERSP("bew", "net/minecraft/client/entity/EntityPlayerSP"),
    ENUMCHATFORMATTING("a", "net/minecraft/util/EnumChatFormatting"),
    FONTRENDERER("avn", "net/minecraft/client/gui/FontRenderer"),
    GAMEPROFILE("com/mojang/authlib/GameProfile"),
    GAMESETTINGS("avh", "net/minecraft/client/settings/GameSettings"),
    GUICONTAINER("ayl", "net/minecraft/client/gui/inventory/GuiContainer"),
    GUIINGAME("avo", "net/minecraft/client/gui/GuiIngame"),
    GUIINGAMEFORGE("net/minecraftforge/client/GuiIngameForge"),
    GUINEWCHAT("avt", "net/minecraft/client/gui/GuiNewChat"),
    GUISCREEN("axu", "net/minecraft/client/gui/GuiScreen"),
    GUISCREENBOOK("ayo", "net/minecraft/client/gui/GuiScreenBook"),
    ICAMERA("bia", "net/minecraft/client/renderer/culling/ICamera"),
    ICHATCOMPONENT("eu", "net/minecraft/util/IChatComponent"),
    ICOMMANDSENDER("m", "net/minecraft/command/ICommandSender"),
    INVENTORYPLAYER("wm", "net/minecraft/entity/player/InventoryPlayer"),
    ITEMSTACK("zx", "net/minecraft/item/ItemStack"),
    MAP("java/util/Map"),
    MINECRAFT("ave", "net/minecraft/client/Minecraft"),
    NETHANDLERPLAYCLIENT("bcy", "net/minecraft/client/network/NetHandlerPlayClient"),
    NETWORKMANAGER("ek", "net/minecraft/network/NetworkManager"),
    NETWORKPLAYERINFO("bdc", "net/minecraft/client/network/NetworkPlayerInfo"),
    POTION("pe", "net/minecraft/potion/Potion"),
    RENDERGLOBAL("bfr", "net/minecraft/client/renderer/RenderGlobal"),
    RENDERITEM("bjh", "net/minecraft/client/renderer/entity/RenderItem"),
    RENDERMANAGER("biu", "net/minecraft/client/renderer/entity/RenderManager"),
    S38PACKETPLAYERLISTITEM("gz", "net/minecraft/network/play/server/S38PacketPlayerListItem"),
    S38PACKETPLAYERLISTITEM$ADDPLAYERDATA("gz$b", "net/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData"),
    SCALEDRESOLUTION("avr", "net/minecraft/client/gui/ScaledResolution"),
    SCOREBOARD("auo", "net/minecraft/scoreboard/Scoreboard"),
    SCOREOBJECTIVE("auk", "net/minecraft/scoreboard/ScoreObjective"),
    SCOREPLAYERTEAM("aul", "net/minecraft/scoreboard/ScorePlayerTeam"),
    SLOT("yg", "net/minecraft/inventory/Slot"),
    STRINGBUILDER("java/lang/StringBuilder"),
    TEAM("auq", "net/minecraft/scoreboard/Team"),
    TEXTUREMANAGER("bmj", "net/minecraft/client/renderer/texture/TextureManager"),
    WORLDRENDERER("bfd", "net/minecraft/client/renderer/WorldRenderer");

    public final String name;

    ClassMapping(String clearName) {
        this.name = clearName;
    }

    ClassMapping(String obfName, String clearName) {
        this.name = ASMLoadingPlugin.isObf ? obfName : clearName;
    }

    @Override
    public String toString() {
        return name;
    }

}
