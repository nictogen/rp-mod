package com.afg.rpmod.handlers;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.blocks.CityBlock.CityBlockTE;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class CityEventHandler {
	Predicate<TileEntity> p = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof CityBlockTE; 
			} };
	
	@SubscribeEvent
	public void checkBreak(PlayerEvent.BreakSpeed e){
		World world = e.getEntity().worldObj;
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, p)) {
		    if(t.getPos().getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) < 100){
		    	e.setCanceled(true);
		    }
		}
	}
}
