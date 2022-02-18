package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

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
    public List<String> getCommandAliases() {
        return Collections.singletonList("Kill");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        (Minecraft.getMinecraft()).thePlayer.sendChatMessage("/kill");
        if (ConfigHandler.show_killcooldownHUD && FKCounterMod.isInMwGame) {
            KillCooldownGui.drawCooldownGui();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}
