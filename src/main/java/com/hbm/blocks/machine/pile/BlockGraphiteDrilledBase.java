package com.hbm.blocks.machine.pile;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockHazardFuel;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockGraphiteDrilledBase extends BlockHazardFuel {

	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis", Direction.Axis.class);

	public BlockGraphiteDrilledBase(String s) {
        super(null, s, 1, 1, 16000);
//		super(ModBlocks.block_graphite.getDefaultState().getMaterial(), s, ((BlockHazardFuel) ModBlocks.block_graphite).encouragement, ((BlockHazardFuel) ModBlocks.block_graphite).flammability, 16000);
		this.setSoundType(SoundType.METAL);
//		this.setHardness(5.0F);
//		this.setResistance(10.0F);
	}

	protected static void ejectItem(Level world, int x, int y, int z, Direction dir, ItemStack stack) {
		// Create an ItemEntity (formerly EntityItem)
		BlockPos pos = new BlockPos(x, y, z);
		ItemEntity dust = new ItemEntity(world, pos.getX() + 0.5D + dir.getStepX() * 0.75D, pos.getY() + 0.5D + dir.getStepY() * 0.75D, pos.getZ() + 0.5D + dir.getStepZ() * 0.75D, stack);

		// Set the motion direction
		dust.setDeltaMovement(dir.getStepX() * 0.25D, dir.getStepY() * 0.25D, dir.getStepZ() * 0.25D);

		// Add the entity to the world (spawn it)
		world.addFreshEntity(dust);
	}

//	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune){
		return Items.AIR;
	}
	
//	@Override
	public void getDrops(NonNullList<ItemStack> drops, BlockGetter world, BlockPos pos, BlockState state, int fortune){
		drops.add(new ItemStack(ModItems.ingot_graphite, 8));
	}
	
//	@Override
	public int getMetaFromState(BlockState state){
		return state.getValue(AXIS).ordinal();
	}
	
//	@Override
	public BlockState getStateFromMeta(int meta){
		return this.defaultBlockState().setValue(AXIS, Direction.Axis.values()[meta&3]);
	}
	
//	@Override
	protected BlockState createBlockState() {
		// Create a BlockState using the new system (directly use the property in the BlockState constructor)
		return this.defaultBlockState().setValue(AXIS, Direction.Axis.X);  // Default to X axis or whatever you choose
	}
}
