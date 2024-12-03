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
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;


public class BlockCrashedBomb extends Block implements IBomb {

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
//   BlockBehaviour.Properties properties
	public BlockCrashedBomb(Material materialIn, String s) {
		super(null);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);

		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public BlockEntity createNewTileEntity(Level worldIn, int meta) {
		return new TileEntityCrashedBomb(null, null, null);
	}

//	@Override
	public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
	}

//	@Override
	public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand,
									Direction facing, float hitX, float hitY, float hitZ) {
		if(world.isClientSide)
			return true;
		Item tool = player.getItemInHand(hand).getItem();
		if (tool == ModItems.defuser || tool == ModItems.defuser_desh) {
			if(tool.getMaxDamage(player.getItemInHand(hand)) > 0)
					player.getItemInHand(hand).setDamageValue(1);
			world.destroyBlock(pos, false);

			spawnItemEntity(world, pos, new ItemStack(ModItems.egg_balefire_shard));
			spawnItemEntity(world, pos, new ItemStack(ModItems.plate_steel,
					10 + RandomSource.create().nextInt(15)));
			spawnItemEntity(world, pos, new ItemStack(ModItems.plate_titanium,
					2 + RandomSource.create().nextInt(7)));

			return true;
		}

		return false;
	}

	private void spawnItemEntity(Level world, BlockPos pos, ItemStack stack) {
		ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
		world.addFreshEntity(entity);
	}

//	@Override
	public RenderShape getRenderType(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

//	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

//	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

//	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

//	@Override
	public boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}

//	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

//	@Override
	protected void createBlockState(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

//	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).get2DDataValue();
	}

//	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction enumfacing = Direction.from3DDataValue(meta);

        if (enumfacing.getAxis() == Direction.Axis.Y)
        {
            enumfacing = Direction.NORTH;
        }

        return this.defaultBlockState().setValue(FACING, enumfacing);
	}



//	@Override
	public BlockState withRotation(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

//	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn)
	{
	   return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

//	@Override
	public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
		tooltip.add("§a["+ I18nUtil.resolveKey("trait.balefirebomb")+"]"+"§r");
		tooltip.add(" §e"+I18nUtil.resolveKey("desc.radius", (int) (BombConfig.fatmanRadius * 1.25))+"§r");
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if (!world.isClientSide) {
        	world.removeBlock(pos, false);
//			EntityBalefire bf = new EntityBalefire(world);
//			bf.setPos(pos.getX(), pos.getY(), pos.getZ());
//			bf.destructionRange = (int) (BombConfig.fatmanRadius * 1.25);
//			world.addFreshEntity(bf);

			if(BombConfig.enableNukeClouds) {
				EntityNukeTorex.statFacBale(world, pos.getX() + 0.5, pos.getY() + 5, pos.getZ() + 0.5,
						(int) (BombConfig.fatmanRadius * 1.25));
			}
        }
	}
}
