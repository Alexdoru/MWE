package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class GuiNewChatHook_CleanChatLogs {

    public static String removeFormatting(String s) {
        return ConfigHandler.cleanChatLogs ? EnumChatFormatting.getTextWithoutFormattingCodes(s) : s;
    }

}
