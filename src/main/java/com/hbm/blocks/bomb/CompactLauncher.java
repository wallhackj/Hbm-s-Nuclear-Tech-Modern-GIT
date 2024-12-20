package com.hbm.blocks.bomb;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IBomb;
import com.hbm.interfaces.IMultiBlock;
import com.hbm.lib.InventoryHelper;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.bomb.TileEntityCompactLauncher;
import com.hbm.tileentity.machine.TileEntityDummy;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;


public class CompactLauncher extends Block implements IMultiBlock, IBomb {

	public static final AABB COMPACT_BOX = new AABB(0, 1, 0, 1, 1, 1);
	
	public CompactLauncher(Material materialIn, String s) {
		super(materialIn);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public BlockEntity createNewTileEntity(Level worldIn, int meta) {
		return new TileEntityCompactLauncher();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.struct_launcher_core);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ModBlocks.struct_launcher_core);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
		{
			return true;
		} else if(!player.isSneaking())
		{
			TileEntityCompactLauncher entity = (TileEntityCompactLauncher) world.getTileEntity(pos);
			if(entity != null)
			{
				player.openGui(MainRegistry.instance, ModBlocks.guiID_compact_launcher, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntityCompactLauncher te = (TileEntityCompactLauncher) world.getTileEntity(pos);
		
		if(!(world.getBlockState(pos.add(1, 0, 1)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(1, 0, 0)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(1, 0, -1)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(0, 0, -1)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(-1, 0, -1)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(-1, 0, 0)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(-1, 0, 1)).getMaterial().isReplaceable() &&
				world.getBlockState(pos.add(0, 0, 1)).getMaterial().isReplaceable())) {
			world.destroyBlock(pos, true);
			return;
		}

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		placeDummy(world, x + 1, y, z + 1, pos, ModBlocks.dummy_port_compact_launcher);
		placeDummy(world, x + 1, y, z, pos, ModBlocks.dummy_plate_compact_launcher);
		placeDummy(world, x + 1, y, z - 1, pos, ModBlocks.dummy_port_compact_launcher);
		placeDummy(world, x, y, z - 1, pos, ModBlocks.dummy_plate_compact_launcher);
		placeDummy(world, x - 1, y, z - 1, pos, ModBlocks.dummy_port_compact_launcher);
		placeDummy(world, x - 1, y, z, pos, ModBlocks.dummy_plate_compact_launcher);
		placeDummy(world, x - 1, y, z + 1, pos, ModBlocks.dummy_port_compact_launcher);
		placeDummy(world, x, y, z + 1, pos, ModBlocks.dummy_plate_compact_launcher);
		
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}
	
	private void placeDummy(World world, int x, int y, int z, BlockPos target, Block block) {
		BlockPos pos = new BlockPos(x, y, z);
		world.setBlockState(pos, block.getDefaultState());
		
		TileEntity te = world.getTileEntity(pos);
		
		if(te instanceof TileEntityDummy) {
			TileEntityDummy dummy = (TileEntityDummy)te;
			dummy.target = target;
		}
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return COMPACT_BOX;
	}
	
	@Override
	public void explode(World world, BlockPos pos) {
		TileEntityCompactLauncher entity = (TileEntityCompactLauncher) world.getTileEntity(pos);
		if(entity.canLaunch())
			entity.launch();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		InventoryHelper.dropInventoryItems(worldIn, pos, worldIn.getTileEntity(pos));
		super.breakBlock(worldIn, pos, state);
	}

}
