package fr.alexdoru.mwe.asm.hooks.mc.other;

public class ClientCommandHandlerHook_FixSlash {

    public static boolean shouldCancel(String msg) {
        return !msg.trim().startsWith("/");
    }

}
