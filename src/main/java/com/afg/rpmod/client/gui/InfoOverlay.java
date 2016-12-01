package com.afg.rpmod.client.gui;

import org.fusesource.jansi.Ansi.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.afg.rpmod.IPlayerData;

public class InfoOverlay extends Gui {


	public void doRender() {
		IPlayerData data = Minecraft.getMinecraft().thePlayer.getCapability(IPlayerData.PLAYER_DATA, null);
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		String[] info = {"Money: $" + data.getMoney(), "Bank Money: $" + data.getBankMoney()};
		if(data != null)
			for(int i = 0; i < info.length; i++)
				this.drawString(Minecraft.getMinecraft().fontRendererObj, info[i], 0, i*10, 0x00FF00);

	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.EXPERIENCE)
			this.doRender();
	}
}