package com.afg.rpmod.handlers;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.blocks.CityBlock.CityBlockTE;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class CityEventHandler {
	Predicate<TileEntity> pCity = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof CityBlockTE; 
		} };

	Predicate<TileEntity> pPlot = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof PlotBlockTE; 
		} };

	@SubscribeEvent
	public void checkBreak(PlayerEvent.BreakSpeed e){
		e.setCanceled(checkPermission(e.getEntityPlayer(), e.getPos()));
	}
	
	@SubscribeEvent
	public void checkPlace(BlockEvent.PlaceEvent e){
		e.setCanceled(checkPermission(e.getPlayer(), e.getPos()));
	}
	
	@SubscribeEvent
	public void checkExplosion(ExplosionEvent.Start e){
		if(e.getExplosion().getExplosivePlacedBy() instanceof EntityPlayer)
			e.setCanceled(checkPermission((EntityPlayer) e.getExplosion().getExplosivePlacedBy(), new BlockPos(e.getExplosion().getPosition().xCoord, e.getExplosion().getPosition().yCoord, e.getExplosion().getPosition().zCoord)));
	}
	
	private boolean checkPermission(EntityPlayer player, BlockPos pos){
		List<TileEntity> allTEs = player.worldObj.loadedTileEntityList;
		boolean cancel = false;
		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			if(t.getPos().getDistance(pos.getX(), pos.getY(), pos.getZ()) < 100){
				cancel = true;
				}
			}
		if(cancel == true)
			for (TileEntity t : Collections2.filter(allTEs, pPlot)) {
				PlotBlockTE te = (PlotBlockTE) t;
				if(te.getPos().getDistance(pos.getX(), pos.getY(), pos.getZ()) < te.range && te.getPlayer() == player){
					cancel = false;
					}
				}
		return cancel;
	}
}
