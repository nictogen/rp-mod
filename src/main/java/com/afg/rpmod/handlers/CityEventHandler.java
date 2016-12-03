package com.afg.rpmod.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.utils.CityUtils;

public class CityEventHandler {
	

	@SubscribeEvent
	public void checkBreak(PlayerEvent.BreakSpeed e){
		e.setCanceled(CityUtils.checkPermission(e.getEntityPlayer(), e.getPos()));
	}
	
	@SubscribeEvent
	public void checkPlace(BlockEvent.PlaceEvent e){
//		e.setCanceled(CityUtils.checkPermission(e.getPlayer(), e.getPos()));
	}
	
	@SubscribeEvent
	public void checkExplosion(ExplosionEvent.Start e){
		if(e.getExplosion().getExplosivePlacedBy() instanceof EntityPlayer)
			e.setCanceled(CityUtils.checkPermission((EntityPlayer) e.getExplosion().getExplosivePlacedBy(), new BlockPos(e.getExplosion().getPosition().xCoord, e.getExplosion().getPosition().yCoord, e.getExplosion().getPosition().zCoord)));
	}
	
	
}
