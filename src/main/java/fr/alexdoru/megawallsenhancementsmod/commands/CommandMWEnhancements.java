package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.gui.GeneralConfigGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

public class CommandMWEnhancements extends CommandBase {

    @Override
    public String getCommandName() {
        return "mwenhancements";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mwenhancements";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        new DelayedTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GeneralConfigGuiScreen()), 1);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("megawallsenhancements");
    }

}
