//package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.*;
//import net.minecraft.world.World;
//
//public class RayTraceResult {
//
//    // TODO change final fields
//    /** The closest block/entity from the player */
//    public RayTraceResult.HitType typeOfDirectHit;
//    public BlockPos blockPosDirectHit;
//    public Entity entityDirectHit;
//    /** The second block/entity that is behind, if any */
//    public RayTraceResult.HitType secondHitType;
//    public BlockPos blockPosSecondHit;
//    public Entity entitySecondHit;
//
//    /**
//     * See {@link net.minecraft.entity.Entity#rayTrace}
//     */
//    public RayTraceResult(World world, EntityPlayer player) {
//        Vector3D playersEyesPosition = new Vector3D(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);
//        final double rayTraceDistance = 3.0D; // TODO make a second one, 3 for attack reach, 4.5 for breaking
//        final Vector3D playersLook = Vector3D.getVectorFromRotation(player.rotationPitch, player.rotationYawHead).mulitply(rayTraceDistance);
//        //final MovingObjectPosition movingObjectPosition = world.rayTraceBlocks(playersEyesPosition, playersLook, false, false, true);
//
//        final boolean stopOnLiquid = false;
//        final boolean ignoreBlockWithoutBoundingBox = false;
//        final boolean returnLastUncollidableBlock = true;
//
//        if (Double.isNaN(playersEyesPosition.xCoord) || Double.isNaN(playersEyesPosition.yCoord) || Double.isNaN(playersEyesPosition.zCoord)
//                || Double.isNaN(playersLook.xCoord) || Double.isNaN(playersLook.yCoord) || Double.isNaN(playersLook.zCoord)) {
//            return;
//        }
//
//        final int i = MathHelper.floor_double(playersLook.xCoord);
//        final int j = MathHelper.floor_double(playersLook.yCoord);
//        final int k = MathHelper.floor_double(playersLook.zCoord);
//        int l = MathHelper.floor_double(playersEyesPosition.xCoord);
//        int i1 = MathHelper.floor_double(playersEyesPosition.yCoord);
//        int j1 = MathHelper.floor_double(playersEyesPosition.zCoord);
//        BlockPos blockpos = new BlockPos(l, i1, j1);
//        final IBlockState iblockstate = world.getBlockState(blockpos);
//        final Block block = iblockstate.getBlock();
//
//        /* Checks collision with the block at the current player's eyes pos */
//        if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(world, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
//            MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, blockpos, playersEyesPosition, playersLook);
//            if (movingobjectposition != null) {
//                return movingobjectposition;
//            }
//        }
//
//        MovingObjectPosition movingobjectposition2 = null;
//        int k1 = 200;
//
//        while (k1-- >= 0) {
//            if (Double.isNaN(playersEyesPosition.xCoord) || Double.isNaN(playersEyesPosition.yCoord) || Double.isNaN(playersEyesPosition.zCoord)) {
//                return;
//            }
//
//            if (l == i && i1 == j && j1 == k) {
//                return returnLastUncollidableBlock ? movingobjectposition2 : null;
//            }
//
//            boolean flag2 = true;
//            boolean flag = true;
//            boolean flag1 = true;
//            double d0 = 999.0D;
//            double d1 = 999.0D;
//            double d2 = 999.0D;
//
//            if (i > l) {
//                d0 = (double) l + 1.0D;
//            } else if (i < l) {
//                d0 = (double) l + 0.0D;
//            } else {
//                flag2 = false;
//            }
//
//            if (j > i1) {
//                d1 = (double) i1 + 1.0D;
//            } else if (j < i1) {
//                d1 = (double) i1 + 0.0D;
//            } else {
//                flag = false;
//            }
//
//            if (k > j1) {
//                d2 = (double) j1 + 1.0D;
//            } else if (k < j1) {
//                d2 = (double) j1 + 0.0D;
//            } else {
//                flag1 = false;
//            }
//
//            double d3 = 999.0D;
//            double d4 = 999.0D;
//            double d5 = 999.0D;
//            final double d6 = playersLook.xCoord - playersEyesPosition.xCoord;
//            final double d7 = playersLook.yCoord - playersEyesPosition.yCoord;
//            final double d8 = playersLook.zCoord - playersEyesPosition.zCoord;
//
//            if (flag2) {
//                d3 = (d0 - playersEyesPosition.xCoord) / d6;
//            }
//
//            if (flag) {
//                d4 = (d1 - playersEyesPosition.yCoord) / d7;
//            }
//
//            if (flag1) {
//                d5 = (d2 - playersEyesPosition.zCoord) / d8;
//            }
//
//            if (d3 == -0.0D) {
//                d3 = -1.0E-4D;
//            }
//
//            if (d4 == -0.0D) {
//                d4 = -1.0E-4D;
//            }
//
//            if (d5 == -0.0D) {
//                d5 = -1.0E-4D;
//            }
//
//            EnumFacing enumfacing;
//
//            if (d3 < d4 && d3 < d5) {
//                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
//                playersEyesPosition = new Vec3(d0, playersEyesPosition.yCoord + d7 * d3, playersEyesPosition.zCoord + d8 * d3);
//            } else if (d4 < d5) {
//                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
//                playersEyesPosition = new Vec3(playersEyesPosition.xCoord + d6 * d4, d1, playersEyesPosition.zCoord + d8 * d4);
//            } else {
//                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
//                playersEyesPosition = new Vec3(playersEyesPosition.xCoord + d6 * d5, playersEyesPosition.yCoord + d7 * d5, d2);
//            }
//
//            l = MathHelper.floor_double(playersEyesPosition.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
//            i1 = MathHelper.floor_double(playersEyesPosition.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
//            j1 = MathHelper.floor_double(playersEyesPosition.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
//            blockpos = new BlockPos(l, i1, j1);
//            final IBlockState iblockstate1 = world.getBlockState(blockpos);
//            final Block block1 = iblockstate1.getBlock();
//
//            if (!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(world, blockpos, iblockstate1) != null) {
//                if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
//                    MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, blockpos, playersEyesPosition, playersLook);
//                    if (movingobjectposition1 != null) {
//                        return movingobjectposition1;
//                    }
//                } else {
//                    movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, playersEyesPosition, enumfacing, blockpos);
//                }
//            }
//        }
//
//        return returnLastUncollidableBlock ? movingobjectposition2 : null;
//
//    }
//
//    public static enum HitType {
//        MISS,
//        BLOCK,
//        ENTITY
//    }
//
//}
