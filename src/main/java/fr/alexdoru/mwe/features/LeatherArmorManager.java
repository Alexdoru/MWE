package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public final class LeatherArmorManager {

    private static final ItemArmor[] LEATHER_ARMOR = new ItemArmor[]{Items.leather_helmet, Items.leather_chestplate, Items.leather_leggings, Items.leather_boots};
    private static final ItemArmor[] IRON_ARMOR = new ItemArmor[]{Items.iron_helmet, Items.iron_chestplate, Items.iron_leggings, Items.iron_boots};
    private static final String FAKE_ARMOR_TAG = "mwe$fakearmor";

    private final IScoreboardParser parser = ScoreboardTracker.getParser();
    private final ItemStack[] ownFakeArmor = new ItemStack[4];
    private final int[] ownFakeArmorColor = new int[4];

    public ItemStack replaceIronArmor(EntityOtherPlayerMP player, int slotIn, ItemStack stack) {
        if (MWEConfig.coloredLeatherArmor &&
                slotIn != 0 &&
                (this.parser.isInMwGame() || this.parser.isMWReplay()) &&
                ((EntityPlayerAccessor) player).getPlayerTeamColor() != 0 &&
                isCleanIronArmor(stack)) {
            return createColoredLeatherArmor(stack, ((EntityPlayerAccessor) player).getPlayerSpecialRenderColor());
        }
        return stack;
    }

    public ItemStack getFakeArmorForLocalPlayerRender(EntityLivingBase entity, ItemStack stack) {
        if (MWEConfig.coloredLeatherArmor &&
                entity == Minecraft.getMinecraft().thePlayer &&
                this.parser.isInMwGame() &&
                entity instanceof EntityPlayerAccessor &&
                ((EntityPlayerAccessor) entity).getPlayerTeamColor() != 0 &&
                isCleanIronArmor(stack)) {
            return getStackForLocalPlayer(stack, ((EntityPlayerAccessor) entity).getPlayerSpecialRenderColor());
        }
        return stack;
    }

    private ItemStack getStackForLocalPlayer(ItemStack stack, int color) {
        final int armorType = ((ItemArmor) stack.getItem()).armorType;
        if (ownFakeArmor[armorType] == null || ownFakeArmorColor[armorType] != color) {
            ownFakeArmor[armorType] = createColoredLeatherArmor(stack, color);
            ownFakeArmorColor[armorType] = color;
        }
        return ownFakeArmor[armorType];
    }

    public void onColorChange(EntityPlayer player, int oldColor, int newColor) {
        if (MWEConfig.coloredLeatherArmor &&
                oldColor != newColor &&
                (this.parser.isInMwGame() || this.parser.isMWReplay()) &&
                player instanceof EntityOtherPlayerMP &&
                ((EntityPlayerAccessor) player).getPlayerTeamColor() != 0) {
            for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                final ItemStack stack = player.inventory.armorInventory[i];
                if (isFakeLeatherArmor(stack)) {
                    player.inventory.armorInventory[i] = createColoredLeatherArmor(stack, newColor);
                }
            }
        }
    }

    public void onSettingChange() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || (!this.parser.isInMwGame() && !this.parser.isMWReplay())) return;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player instanceof EntityOtherPlayerMP && ((EntityPlayerAccessor) player).getPlayerTeamColor() != 0) {
                for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                    final ItemStack stack = player.inventory.armorInventory[i];
                    if (MWEConfig.coloredLeatherArmor) {
                        if (isCleanIronArmor(stack)) {
                            player.inventory.armorInventory[i] = createColoredLeatherArmor(stack, ((EntityPlayerAccessor) player).getPlayerSpecialRenderColor());
                        }
                    } else if (isFakeLeatherArmor(stack)) {
                        final ItemArmor itemArmor = (ItemArmor) stack.getItem();
                        player.inventory.armorInventory[i] = new ItemStack(IRON_ARMOR[itemArmor.armorType]);
                    }
                }
            }
        }
    }

    private static boolean isCleanIronArmor(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor && !stack.isItemEnchanted() && ((ItemArmor) stack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.IRON;
    }

    private static boolean isFakeLeatherArmor(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemArmor && !stack.isItemEnchanted() && stack.hasTagCompound() && stack.getTagCompound().getBoolean(FAKE_ARMOR_TAG)) {
            final ItemArmor itemArmor = (ItemArmor) stack.getItem();
            return itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && itemArmor.hasColor(stack);
        }
        return false;
    }

    private static ItemStack createColoredLeatherArmor(ItemStack currentArmor, int color) {
        final ItemArmor ironArmor = (ItemArmor) currentArmor.getItem();
        final ItemArmor leatherArmor = LEATHER_ARMOR[ironArmor.armorType];
        final ItemStack leatherItemStack = new ItemStack(leatherArmor);
        leatherArmor.setColor(leatherItemStack, color);
        leatherItemStack.getTagCompound().setBoolean(FAKE_ARMOR_TAG, true);
        return leatherItemStack;
    }

}
