package com.afg.rpmod.client.render.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.afg.rpmod.blocks.CookPan.CookPanTE;

public class CookPanTESR extends TileEntitySpecialRenderer<CookPanTE> {

	@Override
	public void renderTileEntityAt(CookPanTE te, double x, double y, double z, float partialTicks, int destroyStage) {
		ItemStack stack = te.getCooking();
		if (stack != null && stack.getItem() != null) {
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.translate(0.5, 1, 0.5);
			GlStateManager.scale(0.25f, 0.25f, 0.25f);
			this.renderFire(partialTicks);
			GlStateManager.translate(-0.2, -2, 0.15);
			GlStateManager.rotate(90, 1.0f, 0.0f, 0.0f);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

	}

	private void renderFire(float partialTicks)
	{
		GlStateManager.disableLighting();
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
		TextureAtlasSprite textureatlassprite1 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f5 = 0.0F;
		int i = 0;
		float f1 = 1;
		float f4 = 4;
		for(int r = 0; r < 8; r++){
			GlStateManager.pushMatrix();
			GlStateManager.rotate(r*45, 0.0F, 1.0F, 0.0F);
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			float f6 = textureatlassprite2.getMinU();
			float f7 = textureatlassprite2.getMinV();
			float f8 = textureatlassprite2.getMaxU();
			float f9 = textureatlassprite2.getMaxV();

			if (i / 2 % 2 == 0)
			{
				float f10 = f8;
				f8 = f6;
				f6 = f10;
			}

			vertexbuffer.pos((double)(f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f8, (double)f9).endVertex();
			vertexbuffer.pos((double)(-f1 - 0.0F), (double)(0.0F - f4), (double)f5).tex((double)f6, (double)f9).endVertex();
			vertexbuffer.pos((double)(-f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f6, (double)f7).endVertex();
			vertexbuffer.pos((double)(f1 - 0.0F), (double)(1.4F - f4), (double)f5).tex((double)f8, (double)f7).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
		GlStateManager.enableLighting();
	}


}