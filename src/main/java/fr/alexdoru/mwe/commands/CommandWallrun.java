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

            StringBuilder sb = new StringBuilder();
            sb.append(AQUA).append("Wallruns you can't wall run on:\n");

            switch (map) {
                case "Bloodmoon":
                    sb.append(RED).append("❌ ").append(BLUE).append("Blue Back Wall\n");
                    sb.append(RED).append("❌ ").append(YELLOW).append("Yellow Opposite Spawn Wall\n");
                    break;

                case "Dragonkeep":
                    sb.append(RED).append("❌ ").append(RED).append("Red Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(RED).append("Red Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLUE).append("Blue Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLUE).append("Blue Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(GREEN).append("Green Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(GREEN).append("Green Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLACK).append("Yellow Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLACK).append("Yellow Opposite Spawn Wall\n");

                    break;

                case "Wonderland":
                    sb.append(RED).append("❌ ").append(RED).append("Red Spawn Wall\n");
                    break;

                case "Ebonveil":
                    sb.append(RED).append("❌ ").append(YELLOW).append("Yellow Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(YELLOW).append("Yellow Opposite Spawn Wall\n");
                    break;

                case "Mad Pixel":
                    sb.append(RED).append("❌ ").append(RED).append("Red Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(RED).append("Red Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLUE).append("Blue Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLUE).append("Blue Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(GREEN).append("Green Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(GREEN).append("Green Opposite Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLACK).append("Yellow Spawn Wall\n");
                    sb.append(RED).append("❌ ").append(BLACK).append("Yellow Opposite Spawn Wall\n");
                    break;

                default:
                    String defaultSb = AQUA + "There are no broken wallruns!";
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(defaultSb));
                    return; // stop so we don’t also print the original sb
            }

            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(sb.toString()));

        } catch (Exception e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(RED + "Failed to parse /locraw response."));
            e.printStackTrace();
        }
    }
}
