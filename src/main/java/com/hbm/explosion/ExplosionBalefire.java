package com.hbm.explosion;

import com.hbm.blocks.ModBlocks;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ExplosionBalefire {

	public int posX;
	public int posY;
	public int posZ;
	public int lastposX = 0;
	public int lastposZ = 0;
	public int radius;
	public int radius2;
	public Level worldObj;
	private int n = 1;
	private int nlimit;
	private int shell;
	private int leg;
	private int element;
	
	public void saveToNbt(CompoundTag nbt, String name) {
		nbt.putInt(name + "posX", posX);
		nbt.putInt(name + "posY", posY);
		nbt.putInt(name + "posZ", posZ);
		nbt.putInt(name + "lastposX", lastposX);
		nbt.putInt(name + "lastposZ", lastposZ);
		nbt.putInt(name + "radius", radius);
		nbt.putInt(name + "radius2", radius2);
		nbt.putInt(name + "n", n);
		nbt.putInt(name + "nlimit", nlimit);
		nbt.putInt(name + "shell", shell);
		nbt.putInt(name + "leg", leg);
		nbt.putInt(name + "element", element);
	}
	
	public void readFromNbt(CompoundTag nbt, String name) {
		posX = nbt.getInt(name + "posX");
		posY = nbt.getInt(name + "posY");
		posZ = nbt.getInt(name + "posZ");
		lastposX = nbt.getInt(name + "lastposX");
		lastposZ = nbt.getInt(name + "lastposZ");
		radius = nbt.getInt(name + "radius");
		radius2 = nbt.getInt(name + "radius2");
		n = nbt.getInt(name + "n");
		nlimit = nbt.getInt(name + "nlimit");
		shell = nbt.getInt(name + "shell");
		leg = nbt.getInt(name + "leg");
		element = nbt.getInt(name + "element");
	}
	
	public ExplosionBalefire(int x, int y, int z, Level world, int rad)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		this.worldObj = world;
		
		this.radius = rad;
		this.radius2 = this.radius * this.radius;

		this.nlimit = this.radius2 * 4;
	}
	
	public boolean update()
	{
		breakColumn(this.lastposX, this.lastposZ);
		this.shell = (int) Math.floor((Math.sqrt(n) + 1) / 2);
		if(shell == 0)
			shell = 1;
		int shell2 = this.shell * 2;
		this.leg = (int) Math.floor((this.n - (shell2 - 1) * (shell2 - 1)) / shell2);
		this.element = (this.n - (shell2 - 1) * (shell2 - 1)) - shell2 * this.leg - this.shell + 1;
		this.lastposX = this.leg == 0 ? this.shell : this.leg == 1 ? -this.element : this.leg == 2 ?
				-this.shell : this.element;
		this.lastposZ = this.leg == 0 ? this.element : this.leg == 1 ? this.shell : this.leg == 2 ?
				-this.element : -this.shell;
		this.n++;
		return this.n > this.nlimit;
	}

	@SuppressWarnings("deprecation")
	private void breakColumn(int x, int z)
	{
		int dist = (int) (radius - Math.sqrt(x * x + z * z));
		
		if (dist > 0) {
			int pX = posX + x;
			int pZ = posZ + z;
			
//			int y  = worldObj.getHeight(pX, pZ);
			int y  = worldObj.getHeight();
			int maxdepth = (int) (10 + radius * 0.25);
			int voidDepth = (int) ((maxdepth * dist / radius) + (Math.sin(dist * 0.15 + 2) * 2));//
			
			int depth = Math.max(y - voidDepth, 0);
			
			while(y > depth) {

				Block b = worldObj.getBlockState(new BlockPos(pX, y, pZ)).getBlock();
				
				if(b == ModBlocks.block_schrabidium_cluster) {
					
					if(worldObj.random.nextInt(10) == 0) {
						worldObj.setBlockAndUpdate(new BlockPos(pX, y + 1, pZ),
								ModBlocks.balefire.defaultBlockState());
						worldObj.setBlockAndUpdate(new BlockPos(pX, y, pZ),
								ModBlocks.block_euphemium_cluster.
										getStateFromMeta(b.getMetaFromState(worldObj.getBlockState(
												new BlockPos(pX, y, pZ)))), 3);
					}
					return;
				} else if(b == ModBlocks.cmb_brick_reinforced){
					if(worldObj.random.nextInt(10) == 0) {
						worldObj.setBlockAndUpdate(new BlockPos(pX, y + 1, pZ),
								ModBlocks.balefire.defaultBlockState());
					}
					return;
				}
				
				worldObj.removeBlock(new BlockPos(pX, y, pZ), false);
				
				y--;
			}
			
			if(worldObj.random.nextInt(10) == 0) {
				worldObj.setBlockAndUpdate(new BlockPos(pX, depth + 1, pZ),
						ModBlocks.balefire.defaultBlockState());
				
				Block b = worldObj.getBlockState(new BlockPos(pX, y, pZ)).getBlock();
				
				if(b == ModBlocks.block_schrabidium_cluster)
					worldObj.setBlockAndUpdate(new BlockPos(pX, y, pZ),
							ModBlocks.block_euphemium_cluster
									.getStateFromMeta(b.getMetaFromState(worldObj.getBlockState(
											new BlockPos(pX, y, pZ)))), 3);
			}
			int startDepth = (int)(6 * dist / radius);
			for(int i = 0; i <= startDepth; i++) {
				if(worldObj.getBlockState(new BlockPos(pX, depth-i, pZ)).getBlock() == Blocks.STONE){
					switch(startDepth-i){
						case 6: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_core.defaultBlockState()); break;
						case 5: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_4.defaultBlockState()); break;
						case 4: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_3.defaultBlockState()); break;
						case 3: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_2.defaultBlockState()); break;
						case 2: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_1.defaultBlockState()); break;
						case 1: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_0.defaultBlockState()); break;
						case 0: worldObj.setBlockAndUpdate(new BlockPos(pX, depth-i, pZ),
								ModBlocks.baleonitite_slaked.defaultBlockState()); break;
					}
				}
			}
		}
	}
}
