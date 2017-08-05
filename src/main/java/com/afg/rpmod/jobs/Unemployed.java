package com.afg.rpmod.jobs;

import com.afg.rpmod.capabilities.IPlayerData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class Unemployed extends Job {

	public Unemployed(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
		return 2.0*data.getJobLvl();
	}

	@Override
	public EnumJobType getType() {
		return EnumJobType.UNEMPLOYED;
	}

	@Override
	public String getName() {
		return "Unemployed";
	}

	@Override
	public Item[] getAvailableRecipes() {
		return new Item[]{};
	}
	
	@Override
	public Block[] getAvailableMineables() {
		return new Block[]{};
	}


}
