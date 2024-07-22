package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;

@SuppressWarnings("unused")
public class GuiNewChatHook_LongerChat {

    public static int getLongerChatSize(int original) {
        return original == 100 && ConfigHandler.longerChat ? 32000 : original;
    }

}
