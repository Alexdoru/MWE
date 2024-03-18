package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class WDR implements Comparable<WDR> {

    public long time;
    public final ArrayList<String> hacks;

    public WDR(long time, ArrayList<String> hacks) {
        this.time = time;
        this.hacks = hacks;
        this.hacks.trimToSize();
    }

    /**
     * Compares the timestamp
     */
    @Override
    public int compareTo(@Nonnull WDR wdr) {
        return Long.compare(this.time, wdr.time);
    }

    public boolean shouldPutRedIcon() {
        for (final String s : this.hacks) {
            if (isRedHack(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValidCheats() {
        for (final String cheat : hacks) {
            if (CommandReport.cheatsList.contains(cheat)) {
                return true;
            }
        }
        return false;
    }

    public String hacksToString() {
        final StringBuilder cheats = new StringBuilder();
        for (final String hack : hacks) {
            cheats.append(" ").append(hack);
        }
        return cheats.toString();
    }

    public IChatComponent getFormattedHacks() {
        final IChatComponent allCheats = new ChatComponentText("");
        for (final String hack : this.hacks) {
            if (isRedHack(hack)) {
                allCheats.appendText(" " + EnumChatFormatting.DARK_RED + hack);
            } else {
                allCheats.appendText(" " + EnumChatFormatting.GOLD + hack);
            }
        }
        return allCheats;
    }

    private static boolean isRedHack(String s) {
        return s.startsWith("bhop") ||
                s.startsWith("autoblock") ||
                s.startsWith("fastbreak") ||
                s.startsWith("noslowdown") ||
                s.startsWith("scaffold");
    }

}