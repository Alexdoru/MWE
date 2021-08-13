package fr.alexdoru.fkcountermod.utils;

import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;

public class MinecraftUtils {
    /* Regex made by Sk1er */
    private static final Pattern HYPIXEL_PATTERN = Pattern.compile(
            "^(?:(?:(?:.+\\.)?hypixel\\.net)|(?:209\\.222\\.115\\.\\d{1,3})|(?:99\\.198\\.123\\.[123]?\\d?))\\.?(?::\\d{1,5}\\.?)?$",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isHypixel() {
        FMLClientHandler instance = FMLClientHandler.instance();

        if (instance == null) {
            return false;
        }

        Minecraft client = instance.getClient();

        if (client == null) {
            return false;
        }

        ServerData currentServerData = client.getCurrentServerData();
        return (currentServerData != null && HYPIXEL_PATTERN.matcher(currentServerData.serverIP).find());
    }
}

