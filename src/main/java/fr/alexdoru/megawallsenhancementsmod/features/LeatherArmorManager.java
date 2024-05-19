package fr.alexdoru.megawallsenhancementsmod.features;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class LeatherArmorManager {

    public static ItemStack replaceIronArmor(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        if (slotIn != 0 && ConfigHandler.coloredLeatherArmor &&
                (ScoreboardTracker.isInMwGame || ScoreboardTracker.isMWReplay) &&
                player instanceof EntityPlayerAccessor &&
                ((EntityPlayerAccessor) player).getPlayerTeamColor() != '\0' &&
                isCleanIronArmor(stack)) {
            return createColoredLeatherArmor(stack, ((EntityPlayerAccessor) player).getPlayerTeamColorInt());
        }
        return stack;
    }

    public static void onColorChange(EntityPlayer player, int oldColor, int newColor) {
        if (ConfigHandler.coloredLeatherArmor &&
                (ScoreboardTracker.isInMwGame || ScoreboardTracker.isMWReplay) &&
                player instanceof EntityOtherPlayerMP &&
                oldColor != newColor &&
                player instanceof EntityPlayerAccessor &&
                ((EntityPlayerAccessor) player).getPlayerTeamColor() != '\0') {
            for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                final ItemStack stack = player.inventory.armorInventory[i];
                if (isColoredLeatherArmor(stack, oldColor)) {
                    player.inventory.armorInventory[i] = createColoredLeatherArmor(stack, newColor);
                }
            }
        }
    }

    public static void onSettingChange() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || (!ScoreboardTracker.isInMwGame && !ScoreboardTracker.isMWReplay)) return;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player instanceof EntityOtherPlayerMP && player instanceof EntityPlayerAccessor && ((EntityPlayerAccessor) player).getPlayerTeamColor() != '\0') {
                for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                    final ItemStack stack = player.inventory.armorInventory[i];
                    if (ConfigHandler.coloredLeatherArmor) {
                        if (isCleanIronArmor(stack)) {
                            player.inventory.armorInventory[i] = createColoredLeatherArmor(stack, ((EntityPlayerAccessor) player).getPlayerTeamColorInt());
                        }
                    } else if (isColoredLeatherArmor(stack, ((EntityPlayerAccessor) player).getPlayerTeamColorInt())) {
                        final ItemArmor itemArmor = (ItemArmor) stack.getItem();
                        player.inventory.armorInventory[i] = new ItemStack(new ItemArmor(ItemArmor.ArmorMaterial.IRON, itemArmor.renderIndex, itemArmor.armorType));
                    }
                }
            }
        }
    }

    private static boolean isCleanIronArmor(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor && !stack.isItemEnchanted() && ((ItemArmor) stack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.IRON;
    }

    private static boolean isColoredLeatherArmor(ItemStack stack, int color) {
        if (stack != null && stack.getItem() instanceof ItemArmor && !stack.isItemEnchanted()) {
            final ItemArmor itemArmor = (ItemArmor) stack.getItem();
            return itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && itemArmor.hasColor(stack) && itemArmor.getColor(stack) == color;
        }
        return false;
    }

    private static ItemStack createColoredLeatherArmor(ItemStack currentArmor, int color) {
        final ItemArmor ironArmor = (ItemArmor) currentArmor.getItem();
        final ItemArmor leatherArmor = new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, ironArmor.renderIndex, ironArmor.armorType);
        final ItemStack leatherItemStack = new ItemStack(leatherArmor);
        leatherArmor.setColor(leatherItemStack, color);
        return leatherItemStack;
    }

}
