package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GuiScreenBookHook {

    private static boolean isOnNickGenerationPage = false;
    private static long lastTimeClicked;

    private static final Pattern nickSuccessPagePattern = Pattern.compile("You have finished setting up your nickname!\\s*When you go into a game, you will be nicked as\\s*(?:|\\[(?:MV|VI)P\\+?\\+?\\] )(\\w{2,16})");
    private static final Pattern nickGenerationPagePattern = Pattern.compile("We've generated a random username for you:\\s*\\w{2,16}");

    public static void onBookInit(ItemStack book) {
        isOnNickGenerationPage = false;
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
                    Matcher matcher = nickSuccessPagePattern.matcher(pagetext);
                    if (matcher.find()) {
                        final String newNick = matcher.group(1);
                        if (newNick != null && !newNick.equals("")) {
                            ConfigHandler.hypixelNick = newNick;
                            ConfigHandler.saveConfig();
                            return;
                        }
                    }
                    matcher = nickGenerationPagePattern.matcher(pagetext);
                    if (matcher.find()) {
                        isOnNickGenerationPage = true;
                        return;
                    }
                }
            }
        }
    }

    public static void onKeyTyped(int keycode) {
        if (isOnNickGenerationPage && System.currentTimeMillis() - lastTimeClicked > 500 && MegaWallsEnhancementsMod.newNickKey.getKeyCode() != 0 && MegaWallsEnhancementsMod.newNickKey.getKeyCode() == keycode) {
            lastTimeClicked = System.currentTimeMillis();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/nick help setrandom");
        }
    }

    public static void renderInstructions(int screenWidth, int y) {
        if (isOnNickGenerationPage) {
            final int keyCode = MegaWallsEnhancementsMod.newNickKey.getKeyCode();
            final String text;
            if (keyCode == 0) {
                text = EnumChatFormatting.GREEN + "Go to " + EnumChatFormatting.GOLD + "Option -> Controls -> MegaWallsEnhancements" + EnumChatFormatting.GREEN + ", to set the keybind to get a random nick";
            } else {
                text = EnumChatFormatting.GREEN + "Press " + EnumChatFormatting.GOLD + Keyboard.getKeyName(keyCode) + EnumChatFormatting.GREEN + " to get a new random nick";
            }
            drawCenteredStringAt(screenWidth / 2, y + 24 + 24, text);
        }
    }

    private static void drawCenteredStringAt(int x, int y, String text) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, (float) (x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2), (float) y, 0);
    }

}
