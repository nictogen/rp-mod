package com.afg.rpmod.jobs;

import com.afg.rpmod.IPlayerData;

import net.minecraft.entity.player.EntityPlayer;

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

}
