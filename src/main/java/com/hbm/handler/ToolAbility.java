package com.hbm.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hbm.config.ToolConfig;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.inventory.CentrifugeRecipes;
import com.hbm.inventory.CrystallizerRecipes;
import com.hbm.inventory.ShredderRecipes;
import com.hbm.blocks.generic.BlockStorageCrate;
import com.hbm.blocks.machine.MachineBattery;
import com.hbm.blocks.machine.MachineFENSU;
import com.hbm.items.ModItems;
import com.hbm.items.tool.IItemAbility;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.util.EnchantmentUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ToolAbility {
	public abstract void onDig(Level world, int x, int y, int z, Player player, BlockState block, 
							   IItemAbility tool, InteractionHand hand);
	public abstract String getName();
	@OnlyIn(Dist.CLIENT)
	public abstract String getFullName();
	public abstract String getExtension();
	public abstract boolean isAllowed();
	
	public static class RecursionAbility extends ToolAbility {
		int radius;
		
		public RecursionAbility(int radius) {
			this.radius = radius;
		}
		
		private Set<BlockPos> pos = new HashSet<BlockPos>();

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			
			Block b = world.getBlockState(new BlockPos(x, y, z)).getBlock();

			if(b == Blocks.STONE && !ToolConfig.recursiveStone)
				return;
			if(b == Blocks.NETHERRACK && !ToolConfig.recursiveNetherrack)
				return;
			
			List<Integer> indices = Arrays.asList(new Integer[] {0, 1, 2, 3, 4, 5});
			Collections.shuffle(indices);
			
			pos.clear();
			
			for(Integer i : indices) {
				switch(i) {
				case 0: breakExtra(world, x + 1, y, z, x, y, z, player, tool, hand, 0); break;
				case 1: breakExtra(world, x - 1, y, z, x, y, z, player, tool, hand, 0); break;
				case 2: breakExtra(world, x, y + 1, z, x, y, z, player, tool, hand, 0); break;
				case 3: breakExtra(world, x, y - 1, z, x, y, z, player, tool, hand, 0); break;
				case 4: breakExtra(world, x, y, z + 1, x, y, z, player, tool, hand, 0); break;
				case 5: breakExtra(world, x, y, z - 1, x, y, z, player, tool, hand, 0); break;
				}
			}
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityVein;
		}

		private boolean areRedstone(BlockState ref, BlockState b){
			return ref.getBlock() instanceof BlockRedstoneOre && b.getBlock() instanceof BlockRedstoneOre;
		}
		
		private void breakExtra(Level world, int x, int y, int z, int refX, int refY, int refZ, Player player, 
								IItemAbility tool, InteractionHand hand, int depth) {
			
			if(pos.contains(new BlockPos(x, y, z)))
				return;
			
			depth += 1;

			if(depth > ToolConfig.recursionDepth)
				return;
			
			pos.add(new BlockPos(x, y, z));
			
			//don't lose the ref block just yet
			if(x == refX && y == refY && z == refZ)
				return;
			
			if(Vec3.createVectorHelper(x - refX, y - refY, z - refZ).lengthVector() > radius)
				return;
			
			BlockState b = world.getBlockState(new BlockPos(x, y, z));
			BlockState ref = world.getBlockState(new BlockPos(refX, refY, refZ));
			
			if(b != ref && !areRedstone(ref, b))
				return;
			
			if(player.getItemInHand(hand).isEmpty())
				return;
			
			tool.breakExtraBlock(world, x, y, z, player, refX, refY, refZ, hand);
			
			List<Integer> indices = Arrays.asList(new Integer[] {0, 1, 2, 3, 4, 5});
			Collections.shuffle(indices);
			
			for(Integer i : indices) {
				switch(i) {
				case 0: breakExtra(world, x + 1, y, z, refX, refY, refZ, player, tool, hand, depth); break;
				case 1: breakExtra(world, x - 1, y, z, refX, refY, refZ, player, tool, hand, depth); break;
				case 2: breakExtra(world, x, y + 1, z, refX, refY, refZ, player, tool, hand, depth); break;
				case 3: breakExtra(world, x, y - 1, z, refX, refY, refZ, player, tool, hand, depth); break;
				case 4: breakExtra(world, x, y, z + 1, refX, refY, refZ, player, tool, hand, depth); break;
				case 5: breakExtra(world, x, y, z - 1, refX, refY, refZ, player, tool, hand, depth); break;
				}
			}
		}

		@Override
		public String getName() {
			return "tool.ability.recursion";
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public String getFullName() {
			return I18n.get(getName()) + getExtension();
		}

		@Override
		public String getExtension() {
			return " (" + radius + ")";
		}
		
	}

	public static class HammerAbility extends ToolAbility {

		int range;
		
		public HammerAbility(int range) {
			this.range = range;
		}
		
		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			
			for(int a = x - range; a <= x + range; a++) {
				for(int b = y - range; b <= y + range; b++) {
					for(int c = z - range; c <= z + range; c++) {
						
						if(a == x && b == y && c == z)
							continue;
						
						tool.breakExtraBlock(world, a, b ,c, player, x, y, z, hand);
					}
				}
			}
		}

		@Override
		public String getName() {
			return "tool.ability.hammer";
		}

		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityHammer;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public String getFullName() {
			return I18n.get(getName()) + getExtension();
		}

		@Override
		public String getExtension() {
			return " (" + range + ")";
		}
	}

	public static class SmelterAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			
			if(isNBTThing(block.getBlock())) return;
			//a band-aid on a gaping wound
			if(block.getBlock() == Blocks.LIT_REDSTONE_ORE)
				block = Blocks.REDSTONE_ORE.defaultBlockState();
			
			ItemStack stack = new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block));
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
			
			if(result != null && !result.isEmpty()) {
				world.removeBlock(new BlockPos(x, y, z), false);
				world.addFreshEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, result.copy()));
			}
		}

		@Override
		public String getName() {
			return "tool.ability.smelter";
		}

		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityFurnace;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public String getFullName() {
			return I18n.get(getName());
		}
		
		@Override
		public String getExtension() {
			return "";
		}
	}
	
	public static class ShredderAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			
			//a band-aid on a gaping wound
			if(block.getBlock() == Blocks.LIT_REDSTONE_ORE)
				block = Blocks.REDSTONE_ORE.defaultBlockState();
			
			ItemStack stack = new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block));
			ItemStack result = ShredderRecipes.getShredderResult(stack);
			
			if(result != null && result.getItem() != ModItems.scrap) {
				world.removeBlock(new BlockPos(x, y, z), false);
				world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, result.copy()));
			}
		}

		@Override
		public String getName() {
			return "tool.ability.shredder";
		}

		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityShredder;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public String getFullName() {
			return I18n.get(getName());
		}
		
		@Override
		public String getExtension() {
			return "";
		}
	}
	
	public static class CentrifugeAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			
			//a band-aid on a gaping wound
			if(block.getBlock() == Blocks.LIT_REDSTONE_ORE)
				block = Blocks.REDSTONE_ORE.getDefaultState();
			
			ItemStack stack = new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block));
			ItemStack[] result = CentrifugeRecipes.getOutput(stack);
			
			if(result != null) {
				world.removeBlock(new BlockPos(x, y, z), false);
				
				for(ItemStack st : result) {
					if(st != null)
						world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, st.copy()));
				}
			}
		}

		@Override
		public String getName() {
			return "tool.ability.centrifuge";
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityCentrifuge;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public String getFullName() {
			return I18n.get(getName());
		}
		
		@Override
		public String getExtension() {
			return "";
		}
	}
	
	public static class SilkAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			//if the tool is already enchanted, do nothing
			if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getItemInHand(hand)) > 0 ||
					player.getItemInHand(hand).isEmpty())
				return;

			if(isNBTThing(block.getBlock())) return;

			//add enchantment
			ItemStack stack = player.getItemInHand(hand);

			EnchantmentUtil.addEnchantment(stack, Enchantments.SILK_TOUCH, 1);
			BlockPos pos = new BlockPos(x, y, z);
			block.getBlock().harvestBlock(world, player, pos, block, world.getBlockEntity(pos), stack);
			
			EnchantmentUtil.removeEnchantment(stack, Enchantments.SILK_TOUCH);

			world.removeBlock(pos, false);
		}

		@Override
		public String getName() {
			return "tool.ability.silktouch";
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilitySilk;
		}

		@Override
		public String getFullName() {
			return I18n.get(getName());
		}
		
		@Override
		public String getExtension() {
			return "";
		}
	}
	
	public static class LuckAbility extends ToolAbility {

		int luck;

		public LuckAbility(int luck) {
			this.luck = luck;
		}

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {
			//if the tool is already enchanted, do nothing
			if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getItemInHand(hand)) > 0 ||
					player.getItemInHand(hand) == null)
				return;

			if(isNBTThing(block.getBlock())) return;

			//add enchantment
			ItemStack stack = player.getItemInHand(hand);
			
			EnchantmentUtil.addEnchantment(stack, Enchantments.FORTUNE, luck);
			BlockPos pos = new BlockPos(x, y, z);
			block.getBlock().harvestBlock(world, player, pos, block, world.getBlockEntity(pos), stack);
			EnchantmentUtil.removeEnchantment(stack, Enchantments.FORTUNE);

			world.removeBlock(pos, false);
		}

		@Override
		public String getName() {
			return "tool.ability.luck";
		}

		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityLuck;
		}
		
		@Override
		public String getFullName() {
			return I18n.get(getName()) + getExtension();
		}

		@Override
		public String getExtension() {
			return " (" + luck + ")";
		}
	}
	
	public static class CrystallizerAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {

			//a band-aid on a gaping wound
			if(block.getBlock() == Blocks.LIT_REDSTONE_ORE)
				block = Blocks.REDSTONE_ORE.getDefaultState();

			ItemStack stack = new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block));
			ItemStack result = CrystallizerRecipes.getOutputItem(stack);

			if(result != null) {
				world.removeBlock(new BlockPos(x, y, z), false);
				world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, result.copy()));
			}
		}

		@Override
		public String getExtension() {
			return "";
		}

		@Override
		public String getName() {
			return "tool.ability.crystallizer";
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityCrystallizer;
		}

		@Override
		public String getFullName() {
			return I18n.get(getName());
		}
	}
	
	public static class MercuryAbility extends ToolAbility {

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {

			//a band-aid on a gaping wound
			if(block.getBlock() == Blocks.LIT_REDSTONE_ORE)
				block = Blocks.REDSTONE_ORE.defaultBlockState();

			int mercury = 0;

			if(block.getBlock() == Blocks.REDSTONE_ORE)
				mercury = player.getRNG().nextInt(5) + 4;
			if(block.getBlock() == Blocks.REDSTONE_BLOCK)
				mercury = player.getRNG().nextInt(7) + 8;

			if(mercury > 0) {
				world.removeBlock(new BlockPos(x, y, z), false);
				world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(ModItems.nugget_mercury, mercury)));
			}
		}

		@Override
		public String getExtension() {
			return "";
		}

		@Override
		public String getName() {
			return "tool.ability.mercury";
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityMercury;
		}

		@Override
		public String getFullName() {
			return I18n.get(getName());
		}
	}

	public static class ExplosionAbility extends ToolAbility {

		float strength;

		public ExplosionAbility(float strength) {
			this.strength = strength;
		}

		@Override
		public void onDig(Level world, int x, int y, int z, Player player, BlockState block, IItemAbility tool, InteractionHand hand) {

			ExplosionNT ex = new ExplosionNT(player.level(), player, x + 0.5, y + 0.5, z + 0.5, strength);
			ex.addAttrib(ExAttrib.ALLDROP);
			ex.addAttrib(ExAttrib.NOHURT);
			ex.addAttrib(ExAttrib.NOPARTICLE);
			ex.explode();

			player.level().explode(player, x + 0.5, y + 0.5, z + 0.5, 0.1F,
					false);
		}

		@Override
		public String getExtension() {
			return " (" + strength + ")";
		}

		@Override
		public String getName() {
			return "tool.ability.explosion";
		}
		
		@Override
		public boolean isAllowed() {
			return ToolConfig.abilityExplosion;
		}

		@Override
		public String getFullName() {
			return I18n.get(getName()) + getExtension();
		}
	}

	public boolean isNBTThing(Block b){
		return b instanceof BlockStorageCrate || b instanceof MachineBattery || b instanceof MachineFENSU;
	}
}