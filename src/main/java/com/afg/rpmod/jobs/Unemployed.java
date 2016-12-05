package com.afg.rpmod.jobs;

import com.afg.rpmod.capabilities.IPlayerData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
	public JobType getType() {
		return JobType.UNEMPLOYED;
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public String getName() {
		return "Unemployed";
	}

	@Override
	public Item[] getExclusiveCraftingRecipes() {
		return new Item[]{};
	}

}
