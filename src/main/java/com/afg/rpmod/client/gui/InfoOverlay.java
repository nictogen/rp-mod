package com.afg.rpmod.client.gui;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentDoor;
import com.afg.rpmod.blocks.ApartmentDoor.ApartmentDoorTE;
import com.afg.rpmod.blocks.CookPan;
import com.afg.rpmod.blocks.CookPan.CookPanTE;
import com.afg.rpmod.capabilities.IPlayerData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InfoOverlay extends Gui {


	public void doRender() {
		IPlayerData data = Minecraft.getMinecraft().player.getCapability(IPlayerData.PLAYER_DATA, null);
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		int screenwidth = resolution.getScaledWidth();
		int screenheight = resolution.getScaledHeight();

		if(data != null && data.getJob() != null){
			String[] info = {"Money: $" + data.getMoney(), "Bank Money: $" + data.getBankMoney(), "Job: " + data.getJob().getName(), "Salary: $" + data.getJob().getIncome(), "Level: " + data.getJobLvl(), "XP: " + data.getJobXP()};
			for(int i = 0; i < info.length; i++)
				this.drawString(Minecraft.getMinecraft().fontRenderer, info[i], 0, i*10, 0x00FF00);
		}
		RayTraceResult hit = Minecraft.getMinecraft().player.rayTrace(3, 1.0f);
		if(hit.typeOfHit == RayTraceResult.Type.BLOCK){
			String s = "";
			Block b = Minecraft.getMinecraft().world.getBlockState(hit.getBlockPos()).getBlock();
			if(b instanceof ApartmentDoor){
				ApartmentDoorTE te = ((ApartmentDoor) RpMod.Blocks.apartmentDoor).getTE(Minecraft.getMinecraft().world, hit.getBlockPos());
				if(te != null && te.getApartment() != null)
					s = te.getApartment().getName();
				
			} else if (b instanceof CookPan){
				CookPanTE te = ((CookPan) RpMod.Blocks.cookPan).getTE(Minecraft.getMinecraft().world, hit.getBlockPos());
				if(te.getPlayer() != null)
					s = te.getPlayer().getName() + "'s Cooking Pan";
			}
			this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, s, screenwidth/2, screenheight/2 - 15, 0x00FF00);
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.EXPERIENCE)
			this.doRender();
	}
}
