package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.potion.HbmPotion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Balefire extends FireBlock {

    public Balefire(String s) {
        super(null);
//        this.setUnlocalizedName(s);
//        this.setRegistryName(s);
//        this.setCreativeTab(null);
//
//        ModBlocks.ALL_BLOCKS.add(this);
    }

//    @Override
    protected boolean canDie(Level worldIn, BlockPos pos) {
        Block b = worldIn.getBlockState(pos).getBlock();

        return (!b.equals(ModBlocks.baleonitite_slaked) &&
                !b.equals(ModBlocks.baleonitite_1) &&
                        !b.equals(ModBlocks.baleonitite_2) &&
                                !b.equals(ModBlocks.baleonitite_3) &&
                                        !b.equals( ModBlocks.baleonitite_4) &&
                                                !b.equals(ModBlocks.baleonitite_core));
    }

//    @Override
//    public int getFlammability(Block b) {
//        if (b != ModBlocks.baleonitite_slaked &&
//                b != ModBlocks.baleonitite_1 &&
//                b != ModBlocks.baleonitite_2 &&
//                b != ModBlocks.baleonitite_3 &&
//                b != ModBlocks.baleonitite_4 &&
//                b != ModBlocks.baleonitite_core) {
//            return 20000;
//        }
//        return super.getEncouragement(b);
//    }

//    @Override
//    public int getEncouragement(Block b) {
//        if (b != ModBlocks.baleonitite_slaked &&
//                b != ModBlocks.baleonitite_1 &&
//                b != ModBlocks.baleonitite_2 &&
//                b != ModBlocks.baleonitite_3 &&
//                b != ModBlocks.baleonitite_4 &&
//                b != ModBlocks.baleonitite_core) {
//            return 20000;
//        }
//        return super.getEncouragement(b);
//    }

//    @Override
    public void onEntityCollidedWithBlock(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
//        entityIn.setFire(10);

        if (entityIn instanceof LivingEntity)
//            ((LivingEntity) entityIn).addEffect(new PotionEffect(HbmPotion.radiation, 5 * 20, 9));
            return;
    }
}
