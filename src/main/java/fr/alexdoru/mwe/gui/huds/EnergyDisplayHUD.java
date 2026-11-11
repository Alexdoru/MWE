package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;

public class EnergyDisplayHUD extends SimpleTextRenderer {

    private static final Set<Item> VALID_ITEMS = new HashSet<>();

    private long timeStartRender;
    private int prevEnergyValue;
    private boolean hasEnergyToDisplay;

    static {
        VALID_ITEMS.add(Items.diamond_sword);
        VALID_ITEMS.add(Items.iron_sword);
        VALID_ITEMS.add(Items.golden_sword);
        VALID_ITEMS.add(Items.bow);
        VALID_ITEMS.add(Items.diamond_shovel);
    }

    public EnergyDisplayHUD() {
        super(MWEConfig.energyHUDPosition);
        this.setCentered();
        this.setDummyText(EnumChatFormatting.AQUA + EnumChatFormatting.BOLD.toString() + 100);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !this.isEnabled()) {
            return;
        }
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return;
        }
        final ItemStack itemStack = player.getHeldItem();
        final int energy = player.experienceLevel;
        if (itemStack != null && VALID_ITEMS.contains(itemStack.getItem())) {
            if (energy != this.prevEnergyValue) {
                this.timeStartRender = System.currentTimeMillis();
            }
            this.prevEnergyValue = energy;
        }
        this.hasEnergyToDisplay = itemStack != null && itemStack.getItem() != null && energy != 0;
        if (energy >= MWEConfig.highEnergyThreshold) {
            this.setText(EnumChatFormatting.BOLD.toString() + energy);
            this.setColor(MWEConfig.highEnergyHUDColor);
        } else {
            this.setText(String.valueOf(energy));
            this.setColor(MWEConfig.lowEnergyHUDColor);
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.isEnabled() && this.hasEnergyToDisplay && this.timeStartRender + 2500L > currentTimeMillis;
    }

    private boolean isEnabled() {
        return this.rendererPosition.isEnabled() && this.parser.isInMwGame();
    }

}
