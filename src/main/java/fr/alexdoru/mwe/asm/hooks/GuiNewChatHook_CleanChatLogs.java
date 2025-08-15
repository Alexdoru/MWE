package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.StringUtil;

public class GuiNewChatHook_CleanChatLogs {

    public static String removeFormatting(String s) {
        return MWEConfig.cleanChatLogs ? StringUtil.removeFormattingCodes(s) : s;
    }

}
