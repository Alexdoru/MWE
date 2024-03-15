package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.AttackDetector;
import net.minecraft.entity.player.EntityPlayer;

public class AttackInfo {

    public EntityPlayer target;
    public String targetName;
    public final AttackDetector.AttackType attackType;
    /**
     * Used to filter hurt events from an ability damaging multiple players at
     * the same time and mistaken for a player attacking multiple entities
     */
    public boolean multiTarget = false;

    public AttackInfo(EntityPlayer target, AttackDetector.AttackType attackType) {
        this.setTarget(target);
        this.attackType = attackType;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
        this.targetName = target == null ? null : target.getName();
    }

}
