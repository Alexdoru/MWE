package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.KeybindingListener;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;
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
    private static final TimerUtil timer = new TimerUtil(500L);

    private static final Pattern nickSuccessPagePattern = Pattern.compile("You have finished setting up your nickname!\\s*When you go into a game, you will be nicked as\\s*(?:|\\[(?:MV|VI)P\\+?\\+?\\] )(\\w{2,16})");
    private static final Pattern nickGenerationPagePattern = Pattern.compile("We've generated a random username for you:\\s*\\w{2,16}");

    public static void onBookInit(ItemStack book) {
        try {
            isOnNickGenerationPage = false;
            if (book.hasTagCompound()) {
                final NBTTagCompound nbttagcompound = book.getTagCompound();
                NBTTagList bookPages = nbttagcompound.getTagList("pages", 8);
                if (bookPages != null) {
                    bookPages = (NBTTagList) bookPages.copy();
                    int bookTotalPages = bookPages.tagCount();
                    if (bookTotalPages < 1) {
                        bookTotalPages = 1;
                    }
                    for (int i = 0; i < bookTotalPages; i++) {
                        final String pagetext = EnumChatFormatting.getTextWithoutFormattingCodes(IChatComponent.Serializer.jsonToComponent(bookPages.getStringTagAt(i)).getUnformattedText().replace("\n", ""));
                        Matcher matcher = nickSuccessPagePattern.matcher(pagetext);
                        if (matcher.find()) {
                            final String newNick = matcher.group(1);
                            if (newNick != null && !newNick.isEmpty()) {
                                if (!ConfigHandler.hypixelNick.isEmpty()) {
                                    final String oldAliasForNick = SquadHandler.getSquad().remove(ConfigHandler.hypixelNick);
                                    if (oldAliasForNick != null) {
                                        if (oldAliasForNick.equals(ConfigHandler.hypixelNick)) {
                                            SquadHandler.addPlayer(newNick);
                                        } else {
                                            SquadHandler.addPlayer(newNick, oldAliasForNick);
                                        }
                                    }
                                }
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void onKeyTyped(int keycode) {
        if (isOnNickGenerationPage && timer.update() && KeybindingListener.getNewNickKey().getKeyCode() != 0 && KeybindingListener.getNewNickKey().getKeyCode() == keycode) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/nick help setrandom");
        }
    }

    public static void renderInstructions(int screenWidth, int y) {
        if (isOnNickGenerationPage) {
            final int keyCode = KeybindingListener.getNewNickKey().getKeyCode();
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
