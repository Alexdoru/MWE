package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;

@SuppressWarnings("unused")
public class GuiNewChatHook_LongerChat {

    public static int getLongerChatSize(int original) {
        return original == 100 && MWEConfig.longerChat ? 32000 : original;
    }

}
