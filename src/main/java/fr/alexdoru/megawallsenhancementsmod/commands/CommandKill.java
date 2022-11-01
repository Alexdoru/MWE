package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.KillCooldownHUD;
import net.minecraft.command.ICommandSender;

public class CommandKill extends MyAbstractCommand {

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
        sendChatMessage("/kill");
        if (ConfigHandler.showKillCooldownHUD && FKCounterMod.isInMwGame) {
            KillCooldownHUD.instance.drawCooldownHUD();
        }
    }

}
