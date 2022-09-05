package fr.alexdoru.megawallsenhancementsmod.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommandAddAlias extends CommandBase {

    private static final HashMap<String, String> renamingMap = new HashMap<>();
    private static File alliasDataFile;

    public CommandAddAlias() {
        alliasDataFile = new File((Minecraft.getMinecraft()).mcDataDir, "config/alliasData.json");
        readDataFromFile();
        Runtime.getRuntime().addShutdownHook(new Thread(CommandAddAlias::writeDataToFile));
    }

    @Override
    public String getCommandName() {
        return "addalias";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/addalias";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equals("clearall")) {
            renamingMap.clear();
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cleared alias for all players."));
            return;
        } else if (args.length == 1 && args[0].equals("list")) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "In this lobby :\n"));
            for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                final String alias = renamingMap.get(networkPlayerInfo.getGameProfile().getName());
                if (alias != null) {
                    ChatUtil.addChatMessage(new ChatComponentText(NameUtil.getFormattedName(networkPlayerInfo.getGameProfile().getName()) + EnumChatFormatting.RESET + " (" + alias + ")"));
                }
            }
            return;
        }
        if (args.length != 2) {
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : /addalias <playername> <alias>"));
            return;
        }
        if (args[0].equals("remove")) {
            renamingMap.remove(args[1]);
            NameUtil.updateGameProfileAndName(args[1], true);
            ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Removed alias for " + EnumChatFormatting.GOLD + args[1]));
            return;
        }
        renamingMap.put(args[0], args[1]);
        NameUtil.updateGameProfileAndName(args[0], true);
        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Added alias for " + EnumChatFormatting.GOLD + args[0] + EnumChatFormatting.GREEN + " : " + EnumChatFormatting.GOLD + args[1]));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final List<String> onlinePlayersByName = TabCompletionUtil.getOnlinePlayersByName();
        onlinePlayersByName.addAll(Arrays.asList("clearall", "list", "remove"));
        return getListOfStringsMatchingLastWord(args, onlinePlayersByName);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ad");
    }

    public static HashMap<String, String> getMap() {
        return renamingMap;
    }

    private static void readDataFromFile() {
        if (!alliasDataFile.exists()) {
            return;
        }
        try {
            final Gson gson = new Gson();
            final HashMap<String, String> hashMap = gson.fromJson(new FileReader(alliasDataFile), new TypeToken<HashMap<String, String>>() {}.getType());
            if (hashMap != null) {
                renamingMap.putAll(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDataToFile() {
        try {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(renamingMap);
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(alliasDataFile));
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
