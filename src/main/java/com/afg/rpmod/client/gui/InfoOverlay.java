package com.afg.rpmod.client.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentDoor;
import com.afg.rpmod.blocks.ApartmentDoor.ApartmentDoorTE;
import com.afg.rpmod.blocks.InventorTable;
import com.afg.rpmod.blocks.InventorTable.InventorTableTE;
import com.afg.rpmod.capabilities.IPlayerData;

public class InfoOverlay extends Gui {


	public void doRender() {
		IPlayerData data = Minecraft.getMinecraft().thePlayer.getCapability(IPlayerData.PLAYER_DATA, null);
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		int screenwidth = resolution.getScaledWidth();
		int screenheight = resolution.getScaledHeight();

		if(data != null && data.getJob() != null){
			String[] info = {"Money: $" + data.getMoney(), "Bank Money: $" + data.getBankMoney(), "Job: " + data.getJob().getName(), "Salary: $" + data.getJob().getIncome(), "Level: " + data.getJobLvl(), "XP: " + data.getJobXP()};
			for(int i = 0; i < info.length; i++)
				this.drawString(Minecraft.getMinecraft().fontRendererObj, info[i], 0, i*10, 0x00FF00);
		}
		RayTraceResult hit = Minecraft.getMinecraft().thePlayer.rayTrace(3, 1.0f);
		if(hit.typeOfHit == RayTraceResult.Type.BLOCK){
			String s = "";
			Block b = Minecraft.getMinecraft().theWorld.getBlockState(hit.getBlockPos()).getBlock();
			if(b instanceof ApartmentDoor){
				ApartmentDoorTE te = ((ApartmentDoor) RpMod.Blocks.apartmentDoor).getTE(Minecraft.getMinecraft().theWorld, hit.getBlockPos());
				if(te != null && te.getApartment() != null)
					s = te.getApartment().getName();
				
			} else if (b instanceof InventorTable){
				InventorTableTE te = ((InventorTable) RpMod.Blocks.inventorTableStone).getTE(Minecraft.getMinecraft().theWorld, hit.getBlockPos());
				if(te.getPlayer() != null)
					s = te.getPlayer().getName() + "'s Inventor Table";
			}
			this.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, s, screenwidth/2, screenheight/2 - 15, 0x00FF00);
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.EXPERIENCE)
			this.doRender();
	}
}
