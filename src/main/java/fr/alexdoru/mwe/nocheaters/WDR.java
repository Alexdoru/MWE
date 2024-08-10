package fr.alexdoru.mwe.nocheaters;

import fr.alexdoru.mwe.commands.CommandReport;
import fr.alexdoru.mwe.config.MWEConfig;
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
    private boolean yellowIcon;
    private long timestamp;

    public WDR(String cheat) {
        this.cheats = new ArrayList<>(1);
        this.cheats.add(cheat);
        this.updateIcon();
        this.timestamp = new Date().getTime();
    }

    public WDR(List<String> cheats) {
        this(cheats, new Date().getTime());
    }

    public WDR(List<String> cheats, long timestamp) {
        this.cheats = new ArrayList<>(cheats);
        this.cheats.trimToSize();
        this.updateIcon();
        this.timestamp = timestamp;
    }

    public boolean addCheat(String cheat) {
        if (!this.cheats.contains(cheat)) {
            if (cheat.endsWith("[H]")) {
                this.cheats.remove("cheating");
            }
            this.cheats.add(cheat);
            this.cheats.trimToSize();
            this.updateIcon();
            return true;
        }
        this.timestamp = new Date().getTime();
        return false;
    }

    public void addCheats(List<String> list) {
        if (this.cheats.isEmpty() || list.size() != 1 || !list.get(0).equals("cheating")) {
            list.removeAll(this.cheats);
            this.cheats.addAll(list);
            this.cheats.trimToSize();
            this.updateIcon();
        }
        this.timestamp = new Date().getTime();
    }

    private void updateIcon() {
        if (this.redIcon) return;
        for (final String cheat : this.cheats) {
            if (isRedCheat(cheat)) {
                this.redIcon = true;
                this.yellowIcon = false;
                return;
            }
        }
        for (final String cheat : this.cheats) {
            if (!isNoIconCheat(cheat)) {
                this.redIcon = false;
                this.yellowIcon = true;
                return;
            }
        }
        this.redIcon = false;
        this.yellowIcon = false;
    }

    public boolean hasRedIcon() {
        return this.redIcon;
    }

    public boolean hasYellowIcon() {
        return this.yellowIcon;
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
        for (final String s : MWEConfig.redIconCheats) {
            if (cheat.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNoIconCheat(String cheat) {
        cheat = cheat.toLowerCase();
        for (final String s : MWEConfig.noIconCheats) {
            if (cheat.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

}