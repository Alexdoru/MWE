package fr.alexdoru.mwe.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStackUtil {

    private ItemStackUtil() {}

    public static boolean hasDisplayName(@NotNull ItemStack stack) {
        return stack.hasDisplayName();
    }

    public static String getDisplayName(@NotNull ItemStack stack) {
        return stack.getDisplayName();
    }

    public static boolean hasLore(@NotNull ItemStack stack) {
        final NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey("display", Constants.NBT.TAG_COMPOUND) && nbt.getCompoundTag("display").hasKey("Lore", Constants.NBT.TAG_LIST);
    }

    @Nullable
    public static NBTTagList getLore(@NotNull ItemStack stack) {
        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return null;
        final NBTTagCompound display = nbt.getCompoundTag("display");
        if (display == null) return null;
        return display.getTagList("Lore", Constants.NBT.TAG_STRING);
    }

}
