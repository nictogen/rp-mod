package com.afg.rpmod.client.render.entities;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.entities.EntityNPC;

public class RenderNPC extends RenderLivingBase<EntityNPC>
{
	private static final ResourceLocation JOB_GIVER_TEXTURE = new ResourceLocation(RpMod.MODID, "textures/entities/dog.png");

	public RenderNPC(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelVillager(0.0F), 0.5F);
		this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
	}

	public ModelVillager getMainModel()
	{
		return (ModelVillager)super.getMainModel();
	}

	/**
	 * Allows the render to do state modifications necessary before the model is rendered.
	 */
	protected void preRenderCallback(EntityNPC entitylivingbaseIn, float partialTickTime)
	{
		float f = 0.9375F;

		this.shadowSize = 0.5F;

		GlStateManager.scale(f, f, f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityNPC entity) {
		return JOB_GIVER_TEXTURE;
	}
}