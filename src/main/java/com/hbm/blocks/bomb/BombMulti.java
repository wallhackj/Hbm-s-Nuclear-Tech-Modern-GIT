package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.interfaces.IBomb;
import com.hbm.lib.InventoryHelper;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.bomb.TileEntityBombMulti;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;


public class BombMulti extends Block implements IBomb {

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	public static final AABB MULTI_BB = new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
	
	public final float explosionBaseValue = 8.0F;
	public float explosionValue = 0.0F;
	public int clusterCount = 0;
	public int fireRadius = 0;
	public int poisonRadius = 0;
	public int gasCloud = 0;
	
	public BombMulti(Material materialIn, String s) {
//		super(materialIn);
//		this.setUnlocalizedName(s);
//		this.setRegistryName(s);
		super(null);
		
		ModBlocks.ALL_BLOCKS.add(this);
	}

//	@Override
	public BlockEntity createNewTileEntity(Level worldIn, int meta) {
		return new TileEntityBombMulti();
	}
	
//	@Override
	public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand,
									Direction facing, float hitX, float hitY, float hitZ) {
		if(world.isClientSide)
		{
			return true;
		} else if(!player.isCrouching())
		{
			TileEntityBombMulti entity = (TileEntityBombMulti) world.getBlockEntity(pos);
			if(entity != null)
			{
//				player.openMenu(MainRegistry.instance, ModBlocks.guiID_bomb_multi, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		} else {
			return false;
		}
	}
	
//	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntityBombMulti entity = (TileEntityBombMulti) worldIn.getBlockEntity(pos);
        if (worldIn.hasNeighborSignal(pos))
        {
        	if(/*entity.getExplosionType() != 0*/entity.isLoaded())
        	{
				this.onRemove(state, worldIn, pos, blockIn.defaultBlockState(), false);
            	igniteTestBomb(worldIn, pos.getX(), pos.getY(), pos.getZ());
        	}
        }
	}
	
//	@Override
	public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
		InventoryHelper.dropInventoryItems(worldIn, pos, worldIn.getBlockEntity(pos));
//		super.breakBlock(worldIn, pos, state);
	}
	
//	@Override
	public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer,
								ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, state.setValue(FACING, placer.getDirection().getOpposite()));
	}
	
	public boolean igniteTestBomb(Level world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
    	TileEntityBombMulti entity = (TileEntityBombMulti) world.getBlockEntity(pos);
		if (!world.isClientSide)
		{
        	if(entity.isLoaded())
        	{
        		this.explosionValue = this.explosionBaseValue;
        		switch(entity.return2type())
        		{
        		case 1:
        			this.explosionValue += 1.0F;
        			break;
        		case 2:
        			this.explosionValue += 4.0F;
        			break;
        		case 3:
        			this.clusterCount += 50;
        			break;
        		case 4:
        			this.fireRadius += 10;
        			break;
        		case 5:
        			this.poisonRadius += 15;
        			break;
        		case 6:
        			this.gasCloud += 50;
        		}
        		switch(entity.return5type())
        		{
        		case 1:
        			this.explosionValue += 1.0F;
        			break;
        		case 2:
        			this.explosionValue += 4.0F;
        			break;
        		case 3:
        			this.clusterCount += 50;
        			break;
        		case 4:
        			this.fireRadius += 10;
        			break;
        		case 5:
        			this.poisonRadius += 15;
        			break;
        		case 6:
        			this.gasCloud += 50;
        		}

        		entity.clearSlots();
            	world.removeBlock(pos, false);
            	//world.createExplosion(null, x , y , z , this.explosionValue, true);
            	ExplosionLarge.explode(world, x, y, z, explosionValue, true, true, true);
            	this.explosionValue = 0;
        		
        		if(this.clusterCount > 0)
        		{
                	ExplosionChaos.cluster(world, x, y, z, this.clusterCount, 0.5);
        		}
        		
        		if(this.fireRadius > 0)
        		{
                	ExplosionChaos.burn(world, pos, this.fireRadius);
        		}
        		
        		if(this.poisonRadius > 0)
        		{
                	ExplosionNukeGeneric.wasteNoSchrab(world, pos, this.poisonRadius);
        		}
        		
        		if(this.gasCloud > 0)
        		{
        			ExplosionChaos.spawnChlorine(world, x, y, z, this.gasCloud, this.gasCloud / 50, 0);
        		}
        		
        		this.clusterCount = 0;
        		this.fireRadius = 0;
        		this.poisonRadius = 0;
        		this.gasCloud = 0;
        		
        		
        	}
        }
		return false;
	}
	
//	@Override
	public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
		return MULTI_BB;
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		TileEntityBombMulti entity = (TileEntityBombMulti) world.getBlockEntity(pos);
    	if(/*entity.getExplosionType() != 0*/entity.isLoaded())
    	{
			this.onRemove(world.getBlockState(pos), world, pos, null, false);
        	igniteTestBomb(world, pos.getX(), pos.getY(), pos.getZ());
    	}
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
	protected void createBlockState() {
//		return new BlockStateContainer(this, new IProperty[]{FACING});
	}
	
//	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(FACING).get3DDataValue();
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
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
//	@Override
	public BlockState withMirror(BlockState state, Mirror mirrorIn)
	{
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

}
