//package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;
//
//import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemSword;
//
//public class AutoblockCheckB extends AbstractCheck {
//
//    @Override
//    public String getCheatName() {
//        return "Autoblock";
//    }
//
//    @Override
//    public String getFlagType() {
//        return "B";
//    }
//
//    @Override
//    public String getCheatDescription() {
//        return "The player can attack while their sword is blocked";
//    }
//
//    @Override
//    public boolean canSendReport() {
//        return true;
//    }
//
//    @Override
//    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
//        super.checkViolationLevel(player, this.check(player, data), data.autoblockBVL);
//    }
//
//    @Override
//    public boolean check(EntityPlayer player, PlayerDataSamples data) {
//        if (data.hasAttacked) {
//            final ItemStack itemStack = player.getHeldItem();
//            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
//                if (data.useItemTime > 6) {
//                    data.autoblockBVL.add(2);
//                    if (ConfigHandler.debugLogging) {
//                        this.log(player, data, data.autoblockBVL, "useItemTime " + data.useItemTime + " target " + data.targetedPlayer.getName());
//                    }
//                    return true;
//                } else {
//                    data.autoblockBVL.substract(1);
//                }
//            }
//        }
//        return false;
//    }
//
//    public static ViolationLevelTracker newViolationTracker() {
//        return new ViolationLevelTracker(8);
//    }
//
//}
//
