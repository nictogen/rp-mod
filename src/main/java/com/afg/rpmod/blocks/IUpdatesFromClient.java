package com.afg.rpmod.blocks;

import net.minecraft.nbt.NBTTagCompound;

public interface IUpdatesFromClient {

	public void updateServerData(NBTTagCompound tag);
}
