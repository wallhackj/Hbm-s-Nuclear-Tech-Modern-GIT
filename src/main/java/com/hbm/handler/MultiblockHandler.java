package com.hbm.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.Blocks.AIR;
import static net.minecraft.world.level.block.Blocks.WATER;

public class MultiblockHandler {

    //public enum EnumDirection { North, East, South, West };
    //                            2      5     3      4
    //                           -z     +x    +z     -x

    public static int EnumToInt(Direction dir) {
        return switch (dir) {
            case NORTH -> 2;
            case EAST -> 5;
            case SOUTH -> 3;
            case WEST -> 4;
            default -> 0;
        };
    }

    public static Direction intToEnumFacing(int dir) {
        return switch (dir) {
            case 2 -> NORTH;
            case 5 -> EAST;
            case 3 -> SOUTH;
            case 4 -> WEST;
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        };
    }


    //Approved!
    //pos x, neg x, pos y, neg y, pos z, neg z
    public static final int[] iGenDimensionNorth = new int[]{1, 1, 2, 0, 3, 2};
    public static final int[] iGenDimensionEast = new int[]{2, 3, 2, 0, 1, 1};
    public static final int[] iGenDimensionSouth = new int[]{1, 1, 2, 0, 2, 3};
    public static final int[] iGenDimensionWest = new int[]{3, 2, 2, 0, 1, 1};
    public static final int[] centDimension = new int[]{0, 0, 3, 0, 0, 0};
    public static final int[] cyclDimension = new int[]{1, 1, 5, 0, 1, 1};
    public static final int[] wellDimension = new int[]{1, 1, 5, 0, 1, 1};
    public static final int[] flareDimension = new int[]{1, 1, 11, 0, 1, 1};
    public static final int[] drillDimension = new int[]{1, 1, 3, 0, 1, 1};
    public static final int[] assemblerDimensionNorth = new int[]{2, 1, 1, 0, 1, 2};
    public static final int[] assemblerDimensionEast = new int[]{2, 1, 1, 0, 2, 1};
    public static final int[] assemblerDimensionSouth = new int[]{1, 2, 1, 0, 2, 1};
    public static final int[] assemblerDimensionWest = new int[]{1, 2, 1, 0, 1, 2};
    public static final int[] chemplantDimensionNorth = new int[]{2, 1, 2, 0, 1, 2};
    public static final int[] chemplantDimensionEast = new int[]{2, 1, 2, 0, 2, 1};
    public static final int[] chemplantDimensionSouth = new int[]{1, 2, 2, 0, 2, 1};
    public static final int[] chemplantDimensionWest = new int[]{1, 2, 2, 0, 1, 2};
    public static final int[] fluidTankDimensionNS = new int[]{1, 1, 2, 0, 2, 2};
    public static final int[] fluidTankDimensionEW = new int[]{2, 2, 2, 0, 1, 1};
    public static final int[] refineryDimensions = new int[]{1, 1, 8, 0, 1, 1};
    public static final int[] pumpjackDimensionNorth = new int[]{1, 1, 4, 0, 6, 0};
    public static final int[] pumpjackDimensionEast = new int[]{0, 6, 4, 0, 1, 1};
    public static final int[] pumpjackDimensionSouth = new int[]{1, 1, 4, 0, 0, 6};
    public static final int[] pumpjackDimensionWest = new int[]{6, 0, 4, 0, 1, 1};
    public static final int[] turbofanDimensionNorth = new int[]{1, 1, 2, 0, 3, 3};
    public static final int[] turbofanDimensionEast = new int[]{3, 3, 2, 0, 1, 1};
    public static final int[] turbofanDimensionSouth = new int[]{1, 1, 2, 0, 3, 3};
    public static final int[] turbofanDimensionWest = new int[]{3, 3, 2, 0, 1, 1};
    public static final int[] AMSLimiterDimensionNorth = new int[]{0, 0, 5, 0, 2, 2};
    public static final int[] AMSLimiterDimensionEast = new int[]{2, 2, 5, 0, 0, 0};
    public static final int[] AMSLimiterDimensionSouth = new int[]{0, 0, 5, 0, 2, 2};
    public static final int[] AMSLimiterDimensionWest = new int[]{2, 2, 5, 0, 0, 0};
    public static final int[] AMSEmitterDimension = new int[]{2, 2, 5, 0, 2, 2,};
    public static final int[] AMSBaseDimension = new int[]{1, 1, 1, 0, 1, 1,};
    public static final int[] radGenDimensionNorth = new int[]{4, 1, 2, 0, 1, 1};
    public static final int[] radGenDimensionEast = new int[]{1, 1, 2, 0, 4, 1};
    public static final int[] radGenDimensionSouth = new int[]{1, 4, 2, 0, 1, 1};
    public static final int[] radGenDimensionWest = new int[]{1, 1, 2, 0, 1, 4};
    public static final int[] reactorSmallDimension = new int[]{0, 0, 2, 0, 0, 0};
    public static final int[] uf6Dimension = new int[]{0, 0, 1, 0, 0, 0};

    //Approved!
    public static boolean checkSpace(Level world, BlockPos pos, int[] dimensions) {
        boolean placable = true;
        BlockPos.MutableBlockPos replace = new BlockPos.MutableBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        for (int a = x - dimensions[1]; a <= x + dimensions[0]; a++) {
            for (int b = y - dimensions[3]; b <= y + dimensions[2]; b++) {
                for (int c = z - dimensions[5]; c <= z + dimensions[4]; c++) {
                    if (!(a == x && b == y && c == z)) {
                        replace.set(a, b, c);
                        BlockState state = world.getBlockState(replace);
                        if (!state.isAir() && state.getBlock() != WATER) {
                            placable = false;
                            break;
                        }
                    }
                }
            }
        }

        return placable;
    }

    public static boolean fillUp(Level level, BlockPos pos, Block block, int[] i) {
        boolean placable = true;
        BlockPos.MutableBlockPos replace = new BlockPos.MutableBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for (int a = x - i[1]; a <= x + i[0]; a++) {
            for (int b = y - i[3]; b <= y + i[2]; b++) {
                for (int c = z - i[5]; c <= z + i[4]; c++) {
                    replace.set(a, b, c);
                    level.setBlock(replace, block.defaultBlockState(), 3);
                }
            }
        }

        return placable;
    }

    public static boolean removeAll(Level world, BlockPos pos, int[] i) {
        boolean placable = true;

        BlockPos.MutableBlockPos replace = new BlockPos.MutableBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for (int a = x - i[1]; a <= x + i[0]; a++) {
            for (int b = y - i[3]; b <= y + i[2]; b++) {
                for (int c = z - i[5]; c <= z + i[4]; c++) {
                    replace.set(a, b, c);
                    world.setBlock(replace, AIR.defaultBlockState(), 3);
                }
            }
        }

        return placable;
    }
}
