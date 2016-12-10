package com.afg.rpmod.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import com.afg.rpmod.client.gui.InfoOverlay;
import com.afg.rpmod.client.render.entities.RenderFactory;
import com.afg.rpmod.entities.NPCJobGiver;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new InfoOverlay());
	}

	@Override
	public void registerRenders(){
		RenderingRegistry.registerEntityRenderingHandler(NPCJobGiver.class, new RenderFactory(NPCJobGiver.class));
	}

}
