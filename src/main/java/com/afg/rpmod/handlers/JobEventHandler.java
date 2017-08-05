package com.afg.rpmod.handlers;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.jobs.Farmer;
import com.afg.rpmod.jobs.Job;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JobEventHandler {

	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent e){
		if(!(e.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) e.getEntityLiving();
		IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
		if(!player.world.isRemote){
			if(data.getJob() != null){
				data.getJob().onUpdate();
				//Give income to the player every minute
				if(player.ticksExisted > 1 && player.ticksExisted % 1200 == 0)
					data.setMoney(data.getMoney() + data.getJob().getIncome());
			} else {  
				data.setJob(Job.createJob(0, player));
			}
			data.increaseTotalPlaytime();
		}
	}

	@SubscribeEvent
	public void useHoe(UseHoeEvent e){
		IPlayerData data = e.getEntityPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
		if(!(data.getJob() instanceof Farmer))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void makeBaby(BabyEntitySpawnEvent e ){
		if(e.getCausedByPlayer() != null){
			IPlayerData data = e.getCausedByPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
			if(!(data.getJob() instanceof Farmer) && data.getJobLvl() < 2)
				e.setCanceled(true);
			else{
				data.setJobXP(data.getJobXP() + 3);
			}
		}
	}
	
	@SubscribeEvent
	public void afterSmelt(PlayerEvent.ItemSmeltedEvent e){
		IPlayerData data = e.player.getCapability(IPlayerData.PLAYER_DATA, null);
		data.getJob().afterSmelting(e);
	}
	
	@SubscribeEvent 
	public void afterBreakBlock(HarvestDropsEvent e){
		if(e.getHarvester() != null){
			IPlayerData data = e.getHarvester().getCapability(IPlayerData.PLAYER_DATA, null);
			data.getJob().onBlockDrops(e);
		}
	}
	
	@SubscribeEvent 
	public void afterCraft(PlayerEvent.ItemCraftedEvent e){
		IPlayerData data = e.player.getCapability(IPlayerData.PLAYER_DATA, null);
		data.getJob().afterCraft(e);
	}


	@SubscribeEvent
	public void onKillEntity(LivingDeathEvent e){
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
			IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
			data.getJob().onKill(e);
		}
	}

	@SubscribeEvent
	public void onLivingDropItems(LivingDropsEvent e){
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
			IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
			data.getJob().onLivingDrops(e);
		}
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent e){
		if(e.getPlayer() != null){
			IPlayerData data = e.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
			Block block = e.getWorld().getBlockState(e.getPos()).getBlock();
			if(Job.isExclusiveMineable(block)){
				if(data != null && data.getJob() != null)
					for(Block b : data.getJob().getAvailableMineables())
						if(b == block)
							return;
				e.setCanceled(true);
			}
		}
	}

}
