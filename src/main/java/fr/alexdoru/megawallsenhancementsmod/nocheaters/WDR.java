package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class WDR implements Comparable<WDR> {

    public static final String NICK = "nick";
    public static final String IGNORED = "ignored";

    public long timestamp;
    public long timeLastManualReport;
    public final ArrayList<String> hacks;

    public WDR(long timestamp, long timeLastManualReport, ArrayList<String> hacks) {
        this.timestamp = timestamp;
        this.timeLastManualReport = timeLastManualReport;
        this.hacks = hacks;
        this.hacks.trimToSize();
    }

    /**
     * Compares the timestamp
     */
    @Override
    public int compareTo(@Nonnull WDR wdr) {
        return Long.compare(this.timestamp, wdr.timestamp);
    }

    public boolean transformName() {
        return !(hacks.size() == 1 && hacks.contains(IGNORED));
    }

    public boolean shouldPutRedIcon() {
        for (final String s : this.hacks) {
            if (s.startsWith("bhop")
                    || s.startsWith("autoblock")
                    || s.startsWith("fastbreak")
                    || s.startsWith("noslowdown")) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnlyIgnored() {
        return hacks.size() == 1 && hacks.contains(IGNORED);
    }

    public boolean isNicked() {
        return hacks.contains(NICK);
    }

    public boolean isIgnored() {
        return false;
        //return hacks.contains(IGNORED);
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
            if (hack.startsWith("bhop")
                    || hack.startsWith("autoblock")
                    || hack.startsWith("fastbreak")
                    || hack.startsWith("noslowdown")) {
                allCheats.appendText(" " + EnumChatFormatting.DARK_RED + hack);
            } else if (hack.equalsIgnoreCase(WDR.NICK)) {
                allCheats.appendText(" " + EnumChatFormatting.DARK_PURPLE + hack);
            } else if (!hack.equals(WDR.IGNORED)) {
                allCheats.appendText(" " + EnumChatFormatting.GOLD + hack);
            }
        }
        return allCheats;
    }

}