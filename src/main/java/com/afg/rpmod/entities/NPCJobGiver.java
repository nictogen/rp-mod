package com.afg.rpmod.entities;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class NPCJobGiver extends EntityNPC {

	public NPCJobGiver(World worldIn) {
		super(worldIn);
	}

	@Override
	public boolean interact(EntityPlayer player, @Nullable ItemStack stack,
			EnumHand hand) {
		return true;
	}

}
