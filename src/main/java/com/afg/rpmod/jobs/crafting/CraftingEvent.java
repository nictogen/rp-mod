package com.afg.rpmod.jobs.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;


public class CraftingEvent extends Event{
	private ItemStack stack;
	private EntityPlayer player;

	public CraftingEvent(ItemStack stack, EntityPlayer player){
		this.player = player;
		this.stack = stack;
	}

	public boolean isCancelable()
	{
		return true;
	}

	public ItemStack getStack(){
		return this.stack;
	}

	public EntityPlayer getPlayer(){
		return this.player;
	}
}