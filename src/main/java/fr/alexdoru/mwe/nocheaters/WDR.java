package fr.alexdoru.mwe.nocheaters;

import fr.alexdoru.mwe.commands.CommandReport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WDR implements Comparable<WDR> {

    private final ArrayList<String> cheats;
    private boolean redIcon;
    private long timestamp;

    public WDR(ArrayList<String> cheats) {
        this(cheats, new Date().getTime());
    }

    public WDR(ArrayList<String> cheats, long timestamp) {
        this.cheats = cheats;
        this.cheats.trimToSize();
        this.updateRedIcon();
        this.timestamp = timestamp;
    }

    public boolean addCheat(String cheat) {
        if (!this.cheats.contains(cheat)) {
            this.cheats.add(cheat);
            this.cheats.trimToSize();
            this.updateRedIcon();
            return true;
        }
        this.timestamp = new Date().getTime();
        return false;
    }

    public void addCheats(List<String> list) {
        list.removeAll(this.cheats);
        this.cheats.addAll(list);
        this.cheats.trimToSize();
        this.updateRedIcon();
        this.timestamp = new Date().getTime();
    }

    private void updateRedIcon() {
        if (this.redIcon) return;
        for (final String cheat : this.cheats) {
            if (isRedCheat(cheat)) {
                this.redIcon = true;
                return;
            }
        }
        this.redIcon = false;
    }

    public boolean hasRedIcon() {
        return this.redIcon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean hasValidCheats() {
        for (final String cheat : this.cheats) {
            if (CommandReport.cheatsList.contains(cheat)) {
                return true;
            }
        }
        return false;
    }

    public String cheatsToString() {
        final StringBuilder sb = new StringBuilder();
        for (final String cheat : this.cheats) {
            sb.append(" ").append(cheat);
        }
        return sb.toString();
    }

    public IChatComponent getFormattedCheats() {
        final IChatComponent imsg = new ChatComponentText("");
        for (final String cheat : this.cheats) {
            if (isRedCheat(cheat)) {
                imsg.appendText(" " + EnumChatFormatting.DARK_RED + cheat);
            } else {
                imsg.appendText(" " + EnumChatFormatting.GOLD + cheat);
            }
        }
        return imsg;
    }

    @Override
    public int compareTo(@Nonnull WDR wdr) {
        return Long.compare(this.timestamp, wdr.timestamp);
    }

    private static boolean isRedCheat(String cheat) {
        cheat = cheat.toLowerCase();
        return cheat.startsWith("bhop") ||
                cheat.startsWith("autoblock") ||
                cheat.startsWith("fastbreak") ||
                cheat.startsWith("noslowdown") ||
                cheat.startsWith("scaffold");
    }

}