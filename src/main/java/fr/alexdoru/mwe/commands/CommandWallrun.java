package fr.alexdoru.mwe.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class CommandWallrun extends MyAbstractCommand {

    @Override
    public String getCommandName() {
        return "wallrun";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/wallrun";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Collections.emptyList();
    }

    public static void handleLocrawResponse(String jsonMessage) {
        try {
            JsonObject json = new JsonParser().parse(jsonMessage).getAsJsonObject();
            String map = json.has("map") ? json.get("map").getAsString() : "Unknown";

            String msg;
            switch (map) {
                case "Bloodmoon":
                    msg = GREEN + "You can't wallrun on " + BLUE + "Blue Back Wall " + GREEN + "and " + YELLOW + "Yellow Opposite Spawn Wall";
                    break;
                case "Dragonkeep":
                    msg = GREEN + "You can't wallrun on " + BLACK + "All Spawn Walls " + GREEN + "and " + BLACK + "All Opposite Spawn Walls";
                    break;
                case "Wonderland":
                    msg = GREEN + "You can't wallrun on " + RED + "Red Spawn Wall";
                    break;
                case "Ebonveil":
                    msg = GREEN + "You can't wallrun on " + YELLOW + "Yellow Spawn Wall " + GREEN + "and " + YELLOW + "Yellow Opposite Spawn Wall";
                    break;
                case "Mad Pixel":
                    msg = BLACK + "Ikiab all these wallruns are chopped idk which ones work";
                    break;
                default:
                    msg = YELLOW + map + GREEN + "has no broken wallruns";
                    break;
            }

            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(msg));

        } catch (Exception e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(RED + "Failed to parse /locraw response."));
            e.printStackTrace();
        }
    }
}
