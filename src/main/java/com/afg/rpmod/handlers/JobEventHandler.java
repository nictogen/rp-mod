package com.afg.rpmod.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.jobs.Job;

public class JobEventHandler {

	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent e){
		if(!(e.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) e.getEntityLiving();
		IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
		if(!player.worldObj.isRemote){
			if(data.getJob() != null){
				data.getJob().onUpdate();
				//Give income to the player every minute
				if(player.ticksExisted > 1 && player.ticksExisted % 1200 == 0)
					data.setMoney(data.getMoney() + data.getJob().getIncome());
			} else {
				data.setJob(Job.createJob(0, player));
			}
		}
	}
}
