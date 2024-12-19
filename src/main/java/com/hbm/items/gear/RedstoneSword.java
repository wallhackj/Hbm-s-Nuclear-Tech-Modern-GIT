package com.hbm.items.gear;

import java.util.List;
import java.util.Random;

import com.hbm.interfaces.IHasCustomModel;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneSword extends SwordItem implements IHasCustomModel {
    //Pridenauer you damn bastard.
    //Drillgon200: ^^ What's this supposed to mean? No idea.
    //Alcater: No idea. I am adding a tooltip to pay some respect to this item.

    private static final Random rand = new Random();

    public static final ModelResourceLocation rsModel = new ModelResourceLocation(
            new ResourceLocation("hbm", "redstone_sword"),"inventory");

    public RedstoneSword(Tiers t, String s) {
        super(t, 3, -2.4F, new Item.Properties());
        ModItems.ALL_ITEMS.add(this);
    }

//    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFull3D() {
        return true;
    }

//    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, Level world, List<String> list, TooltipFlag flagIn) {
        list.add("§5§lVery First NTM Item");
    }


//    @Override
    public InteractionResult onItemUse(Player player, Level worldIn, BlockPos pos, InteractionHand hand,
                                      Direction facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isClientSide) {
            return InteractionResult.PASS;
        }
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (facing.ordinal() == 0) {
            --y;
        }

        if (facing.ordinal() == 1) {
            ++y;
        }

        if (facing.ordinal() == 2) {
            --z;
        }

        if (facing.ordinal() == 3) {
            ++z;
        }

        if (facing.ordinal() == 4) {
            --x;
        }

        if (facing.ordinal() == 5) {
            ++x;
        }
        BlockPos editpos = new BlockPos(x, y, z);
        if (!player.ca(editpos, facing, player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        } else {
            if (worldIn.isEmptyBlock(editpos) && worldIn.isBlockFullCube(pos)) {
                worldIn.playSound(x + 0.5D, y + 0.5D, z + 0.5D,
                        SoundEvents, SoundSource.BLOCKS, 1.0F,
                        rand.nextFloat() * 0.4F + 0.8F, false);
                worldIn.setBlock(editpos, Blocks.REDSTONE_WIRE.defaultBlockState(), 1);
                player.getItemInHand(hand).setDamageValue(14);
                if (player.getItemInHand(hand).getDamageValue() >= player.getItemInHand(hand).getMaxDamage()) {
                    player.getItemInHand(hand).shrink(1);
                }
            }

        }
        super.useOn()
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ModelResourceLocation getResourceLocation() {
        return rsModel;
    }

}
