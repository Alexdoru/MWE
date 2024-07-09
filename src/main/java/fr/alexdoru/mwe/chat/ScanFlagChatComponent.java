package fr.alexdoru.mwe.chat;

import net.minecraft.util.ChatComponentText;

public class ScanFlagChatComponent extends ChatComponentText {

    private final String key;

    public ScanFlagChatComponent(String key, String msg) {
        super(msg);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
