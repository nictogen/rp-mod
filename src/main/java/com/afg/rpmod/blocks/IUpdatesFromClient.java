package com.afg.rpmod.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IUpdatesFromClient {

	public void updateServerData(NBTTagCompound tag);
	
	public boolean isApprovedPlayer(EntityPlayer player);
}
