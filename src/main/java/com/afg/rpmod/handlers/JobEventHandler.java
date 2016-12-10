package com.afg.rpmod.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.entities.EntityNPC;
import com.afg.rpmod.jobs.Inventor;
import com.afg.rpmod.jobs.Job;
import com.afg.rpmod.jobs.crafting.CraftingEvent;

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
			data.increaseTotalPlaytime();
		}
	}

	@SubscribeEvent 
	public void afterCraft(PlayerEvent.ItemCraftedEvent e){
		IPlayerData data = e.player.getCapability(IPlayerData.PLAYER_DATA, null);
		data.getJob().afterCraft(e);
	}


	@SubscribeEvent
	public void onKillEntity(LivingDeathEvent e){
		if(e.getSource().getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) e.getSource().getEntity();
			IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
			data.getJob().onKill(e);
		}
	}

	@SubscribeEvent
	public void onLivingDropItems(LivingDropsEvent e){
		if(e.getSource().getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) e.getSource().getEntity();
			IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
			data.getJob().onLivingDrops(e);
		}
	}

	@SubscribeEvent
	public void onCustomCraft(CraftingEvent e){
		IPlayerData data = e.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
		if(Job.isExclusiveRecipe(e.getStack().getItem())){
			if(data != null && data.getJob() != null)
				for(Item i : data.getJob().getAvailableRecipes())
					if(i == e.getStack().getItem())
						e.setCanceled(!Inventor.isDiscovered(e.getStack().getItem()));
			e.setCanceled(true);
		} else {
			e.setCanceled(!Inventor.isDiscovered(e.getStack().getItem()));
		}
	}

}
