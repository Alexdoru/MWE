package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

@SuppressWarnings("unused")
public class CommandHandlerHook {
    public static String putToLowerCase(String s) {
        if (s != null) {
            return s.toLowerCase();
        }
        return null;
    }
}
