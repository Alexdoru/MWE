package fr.alexdoru.mwe.updater;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.ModUpdater;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import java.io.File;

import static net.minecraft.util.EnumChatFormatting.*;

public final class MWEUpdater extends ModUpdater {

    public MWEUpdater(File modJarFile) {
        super(
                modJarFile,
                MWE.modName,
                MWE.version,
                MWEConfig.automaticUpdate
        );
    }

    @Override
    protected String getApiEndpoint() {
        return "https://api.github.com/repos/Alexdoru/MWE/releases/latest";
    }

    @Override
    protected void printChatNotification() {
        if (this.updateInfo != null) {
            final String releaseLink = "https://github.com/Alexdoru/MWE/releases";
            ChatUtil.addChatMessage(DARK_GRAY + ChatUtil.bar());
            ChatUtil.addChatMessage(ChatUtil.centerLine(DARK_RED.toString() + BOLD + "    MWE " + GOLD + "v" + this.updateInfo.version + GREEN + " is available!"));
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.centerLine(YELLOW + "    Click here to view the changelog & download page."))
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, releaseLink))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + releaseLink)))));
            if (this.automaticUpdate) {
                ChatUtil.addChatMessage("");
                if (this.isFeatherClient) {
                    ChatUtil.addChatMessage(new ChatComponentText(RED + "✘ The automatic updater is disabled on Feather."));
                } else if (this.downloadSuccess) {
                    ChatUtil.addChatMessage(new ChatComponentText(GREEN + "✔ Update has been downloaded and will be installed automatically when closing the game."));
                }
            }
            ChatUtil.addChatMessage(DARK_GRAY + ChatUtil.bar());
        }
    }

}
