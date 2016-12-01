package com.afg.rpmod.proxy;

import net.minecraftforge.common.MinecraftForge;

import com.afg.rpmod.handlers.JobEventHandler;

public class CommonProxy {
	
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new JobEventHandler());
	}
}
