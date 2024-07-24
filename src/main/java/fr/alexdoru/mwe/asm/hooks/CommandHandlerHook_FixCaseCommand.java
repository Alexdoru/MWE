package fr.alexdoru.mwe.asm.hooks;

import java.util.Locale;

@SuppressWarnings("unused")
public class CommandHandlerHook_FixCaseCommand {

    public static String[] putToLowerCase(String[] split) {
        split[0] = putToLowerCase(split[0]);
        return split;
    }

    public static String putToLowerCase(String s) {
        if (s != null) {
            return s.toLowerCase(Locale.ENGLISH);
        }
        return null;
    }

}
