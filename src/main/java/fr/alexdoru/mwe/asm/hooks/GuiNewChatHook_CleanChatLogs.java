package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class GuiNewChatHook_CleanChatLogs {

    public static String removeFormatting(String s) {
        return MWEConfig.cleanChatLogs ? EnumChatFormatting.getTextWithoutFormattingCodes(s) : s;
    }

}
