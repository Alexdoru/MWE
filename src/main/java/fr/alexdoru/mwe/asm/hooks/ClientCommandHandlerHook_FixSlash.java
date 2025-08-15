package fr.alexdoru.mwe.asm.hooks;

public class ClientCommandHandlerHook_FixSlash {

    public static boolean shouldCancel(String msg) {
        return !msg.trim().startsWith("/");
    }

}
