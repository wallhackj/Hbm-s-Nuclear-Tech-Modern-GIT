package com.hbm.blocks.machine.pile;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockHazardFuel;
import com.hbm.items.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class BlockGraphiteDrilledBase extends BlockHazardFuel {

	public static final PropertyEnum<Direction.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);

	public BlockGraphiteDrilledBase(String s) {
		super(ModBlocks.block_graphite.getDefaultState().getMaterial(), s, ((BlockHazardFuel) ModBlocks.block_graphite).encouragement, ((BlockHazardFuel) ModBlocks.block_graphite).flammability, 16000);
		this.setCreativeTab(null);
		this.setSoundType(SoundType.METAL);
		this.setHardness(5.0F);
		this.setResistance(10.0F);
	}
	
	protected static void ejectItem(Level world, int x, int y, int z, Direction dir, ItemStack stack) {
		
		EntityItem dust = new EntityItem(world, x + 0.5D + dir.getFrontOffsetX() * 0.75D, y + 0.5D + dir.getFrontOffsetY() * 0.75D, z + 0.5D + dir.getFrontOffsetZ() * 0.75D, stack);
		dust.motionX = dir.getFrontOffsetX() * 0.25;
		dust.motionY = dir.getFrontOffsetY() * 0.25;
		dust.motionZ = dir.getFrontOffsetZ() * 0.25;
		world.spawnEntity(dust);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Items.AIR;
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		drops.add(new ItemStack(ModItems.ingot_graphite, 8));
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(AXIS).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[meta&3]);
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, AXIS);
	}
}
