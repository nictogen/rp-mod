package com.afg.rpmod.utils;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.blocks.CityBlock.CityBlockTE;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class CityUtils {
	private static Predicate<TileEntity> pCity = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof CityBlockTE; 
		} };

	private static Predicate<TileEntity> pPlot = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof PlotBlockTE; 
		} };

	private static Predicate<TileEntity> pApt = new Predicate<TileEntity>() { 
		@Override public boolean apply(TileEntity te) { 
			return te instanceof ApartmentBlockTE; 
		} };		
		//TODO check nearby depending plots/apartments when decreasing range
	public static boolean checkPermission(EntityPlayer player, BlockPos pos){
		List<TileEntity> allTEs = player.worldObj.loadedTileEntityList;
		boolean cancel = false;
		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			CityBlockTE te = (CityBlockTE) t;
			int diffX = Math.abs(t.getPos().getX() - pos.getX());
			int diffZ = Math.abs(t.getPos().getZ() - pos.getZ());
			if(diffX <= te.range && diffZ <= te.range){
				if(te.getPlayer() != player)
					cancel = true;
			}
		}
		System.out.println(cancel);
		if(cancel == true)
			for (TileEntity t : Collections2.filter(allTEs, pPlot)) {
				PlotBlockTE te = (PlotBlockTE) t;
				int diffX = Math.abs(t.getPos().getX() - pos.getX());
				int diffZ = Math.abs(t.getPos().getZ() - pos.getZ());
				if(diffX <= ((PlotBlockTE) t).range && diffZ <= ((PlotBlockTE) t).range){
					if(te.getPlayer() == player)
						cancel = false;
				}
			}
		if(cancel == true)
			for (TileEntity t : Collections2.filter(allTEs, pApt)) {
				ApartmentBlockTE te = (ApartmentBlockTE) t;
				int diffX = Math.abs(t.getPos().getX() - pos.getX());
				int diffZ = Math.abs(t.getPos().getZ() - pos.getZ());
				if(diffX <= ((ApartmentBlockTE) t).range && diffZ <= ((ApartmentBlockTE) t).range){
					if(pos.getY() >= te.getPos().getY() && pos.getY() < te.getPos().getY() + te.height)
						if(te.getPlayer() == player)
							cancel = false;
				}
			}
		return cancel;
	}

	public static boolean inCity(World world, BlockPos pos){
		boolean inCity = false;
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			int diffX = Math.abs(t.getPos().getX() - pos.getX());
			int diffZ = Math.abs(t.getPos().getZ() - pos.getZ());
			if(diffX <= ((CityBlockTE) t).range && diffZ <= ((CityBlockTE) t).range){
				inCity = true;
			}
		}
		return inCity;
	}

	public static boolean roomForPlot(World world, PlotBlockTE plot){
		if(plot == null)
			return false;
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pPlot)) {
			PlotBlockTE te = (PlotBlockTE) t;
			if(te != plot){
				int border = te.range + plot.range;
				int diffX = Math.abs(t.getPos().getX() - plot.getPos().getX());
				int diffZ = Math.abs(t.getPos().getZ() - plot.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					return false;
			}
		}

		return true;
	}

	public static boolean roomForCity(World world, CityBlockTE city){
		if(city == null)
			return false;
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			CityBlockTE te = (CityBlockTE) t;
			if(te != city){
				int border = te.range + city.range;
				int diffX = Math.abs(t.getPos().getX() - city.getPos().getX());
				int diffZ = Math.abs(t.getPos().getZ() - city.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					return false;
			}
		}

		return true;
	}

	public static boolean roomForApartment(World world, ApartmentBlockTE apt){
		if(apt == null)
			return false;
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pApt)) {
			ApartmentBlockTE te = (ApartmentBlockTE) t;
			if(te != apt){
				int border = te.range + apt.range;
				int diffX = Math.abs(t.getPos().getX() - apt.getPos().getX());
				int diffZ = Math.abs(t.getPos().getZ() - apt.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					if(apt.getPos().getY() + apt.height > te.getPos().getY())
						return false;
			}
		}

		return true;
	}

	public static CityBlockTE closestCity(World world, BlockPos pos){
		List<TileEntity> allTEs = world.loadedTileEntityList;
		CityBlockTE closest = null;
		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			double dist = Math.sqrt(Math.pow((t.getPos().getX() - pos.getX()), 2) + Math.pow((t.getPos().getZ() - pos.getZ()), 2));
			if(closest == null){
				closest = (CityBlockTE) t;
			} else {
				double closestDist = Math.sqrt(Math.pow((closest.getPos().getX() - pos.getX()), 2) + Math.pow((closest.getPos().getZ() - pos.getZ()), 2));
				if(dist < closestDist)
					closest = (CityBlockTE) t;
			}
		}
		return closest;
	}
	
	public static PlotBlockTE closestPlot(World world, BlockPos pos){
		List<TileEntity> allTEs = world.loadedTileEntityList;
		PlotBlockTE closest = null;
		for (TileEntity t : Collections2.filter(allTEs, pPlot)) {
			double dist = Math.sqrt(Math.pow((t.getPos().getX() - pos.getX()), 2) + Math.pow((t.getPos().getZ() - pos.getZ()), 2));
			if(closest == null){
				closest = (PlotBlockTE) t;
			} else {
				double closestDist = Math.sqrt(Math.pow((closest.getPos().getX() - pos.getX()), 2) + Math.pow((closest.getPos().getZ() - pos.getZ()), 2));
				if(dist < closestDist)
					closest = (PlotBlockTE) t;
			}
		}
		return closest;
	}

	public static ApartmentBlockTE apartmentInside(World world, BlockPos pos){
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pApt)) {
			ApartmentBlockTE te = (ApartmentBlockTE) t;
			int minX = te.getPos().getX() - te.range;
			int maxX = te.getPos().getX() + te.range;
			int minY = te.getPos().getY();
			int maxY = te.getPos().getY() + te.height;
			int minZ = te.getPos().getZ() - te.range;
			int maxZ = te.getPos().getZ() + te.range;
			
			if(pos.getX() >= minX && pos.getX() <= maxX)
				if(pos.getY() >= minY && pos.getY() <= maxY)
					if(pos.getZ() >= minZ && pos.getZ() <= maxZ)
						return te;
		}

		return null;
	}
	public static boolean canExpand(World world, PlotBlockTE expanding, int amount){
		List<TileEntity> allTEs = world.loadedTileEntityList;
		CityBlockTE city = (CityBlockTE) world.getTileEntity(expanding.getCity());
		if(city == null)
			return false;

		int max = city.range;

		int diffX = city.getPos().getX() - expanding.getPos().getX();
		if(max > city.range - Math.abs(diffX))
			max = city.range - Math.abs(diffX);
		int diffZ = city.getPos().getZ() - expanding.getPos().getZ();
		if(max > city.range - Math.abs(diffZ))
			max = city.range - Math.abs(diffZ);
		if(expanding.range + amount > max)
			return false;

		for (TileEntity t : Collections2.filter(allTEs, pPlot)) {
			PlotBlockTE te = (PlotBlockTE) t;
			if(te != expanding){
				int border = te.range + expanding.range + amount;
				diffX = Math.abs(t.getPos().getX() - expanding.getPos().getX());
				diffZ = Math.abs(t.getPos().getZ() - expanding.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					return false;
			}
		}

		return true;
	}

	public static boolean canExpand(World worldObj, CityBlockTE expanding, int amount) {

		List<TileEntity> allTEs = worldObj.loadedTileEntityList;

		for (TileEntity t : Collections2.filter(allTEs, pCity)) {
			CityBlockTE te = (CityBlockTE) t;
			if(te != expanding){
				int border = te.range + expanding.range + amount;
				int diffX = Math.abs(t.getPos().getX() - expanding.getPos().getX());
				int diffZ = Math.abs(t.getPos().getZ() - expanding.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					return false;
			}
		}

		return true;
	}

	public static boolean canExpand(World world, ApartmentBlockTE expanding, int amount) {
		List<TileEntity> allTEs = world.loadedTileEntityList;
		PlotBlockTE plot = (PlotBlockTE) world.getTileEntity(expanding.getPlot());
		if(plot == null)
			return false;

		int max = plot.range;

		int diffX = plot.getPos().getX() - expanding.getPos().getX();
		if(max > plot.range - Math.abs(diffX))
			max = plot.range - Math.abs(diffX);
		int diffZ = plot.getPos().getZ() - expanding.getPos().getZ();
		if(max > plot.range - Math.abs(diffZ))
			max = plot.range - Math.abs(diffZ);
		if(expanding.range + amount > max)
			return false;

		for (TileEntity t : Collections2.filter(allTEs, pApt)) {
			ApartmentBlockTE te = (ApartmentBlockTE) t;
			if(te != expanding){
				int border = te.range + expanding.range + amount;
				diffX = Math.abs(t.getPos().getX() - expanding.getPos().getX());
				diffZ = Math.abs(t.getPos().getZ() - expanding.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					if(expanding.getPos().getY() + expanding.height > te.getPos().getY())
						return false;
			}
		}

		return true;
	}

	public static boolean canExpandY(World world, ApartmentBlockTE expanding, int amount) {
		List<TileEntity> allTEs = world.loadedTileEntityList;
		for (TileEntity t : Collections2.filter(allTEs, pApt)) {
			ApartmentBlockTE te = (ApartmentBlockTE) t;
			if(te != expanding){
				int border = te.range + expanding.range;
				int diffX = Math.abs(t.getPos().getX() - expanding.getPos().getX());
				int diffZ = Math.abs(t.getPos().getZ() - expanding.getPos().getZ());
				if(diffX - border < 0 && diffZ - border < 0)
					if(expanding.getPos().getY() + expanding.height + amount > te.getPos().getY())
						return false;
			}
		}

		return true;
	}
}
