package com.hbm.blocks.bomb;

import java.util.List;

import com.hbm.util.I18nUtil;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityBalefire;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityCrashedBomb;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;


public class BlockCrashedBomb extends BlockContainer implements IBomb {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCrashedBomb(Material materialIn, String s) {
		super(materialIn);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);

		ModBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(Level worldIn, int meta) {
		return new TileEntityCrashedBomb();
	}

	@Override
	public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand,
									Direction facing, float hitX, float hitY, float hitZ) {
		if(world.isClientSide)
			return true;
		Item tool = player.getItemInHand(hand).getItem();
		if (tool == ModItems.defuser || tool == ModItems.defuser_desh) {
			if(tool.getMaxDamage(player.getItemInHand(hand)) > 0)
					player.getItemInHand(hand).setDamageValue(1);
			world.destroyBlock(pos, false);

			world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					new ItemStack(ModItems.egg_balefire_shard)));
			world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					new ItemStack(ModItems.plate_steel, 10 + world.random.nextInt(15))));
			world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					new ItemStack(ModItems.plate_titanium, 2 + world.random.nextInt(7))));

			return true;
		}

		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Direction)state.getValue(FACING)).getIndex();
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction enumfacing = Direction.getFront(meta);

        if (enumfacing.getAxis() == Direction.Axis.Y)
        {
            enumfacing = Direction.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
	}



	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn)
	{
	   return state.withRotation(mirrorIn.toRotation((Direction)state.getValue(FACING)));
	}

	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
		tooltip.add("§a["+ I18nUtil.resolveKey("trait.balefirebomb")+"]"+"§r");
		tooltip.add(" §e"+I18nUtil.resolveKey("desc.radius", (int) (BombConfig.fatmanRadius * 1.25))+"§r");
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if (!world.isClientSide) {

        	world.removeBlock(pos, false);
			EntityBalefire bf = new EntityBalefire(world);
			bf.posX = pos.getX();
			bf.posY = pos.getY();
			bf.posZ = pos.getZ();
			bf.destructionRange = (int) (BombConfig.fatmanRadius * 1.25);
			world.spawnEntity(bf);

			if(BombConfig.enableNukeClouds) {
				EntityNukeTorex.statFacBale(world, pos.getX() + 0.5, pos.getY() + 5, pos.getZ() + 0.5,
						(int) (BombConfig.fatmanRadius * 1.25));
			}
        }
	}
}
