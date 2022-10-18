package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandKill extends CommandBase {

    @Override
    public String getCommandName() {
        return "kill";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/kill";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/kill");
        if (ConfigHandler.showKillCooldownHUD && FKCounterMod.isInMwGame) {
            KillCooldownHUD.instance.drawCooldownHUD();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}
