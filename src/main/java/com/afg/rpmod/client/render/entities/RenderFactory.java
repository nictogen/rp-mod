package com.afg.rpmod.client.render.entities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.afg.rpmod.entities.NPCJobGiver;

public class RenderFactory<T extends Entity> implements IRenderFactory<T> {


	Class<T> entity;
	public RenderFactory(Class<T> entity){
		this.entity = entity;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Render<T> createRenderFor(RenderManager manager) {
		if(this.entity == NPCJobGiver.class){
			return (Render<T>) new RenderNPC(manager);
		}
		return null;
	}

}