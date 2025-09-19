package fr.alexdoru.mwe.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

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
            sb.append(AQUA).append("General wallrun information for: " + YELLOW + map + "\n");
            switch (map) {
                case "Bloodmoon":
                    sb.append(WHITE).append("- ").append(AQUA).append("The walls are pretty good. Just watch out for broken wallruns\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is a wall above pre\n");
                    sb.append(DARK_BLUE).append("Wallruns you can't wall run on:\n");
                    sb.append(WHITE).append("- ").append(BLUE).append("Blue Back Wall\n");
                    sb.append(WHITE).append("- ").append(YELLOW).append("Yellow Opposite Spawn Wall\n");
                    break;

                case "Dragonkeep":
                    sb.append(WHITE).append("- ").append(AQUA).append("Back Wall is good for wallrunning. You can always jump down in the water if you need too\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The wall above prebuild can be good if you are able to build up in time. Usually people use this for surviving pre DM.\n");
                    sb.append(DARK_BLUE).append("Wallruns you can't wall run on:\n");
                    sb.append(WHITE).append("- ").append(RED).append("Red Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(RED).append("Red Opposite Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(BLUE).append("Blue Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(BLUE).append("Blue Opposite Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(GREEN).append("Green Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(GREEN).append("Green Opposite Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(YELLOW).append("Yellow Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(YELLOW).append("Yellow Opposite Spawn Wall\n");

                    break;

                case "Wonderland":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are pretty good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    sb.append(DARK_BLUE).append("Wallruns you can't wall run on:\n");
                    sb.append(WHITE).append("- ").append(RED).append("Red Spawn Wall\n");
                    break;

                case "Ebonveil":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are pretty good and really high\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    sb.append(DARK_BLUE).append("Wallruns you can't wall run on:\n");
                    sb.append(WHITE).append("- ").append(YELLOW).append("Yellow Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(YELLOW).append("Yellow Opposite Spawn Wall\n");
                    break;

                case "Mad Pixel":
                    sb.append(WHITE).append("- ").append(AQUA).append("A lot of these wall runs don't work. I wouldn't go up on a wall unless u see it works.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns aren't really good on this map. I would just go tunnel, mines or in castle.\n");
                    break;

                case "City":
                    sb.append(WHITE).append("- ").append(AQUA).append("All walls are really good to wallrun on.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Only thing I would look out for is the slabs/stairs on the lower parts of the wall.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Just make sure you go up early or just expect that you might mess up.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There are wallruns above pre\n");
                    break;

                case "Egypt":
                    sb.append(WHITE).append("- ").append(AQUA).append("All walls are really good to wallrun on.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would start wall running from Back Wall, Top of pre, Castle or just building up when you have a lot of space. \n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The bottom half of Spawn wall and Opposite spawn wall is hard/not worth it to wall run on.\n");
                    break;

                case "Anchored":
                    sb.append(WHITE).append("- ").append(AQUA).append("All walls are good to wallrun on\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Just watch your head on the lower parts of the wall when building up\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There are no wallruns above pre\n");
                    break;

                case "Aztec":
                    sb.append(WHITE).append("- ").append(AQUA).append("Spawn Wall and Back Wall are alright.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can switch bases easily from Spawn Wall or Back Wall to escape\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There are no wallruns above pre\n");
                    break;

                case "Barrage":
                    sb.append(WHITE).append("- ").append(AQUA).append("These wallruns are some of the best in the game\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can hit your head on barriers at the top of the wall to catch up to people or block people off\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Be careful when wall running in bases with a wither alive. Try going down before u reach the rocket to be safe.\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There are no wallruns above pre\n");
                    break;

                case "Duskforge":
                    sb.append(WHITE).append("- ").append(AQUA).append("These wallruns are all really good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Even with withers alive, you can wallrun around the map by using the castle\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There are no wallruns above pre\n");
                    break;

                case "Dynasty":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are pretty good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can hit your head on barriers at the top of the wall to catch up to people or block people off\n");
                    break;

                case "Forsaken":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are all pretty good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The bottom half of the walls have slabs and fences\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would start wall running from pre or from castle or start early when you have a lot of space\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is a wallrun above pre\n");
                    break;

                case "Goldforge":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are not used a lot on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can use Spawn Wall or Opposite Spawn Wall to get some space, but I wouldn't stay there\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Back Wall can be used, but I don't see it often at all\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The base switch is very hard on this map. If you mess up you can put yourself in a really bad spot.\n");
                    break;

                case "Imperial":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are alright on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Spawn Wall and Opposite Spawn Wall are decent and you can switch bases easily\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("Back Wall is hard to get to as you need to go around pillars to get there, unless you go up through castle\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    break;

                case "Kingdom":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are all horrible on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("If you are able to make it to the top of the wall you can wallrun on barriers up there\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The wall caves in and you can't place blocks there. You can get put in a bad spot while building up to the top\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can wall run above pre, but right outside of your base. Some people use this to live\n");
                    break;

                case "Kirobiro":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are decent on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would use them to get some space or escape but I wouldn't stay there\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can switch bases easily\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("A popular way to live pre DM or just survive is to build up a little past pre and wallrun inside mid\n");
                    break;

                case "Launchsite":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are all really good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can hit your head on barriers at the top of the wall to catch up to people or block people off\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    break;

                case "Oasis":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are pretty good\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The bottom half of the wall is just slabs and stuff\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would start wallrunning from castle or Back Wall\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can also start from pre if you have enough space\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    break;

                case "Serenity":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are pretty good on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The issue is that they are pretty low, but they are still good to go on\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can hit your head on barriers at the top of the wall to catch up to people or block people off\n");
                    break;

                case "Serpents":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are decent on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("The bottom half is hard to wallrun on, make sure you have enough space to build up or just try not to mess up a lot\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would run up castle and then start the wallrun if possible\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can wallrun above pre\n");
                    break;

                case "Shadowstone":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are some of the best in the game\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("For pre DM, you can wallrun around the map even with withers alive, just stop and run across the top of castle when you get to them\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("There is no wallrun above pre\n");
                    break;

                case "Shanty Bay":
                    sb.append(WHITE).append("- ").append(AQUA).append("The walls are all good on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("I would watch out on Spawn Wall, you can't wallrun above spawn until you are pretty high up\n");


                    break;

                case "Solace":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wall\n");
                    break;

                case "Steppes":
                    sb.append(WHITE).append("- ").append(AQUA).append("The only wall that is really viable on this map is Opposite Spawn Wall\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("It is usually used to wallrun down after running ontop of castle\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can wallrun above pre but there is only 1 block wide. No one usually does this\n");
                    break;

                case "Stonehold":
                    sb.append(WHITE).append("- ").append(AQUA).append("The wallruns are really good on this map\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("They are all super clean and they go up really high\n");
                    sb.append(WHITE).append("- ").append(AQUA).append("You can also wall run anywhere, Spawn Wall, Back Wall, Opposite Spawn Wall, Above Pre and Inside Mid\n");
                    break;

                default:
                    String defaultSb = AQUA + "Join a Mega Walls game to learn more about the wallruns for the map you are on!";
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
