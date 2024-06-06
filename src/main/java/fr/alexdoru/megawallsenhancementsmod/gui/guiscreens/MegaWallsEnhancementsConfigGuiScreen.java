package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.LeatherArmorManager;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.OptionGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.TextElement;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

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
        this.elementList.add(new TextElement(GREEN + "Mega Walls Enhancements", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        final List<String> repetitiveMsgTooltip = new ArrayList<>();
        repetitiveMsgTooltip.add(GREEN + "Hide repetitive MW msg");
        repetitiveMsgTooltip.add("");
        repetitiveMsgTooltip.add(GRAY + "Hides the following messages in mega walls :");
        repetitiveMsgTooltip.add("");
        repetitiveMsgTooltip.add(RED + "Get to the center to stop the hunger");
        repetitiveMsgTooltip.add(GREEN + "You broke your protected chest");
        repetitiveMsgTooltip.add(GREEN + "You broke your protected trapped chest");
        repetitiveMsgTooltip.add(YELLOW + "Your Salvaging skill returned your arrow to you!");
        repetitiveMsgTooltip.add(YELLOW + "Your Efficiency skill got you an extra drop!");
        repetitiveMsgTooltip.add(GREEN + "Your " + AQUA + "Ability name " + GREEN + "skill is ready!");
        repetitiveMsgTooltip.add(GREEN + "Click your sword or bow to activate your skill!");
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
                GRAY + "Hide the hunger message that appears in the middle of the screen in deathmatch"));
        final List<String> iconsTooltip = new ArrayList<>();
        iconsTooltip.add(GREEN + "Squad Icon on names");
        iconsTooltip.add("");
        iconsTooltip.add(DARK_GRAY + "▪ " + GREEN + "Enabled" + GRAY + " : displays a squad icon in front of names on nametags and in the tablist");
        iconsTooltip.add("");
        iconsTooltip.add(DARK_GRAY + "▪ " + GREEN + "Tab Only" + GRAY + " : displays a squad icon in front of names in the tablist only");
        iconsTooltip.add("");
        iconsTooltip.add(NameUtil.SQUAD_ICON + GRAY + ": players in your squad");
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(3),
                () -> "Squad Icon on names : " + (ConfigHandler.squadIconTabOnly ? GREEN + "Tab Only" : getSuffix(ConfigHandler.squadIconOnNames)),
                () -> {
                    if (ConfigHandler.squadIconOnNames && !ConfigHandler.squadIconTabOnly) {
                        ConfigHandler.squadIconOnNames = false;
                        ConfigHandler.squadIconTabOnly = true;
                    } else if (ConfigHandler.squadIconTabOnly) {
                        ConfigHandler.squadIconTabOnly = false;
                    } else {
                        ConfigHandler.squadIconOnNames = true;
                    }
                    NameUtil.refreshAllNamesInWorld();
                },
                iconsTooltip));
        this.buttonList.add(new OptionGuiButton(
                xPosLeft, getButtonYPos(4),
                "More strength particules",
                (b) -> ConfigHandler.strengthParticules = b,
                () -> ConfigHandler.strengthParticules,
                GRAY + "Spawns angry villager particles when an herobrine or dreadlord gets strength from a final kill"));
        this.buttonList.add(new FancyGuiButton(
                xPosLeft, getButtonYPos(5),
                () -> "Colored Leather Armor : " + getSuffix(ConfigHandler.coloredLeatherArmor),
                () -> {
                    ConfigHandler.coloredLeatherArmor = !ConfigHandler.coloredLeatherArmor;
                    LeatherArmorManager.onSettingChange();
                },
                GREEN + "Colored Leather Armor",
                GRAY + "Changes iron armor worn by other players to colored leather armor matching their team color in Mega Walls"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(1),
                "Renegade arrow count",
                (b) -> ConfigHandler.renegadeArrowCount = b,
                () -> ConfigHandler.renegadeArrowCount,
                GRAY + "Renders above player heads the amount of arrows pinned in each player when playing renegade"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(2),
                "Nick hider",
                (b) -> ConfigHandler.nickHider = b,
                () -> ConfigHandler.nickHider,
                GRAY + "Shows your real name instead of your nick when forming the squad in Mega Walls"));
        this.buttonList.add(new FancyGuiButton(
                xPosRight, getButtonYPos(3),
                () -> "Pink squadmates : " + getSuffix(ConfigHandler.pinkSquadmates),
                () -> {
                    ConfigHandler.pinkSquadmates = !ConfigHandler.pinkSquadmates;
                    NameUtil.refreshAllNamesInWorld();
                },
                GREEN + "Pink squadmates",
                GRAY + "Your squadmates will have a pink nametag, hitbox and hitcolor"));
        this.buttonList.add(new OptionGuiButton(
                xPosRight, getButtonYPos(4),
                "Keep first letter squadname",
                (b) -> ConfigHandler.keepFirstLetterSquadnames = b,
                () -> ConfigHandler.keepFirstLetterSquadnames,
                GRAY + "When adding a player to the squad with a custom name of your choice, using"
                        + YELLOW + " /squad add <name> as <custom name>"
                        + GRAY + ", it will keep the first letter of their real name so that you can track them on the compass"));
        if (!ASMLoadingPlugin.isFeatherLoaded()) {
            final List<String> chatHeadTooltip = new ArrayList<>();
            chatHeadTooltip.add(GREEN + "Chat Heads");
            chatHeadTooltip.add("");
            chatHeadTooltip.add(GRAY + "Renders heads of players in front of chat messages");
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
