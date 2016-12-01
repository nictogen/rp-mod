package com.afg.rpmod.proxy;

import net.minecraftforge.common.MinecraftForge;

import com.afg.rpmod.client.gui.InfoOverlay;

public class ClientProxy extends CommonProxy {
	
	public void registerEventHandlers() {
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new InfoOverlay());
	}
}
