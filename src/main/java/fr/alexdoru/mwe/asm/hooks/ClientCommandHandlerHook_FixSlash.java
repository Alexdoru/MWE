package fr.alexdoru.mwe.asm.hooks;

@SuppressWarnings("unused")
public class ClientCommandHandlerHook_FixSlash {

    public static boolean shouldCancel(String msg) {
        return !msg.trim().startsWith("/");
    }

}
