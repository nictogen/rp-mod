package com.afg.rpmod.client.gui;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentBlock;
import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.blocks.ApartmentDoor.ApartmentDoorTE;
import com.afg.rpmod.network.UpdateTileEntityServer;

public class DoorGui extends GuiScreen {

	protected ResourceLocation location = new ResourceLocation(RpMod.MODID, "textures/gui/background.png");

	protected BlockPos pos;
	public DoorGui(BlockPos pos){
		this.pos = pos;
	}

	@Override
	public void initGui(){
		TileEntity t = Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(t instanceof ApartmentDoorTE){
			ApartmentDoorTE te = (ApartmentDoorTE) t;
			if(te.isLocked())
				this.buttonList.add(new GuiButton(0, this.width / 2 - 40, this.height/2 - 10, 80, 20, "Unlock Door"));
			else
				this.buttonList.add(new GuiButton(1, this.width / 2 - 40, this.height/2 - 10, 80, 20, "Lock Door"));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		ApartmentDoorTE te = (ApartmentDoorTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			switch(button.id){
			case 0:
				tag.setBoolean("locked", false);
				break;
			case 1: 
				tag.setBoolean("locked", true);
				break;
			}
			RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}


	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(location);
		int i = (this.width - 200) / 2;
		int j = ((this.height - 100) / 2);
		
		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);

		this.drawCenteredString(this.fontRendererObj, "Door Settings", this.width/2, this.height/2 - 45, Color.WHITE.getRGB());
		super.drawScreen(par1, par2, par3);
	}

}
