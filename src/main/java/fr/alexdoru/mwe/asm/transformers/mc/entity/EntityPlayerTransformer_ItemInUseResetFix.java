package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Disables the forge added code that causes the item in use the get cleared
 * locally and allows the client to start sprinting too early instead of waiting
 * for the server's response
 */
public class EntityPlayerTransformer_ItemInUseResetFix implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.player.EntityPlayer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.ENTITYPLAYER$ONUPDATE)) {
                boolean slice = false;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (!slice && checkMethodInsnNode(insnNode, MethodMapping.FORGEEVENTFACTORY$ONITEMUSETICK)) {
                        slice = true;
                    } else if (slice && checkVarInsnNode(insnNode, ALOAD, 0)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkFieldInsnNode(secondNode, GETFIELD, FieldMapping.ENTITYPLAYER$INTEMINUSECOUNT)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkJumpInsnNode(thirdNode, IFGT)) {
                                methodNode.instructions.insertBefore(thirdNode, new MethodInsnNode(
                                        INVOKESTATIC,
                                        getHookClass("mc/entity/EntityPlayerHook_ItemInUseResetFix"),
                                        "cancelForgeCode",
                                        "(I)I",
                                        false
                                ));
                                status.addInjection();
                                return;
                            }
                        }
                    } else if (slice && checkMethodInsnNode(insnNode, MethodMapping.ENTITYPLAYER$ONITEMUSEFINISH)) {
                        return;
                    }
                }
            }
        }
    }

    // vanilla code :
    // if (this.itemInUse != null)
    // {
    //     ItemStack itemstack = this.inventory.getCurrentItem();
    //     if (itemstack == this.itemInUse)
    //     {
    //         if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0)
    //         {
    //             updateItemUse(itemstack, 5);
    //         }
    //         if (--this.itemInUseCount == 0 && !this.worldObj.isRemote)
    //         {
    //             onItemUseFinish();
    //         }
    //     } else {
    //         this.clearItemInUse();
    //     }
    // }

    // forge code :
    // if (this.itemInUse != null)
    // {
    //     ItemStack itemstack = this.inventory.getCurrentItem();
    //     if (itemstack == this.itemInUse)
    //     {
    //         itemInUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, itemInUse, itemInUseCount);
    //         if (itemInUseCount <= 0)
    //         {
    //             this.onItemUseFinish();
    //         }
    //         else
    //         { // Forge Keep unindented to lower patch
    //         itemInUse.getItem().onUsingTick(itemInUse, this, itemInUseCount); //Forge Added
    //         if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0)
    //         {
    //             this.updateItemUse(itemstack, 5);
    //         }
    //         if (--this.itemInUseCount == 0 && !this.worldObj.isRemote)
    //         {
    //             this.onItemUseFinish();
    //         }
    //         }
    //     }
    //     else
    //     {
    //         this.clearItemInUse();
    //     }
    // }

}
