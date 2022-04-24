package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class GuiContainerHook {

    private static final HashSet<Item> itemWhitelist = new HashSet<>();

    static {
        itemWhitelist.add(Items.diamond_pickaxe);
        itemWhitelist.add(Item.getItemFromBlock(Blocks.ender_chest));
        itemWhitelist.add(Items.iron_axe);
        itemWhitelist.add(Items.iron_shovel);
        itemWhitelist.add(Items.diamond_shovel);
        itemWhitelist.add(Items.carrot_on_a_stick);
        itemWhitelist.add(Items.golden_apple);
        itemWhitelist.add(Items.compass);
    }

    public static boolean shouldCancelHotkey(Slot theSlot, int i) {
        final boolean isItemImportant = isItemImportant(theSlot, i);
        if (isItemImportant) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Prevented dangerous hotkey in external inventory"));
            return true;
        }
        return false;
    }

    /**
     * Returns true for :
     * diamond pickaxe
     * ender chest
     * iron axe
     * iron shovel
     * diamond shovel
     * carrot on a stick
     * gapple
     * compass
     * iron sword w sharpness
     * diamond sword except quantum sword check silk touch
     * potions / exept phx/ren splash & squid abs
     * bow except if you have enchanted bows on you (pirate bows)
     */
    private static boolean isItemImportant(Slot theSlot, int i) {
        if (FKCounterMod.isInMwGame && ConfigHandler.safeInventory) {
            /*Targeted inventory for the hotkeying*/
            if (theSlot.inventory instanceof InventoryPlayer) {
                return false;
            }
            final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
            final ItemStack itemStack = thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStackHasCustomDisplayName(itemStack)) {
                final Item item = itemStack.getItem();
                if (itemWhitelist.contains(item)) {
                    return true;
                }
                if (item == Items.iron_sword && isEnchantedWithSharpness(itemStack)) {
                    return true;
                }
                if (item == Items.diamond_sword && !isEnchantedWithSilkTouch(itemStack)) {
                    return true;
                }
                if (item instanceof ItemPotion) {
                    final int metadata = itemStack.getMetadata();
                    final boolean isSplash = ItemPotion.isSplash(metadata);
                    final List<PotionEffect> potionEffects = ((ItemPotion) item).getEffects(metadata);
                    /*Kit Speed potions*/
                    if (potionEffects.size() == 1 && potionEffects.get(0).getPotionID() == Potion.moveSpeed.id) {
                        return false;
                    }
                    /*Squid Pots*/
                    if (potionEffects.size() == 1 && potionEffects.get(0).getPotionID() == Potion.absorption.id) {
                        return false;
                    }
                    for (PotionEffect effect : potionEffects) {
                        /*Phoenix & Renegade Pots*/
                        if (effect.getPotionID() == Potion.regeneration.id && isSplash) {
                            return false;
                        }
                    }
                    return true;
                }
                if (item == Items.bow) {
                    if (itemStack.isItemEnchanted()) {
                        return true;
                    }
                    return !hasAnotherBowThatsEnchanted(thePlayer.inventory.mainInventory);
                }
            }
        }
        return false;
    }

    private static boolean itemStackHasCustomDisplayName(ItemStack itemStack) {
        final NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = tagCompound.getCompoundTag("display");
            return nbttagcompound.hasKey("Name", 8);
        }
        return false;
    }

    private static boolean isEnchantedWithSharpness(ItemStack itemStack) {
        return EnchantmentHelper.getEnchantments(itemStack).containsKey(Enchantment.sharpness.effectId);
    }

    private static boolean isEnchantedWithSilkTouch(ItemStack itemStack) {
        return EnchantmentHelper.getEnchantments(itemStack).containsKey(Enchantment.silkTouch.effectId);
    }

    private static boolean hasAnotherBowThatsEnchanted(ItemStack[] mainInventory) {
        for (ItemStack itemStack : mainInventory) {
            if (itemStack != null) {
                final Item item = itemStack.getItem();
                if (item == Items.bow && itemStack.isItemEnchanted()) {
                    return true;
                }
            }
        }
        return false;
    }

}
