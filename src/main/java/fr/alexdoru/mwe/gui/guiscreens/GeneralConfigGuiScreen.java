package fr.alexdoru.mwe.gui.guiscreens;

import fr.alexdoru.mwe.asm.hooks.RendererLivingEntityHook_AprilFun;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.gui.elements.OptionGuiButton;
import fr.alexdoru.mwe.gui.elements.SimpleGuiButton;
import fr.alexdoru.mwe.gui.elements.TextElement;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.minecraft.util.EnumChatFormatting.*;

public class GeneralConfigGuiScreen extends MyGuiScreen {

    @Override
    public void initGui() {
        final int buttonsWidth = 150;
        this.maxWidth = buttonsWidth + (buttonsWidth + 10) * 2;
        this.maxHeight = (buttonsHeight + 4) * 10 + buttonsHeight;
        super.initGui();
        final int xPos = this.getxCenter() - buttonsWidth / 2;
        this.elementList.add(new TextElement(GREEN + "Config", getxCenter(), getButtonYPos(-1)).setSize(2).makeCentered());
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(1), buttonsWidth, buttonsHeight, AQUA + "Final Kill Counter", () -> mc.displayGuiScreen(new FKConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(2), buttonsWidth, buttonsHeight, GREEN + "Mega Walls Enhancements", () -> mc.displayGuiScreen(new MegaWallsConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(3), buttonsWidth, buttonsHeight, GOLD + "Vanilla Enhancements", () -> mc.displayGuiScreen(new VanillaConfigGuiScreen(this))));
        this.buttonList.add(new OptionGuiButton(
                getxCenter() + buttonsWidth / 2 + 10, getButtonYPos(2),
                buttonsWidth, buttonsHeight,
                "Automatic Updates",
                (b) -> ConfigHandler.automaticUpdate = b,
                () -> ConfigHandler.automaticUpdate,
                GRAY + "When your game starts, it will check and notify you if an update is available. If this setting is turned " +
                        GREEN + "ON" + GRAY + " it will also download the updated mod and install it in your mods folder when closing your game.",
                YELLOW + "The automatic installation doesn't work with " + RED + "Feather client" + YELLOW + ", you have to manually download the updated mod and place it in your mods folder."));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(4), buttonsWidth, buttonsHeight, DARK_PURPLE + "HUDs", () -> mc.displayGuiScreen(new HUDsConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(5), buttonsWidth, buttonsHeight, RED + "NoCheaters", () -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(6), buttonsWidth, buttonsHeight, DARK_RED + "Hacker Detector", () -> mc.displayGuiScreen(new HackerDetectorConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(7), buttonsWidth, buttonsHeight, BLUE + "Hitboxes, better F3+b", () -> mc.displayGuiScreen(new HitboxConfigGuiScreen(this))));
        this.buttonList.add(new SimpleGuiButton(xPos, getButtonYPos(9), buttonsWidth, buttonsHeight, "Done", () -> mc.displayGuiScreen(null)));
        if ("01/04".equals(new SimpleDateFormat("dd/MM").format(new Date().getTime()))) {
            this.buttonList.add(new OptionGuiButton(
                    getxCenter() + buttonsWidth / 2 + 10,
                    getButtonYPos(4),
                    130, buttonsHeight,
                    "April fun ",
                    (b) -> RendererLivingEntityHook_AprilFun.active = b,
                    () -> RendererLivingEntityHook_AprilFun.active,
                    GRAY + "haha got u"));
        }
    }

}
