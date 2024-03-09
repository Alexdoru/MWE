package fr.alexdoru.megawallsenhancementsmod.chat;

import net.minecraft.util.ChatComponentText;

public class FlagChatComponent extends ChatComponentText {

    private final String flagKey;

    public FlagChatComponent(String flagkey, String msg) {
        super(msg);
        this.flagKey = flagkey;
    }

    public String getFlagKey() {
        return flagKey;
    }

}
