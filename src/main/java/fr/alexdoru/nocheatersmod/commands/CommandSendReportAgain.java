package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Date;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;

public class CommandSendReportAgain extends CommandBase {

    @Override
    public String getCommandName() {
        return "sendreportagain";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sendreportagain <UUID> <playerName>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 2) {
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }

        // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc
        String uuid = args[0];
        String playername = args[1];
        WDR wdr = WdredPlayers.getWdredMap().get(uuid);
        StringBuilder cheatmessage = new StringBuilder();
        ArrayList<String> constructCheats = new ArrayList<>();

        if (wdr.hacks.get(0).charAt(0) == '-') { // if timestamped report

            int j = 0;

            for (int i = 0; i < wdr.hacks.size(); i++) {
                if (wdr.hacks.get(i).charAt(0) == '-') {
                    j = i;
                } else if (i > j + 3) { // cheats
                    constructCheats.add(wdr.hacks.get(i));
                }
            }

        } else {
            constructCheats.addAll(wdr.hacks);
        }

        ArrayList<String> arrayCheats = WdredPlayers.removeDuplicates(constructCheats); // Removes the duplicate cheats, in timestamped reports for instance

        for (String hack : arrayCheats) { // verify unaccepted terms and replaces them

            if (hack.equals("keepsprint") || hack.equals("noslowdown")) {
                cheatmessage.append(" ").append("velocity");
            } else if (hack.equals("autoblock")) {
                cheatmessage.append(" ").append("aura");
            } else if (hack.equals("bhop")) {
                cheatmessage.append(" bhop aura reach velocity speed antiknockback");
            } else if (!(hack.equals("cheating") || hack.equals("fastbreak") || hack.contains("stalk") || hack.equals("nick"))) {
                for (String offhack : CommandReport.recognizedcheats) {
                    if (hack.equals(offhack)) {
                        cheatmessage.append(" ").append(hack);
                    }
                }

            }

        }

        String message = "/wdr " + playername + cheatmessage;
        (Minecraft.getMinecraft()).thePlayer.sendChatMessage(message);
        long timestamp = (new Date()).getTime();
        WdredPlayers.getWdredMap().put(uuid, new WDR(timestamp, wdr.hacks));

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

}
