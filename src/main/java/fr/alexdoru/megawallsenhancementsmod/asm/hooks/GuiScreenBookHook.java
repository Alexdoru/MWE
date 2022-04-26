package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GuiScreenBookHook {

    private static final Pattern nickSuccessPattern = Pattern.compile("You have finished setting up your nickname!\\s*When you go into a game, you will be nicked as\\s*(?:|\\[(?:MV|VI)P\\+?\\+?\\] )(\\w{2,16})");

    public static void onBookInit(ItemStack book) {
        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            NBTTagList bookPages = nbttagcompound.getTagList("pages", 8);
            if (bookPages != null) {
                bookPages = (NBTTagList) bookPages.copy();
                int bookTotalPages = bookPages.tagCount();
                if (bookTotalPages < 1) {
                    bookTotalPages = 1;
                }
                for (int i = 0; i < bookTotalPages; i++) {
                    String pagetext = EnumChatFormatting.getTextWithoutFormattingCodes(IChatComponent.Serializer.jsonToComponent(bookPages.getStringTagAt(i)).getUnformattedText().replace("\n", ""));
                    final Matcher matcher2 = nickSuccessPattern.matcher(pagetext);
                    if (matcher2.find()) {
                        final String newNick = matcher2.group(1);
                        if (newNick != null && !newNick.equals("")) {
                            ConfigHandler.hypixelNick = newNick;
                            ConfigHandler.saveConfig();
                            return;
                        }
                    }
                }
            }
        }
    }

}
