package com.hbm.items.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IItemAbility {

	public void breakExtraBlock(Level world, int x, int y, int z, Player player, int refX, int refY, int refZ, InteractionHand hand);
}