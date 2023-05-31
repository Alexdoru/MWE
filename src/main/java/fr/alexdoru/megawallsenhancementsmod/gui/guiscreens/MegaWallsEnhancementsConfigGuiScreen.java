package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class MegaWallsEnhancementsConfigGuiScreen extends MyGuiScreen {

    private final GuiScreen parent;

    public MegaWallsEnhancementsConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.maxWidth = BUTTON_WIDTH;
        this.maxHeight = (buttonsHeight + 4) * 8 + buttonsHeight;
        super.initGui();
        final int xPosLeft = getxCenter() - BUTTON_WIDTH - 10;
        final int xPosRight = getxCenter() + 10;
        this.elementList.add(new TextElement(EnumChatFormatting.GREEN + "Mega Walls Enhancements", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        final List<String> repetitiveMsgTooltip = new ArrayList<>();
        repetitiveMsgTooltip.add(EnumChatFormatting.GREEN + "Hide repetitive MW msg");
        repetitiveMsgTooltip.add("");
        repetitiveMsgTooltip.add(EnumChatFormatting.GRAY + "Hides the following messages in mega walls :");
        repetitiveMsgTooltip.add("");
        repetitiveMsgTooltip.add(EnumChatFormatting.RED + "Get to the middle to stop the hunger!");
        repetitiveMsgTooltip.add(EnumChatFormatting.GREEN + "You broke your protected chest");
        repetitiveMsgTooltip.add(EnumChatFormatting.GREEN + "You broke your protected trapped chest");
        repetitiveMsgTooltip.add(EnumChatFormatting.YELLOW + "Your Salvaging skill returned your arrow to you!");
        repetitiveMsgTooltip.add(EnumChatFormatting.YELLOW + "Your Efficiency skill got you an extra drop!");
        repetitiveMsgTooltip.add(EnumChatFormatting.GREEN + "Your " + EnumChatFormatting.AQUA + "Ability name " + EnumChatFormatting.GREEN + "skill is ready!");
        repetitiveMsgTooltip.add(EnumChatFormatting.GREEN + "Click your sword or bow to activate your skill!");
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(1),
                "Hide repetitive MW msg",
                (b) -> ConfigHandler.hideRepetitiveMWChatMsg = b,
                () -> ConfigHandler.hideRepetitiveMWChatMsg,
                repetitiveMsgTooltip));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(2),
                "Hide hunger title",
                (b) -> ConfigHandler.hideHungerTitleInMW = b,
                () -> ConfigHandler.hideHungerTitleInMW,
                EnumChatFormatting.GRAY + "Hide the hunger message that appears in the middle of the screen in deathmatch"));
        final List<String> iconsTooltip = new ArrayList<>();
        iconsTooltip.add(EnumChatFormatting.GREEN + "Icons on names");
        iconsTooltip.add("");
        iconsTooltip.add(EnumChatFormatting.GRAY + "Toggles all icons in front of names, on nametags and in the tablist");
        iconsTooltip.add("");
        iconsTooltip.add(NameUtil.squadprefix + EnumChatFormatting.GRAY + " : players in your squad");
        iconsTooltip.add(NameUtil.prefix_bhop + EnumChatFormatting.GRAY + " : players reported for blatant cheats");
        iconsTooltip.add(NameUtil.prefix + EnumChatFormatting.GRAY + " : players reported for other cheats");
        iconsTooltip.add(NameUtil.prefix_scan + EnumChatFormatting.GRAY + " : players flagged by the /scangame command");
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(3),
                () -> "Icons on names : " + getSuffix(ConfigHandler.iconsOnNames),
                () -> {
                    ConfigHandler.iconsOnNames = !ConfigHandler.iconsOnNames;
                    NameUtil.refreshAllNamesInWorld();
                },
                iconsTooltip));
        final List<String> prestige5Tooltip = new ArrayList<>();
        prestige5Tooltip.add(EnumChatFormatting.GREEN + "Prestige 5 tags");
        prestige5Tooltip.add("");
        prestige5Tooltip.add(EnumChatFormatting.GRAY + "Adds the prestige V colored tags in mega walls.");
        prestige5Tooltip.add("");
        prestige5Tooltip.add(EnumChatFormatting.GRAY + "You need at least," + EnumChatFormatting.GOLD + " 10 000 classpoints" + EnumChatFormatting.GRAY + ", and a "
                + EnumChatFormatting.RED + "working API Key" + EnumChatFormatting.GRAY + ". This will send api requests and store the data in a cache until you close your game.");
        prestige5Tooltip.add(EnumChatFormatting.GRAY + "Type " + EnumChatFormatting.YELLOW + "/mwenhancements clearcache" + EnumChatFormatting.GRAY + " to force update the data.");
        prestige5Tooltip.add("");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "Prestige Colors :");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "10000 classpoints : " + EnumChatFormatting.DARK_PURPLE + "[TAG]");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "13000 classpoints : " + EnumChatFormatting.DARK_BLUE + "[TAG]");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "19000 classpoints : " + EnumChatFormatting.DARK_AQUA + "[TAG]");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "28000 classpoints : " + EnumChatFormatting.DARK_GREEN + "[TAG]");
        prestige5Tooltip.add(EnumChatFormatting.GOLD + "40000 classpoints : " + EnumChatFormatting.DARK_RED + "[TAG]");
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(4),
                () -> "Prestige 5 tags : " + getSuffix(ConfigHandler.prestigeV),
                () -> {
                    if (ConfigHandler.prestigeV) {
                        ConfigHandler.prestigeV = false;
                        NameUtil.refreshAllNamesInWorld();
                    } else {
                        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                            ChatUtil.printApikeySetupInfo();
                        } else {
                            ConfigHandler.prestigeV = true;
                            NameUtil.refreshAllNamesInWorld();
                        }
                    }
                },
                prestige5Tooltip));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(5),
                "More strength particules",
                (b) -> ConfigHandler.strengthParticules = b,
                () -> ConfigHandler.strengthParticules,
                EnumChatFormatting.GRAY + "Spawns angry villager particles when an herobrine or dreadlord gets strength from a final kill"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(1),
                "Renegade arrow count",
                (b) -> ConfigHandler.renegadeArrowCount = b,
                () -> ConfigHandler.renegadeArrowCount,
                EnumChatFormatting.GRAY + "Renders above player heads the amount of arrows pinned in each player when playing renegade",
                EnumChatFormatting.YELLOW + "This can have a negative impact on performance, keep it off if you don't play Renegade"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(2),
                "Nick hider",
                (b) -> ConfigHandler.nickHider = b,
                () -> ConfigHandler.nickHider,
                EnumChatFormatting.GRAY + "Shows your real name instead of your nick when forming the squad in Mega Walls"));
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(3),
                () -> "Pink squadmates : " + getSuffix(ConfigHandler.pinkSquadmates),
                () -> {
                    ConfigHandler.pinkSquadmates = !ConfigHandler.pinkSquadmates;
                    NameUtil.refreshAllNamesInWorld();
                },
                EnumChatFormatting.GREEN + "Pink squadmates",
                EnumChatFormatting.GRAY + "Your squadmates will have a pink nametag, hitbox and hitcolor"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(4),
                "Keep first letter squadname",
                (b) -> ConfigHandler.keepFirstLetterSquadnames = b,
                () -> ConfigHandler.keepFirstLetterSquadnames,
                EnumChatFormatting.GRAY + "When adding a player to the squad with a custom name of your choice, using"
                        + EnumChatFormatting.YELLOW + " /squad add <name> as <custom name>"
                        + EnumChatFormatting.GRAY + ", it will keep the first letter of their real name so that you can track them on the compass"));
        if (!ASMLoadingPlugin.isFeatherLoaded()) {
            final List<String> chatHeadTooltip = new ArrayList<>();
            chatHeadTooltip.add(EnumChatFormatting.GREEN + "Chat Heads");
            chatHeadTooltip.add("");
            chatHeadTooltip.add(EnumChatFormatting.GRAY + "Renders heads of players in front of chat messages");
            if (ASMLoadingPlugin.isPatcherLoaded()) {
                chatHeadTooltip.add("");
                chatHeadTooltip.add(EnumChatFormatting.YELLOW + "If you are using HUD Caching from Patcher, the heads might not fade away properly");
            }
            this.buttonList.add(new FancyGuiButton(
                    xPosRight, getButtonYPos(5),
                    () -> "Chat Heads : " + getSuffix(ConfigHandler.chatHeads),
                    () -> {
                        ConfigHandler.chatHeads = !ConfigHandler.chatHeads;
                        mc.ingameGUI.getChatGUI().refreshChat();
                    },
                    chatHeadTooltip));
        }
        this.buttonList.add(new SimpleGuiButton(getxCenter() - 150 / 2, getButtonYPos(7), 150, buttonsHeight, "Done", () -> mc.displayGuiScreen(this.parent)));
    }

}
