package com.afg.rpmod.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.InventorTable.InventorTableTE;

public class InventorTableGui extends GuiScreen{
	
	private BlockPos pos;
	private static final int PRERESEARCH = 0, COMPLETE = 1, REGULAR = 2;
	private int mode;
	public InventorTableGui(BlockPos pos){
		this.setPos(pos);
	}

	@Override
	public void initGui(){
		TileEntity t = Minecraft.getMinecraft().theWorld.getTileEntity(this.getPos());
		if(t instanceof InventorTableTE){
			InventorTableTE te = (InventorTableTE) t;
			
			if(te.requiredItem == null){
				this.mode = PRERESEARCH;
				this.buttonList.add(new GuiButton(0, this.width / 2 - 60, this.height/2 - 10, 120, 20, "Start Research: $500"));
				//Start research for $$
			} else if(te.completion >= 100){
				this.mode = COMPLETE;
				//Completion button
			} else {
				this.mode = REGULAR;
				//Regular, with research bar and item requirements 
			}
			
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		InventorTableTE te = (InventorTableTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.getPos());
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			switch(button.id){
			case 0: Minecraft.getMinecraft().displayGuiScreen(new DiscoveryListGui(this)); break;
			}
//			RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.getPos()));
//			Minecraft.getMinecraft().displayGuiScreen(new InventorTableGui(this.getPos()));
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(RpMod.guiTextures);

		int i = (this.width - 200) / 2;
		int j = ((this.height - 100) / 2);

		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);


		//		this.drawCenteredString(this.fontRendererObj, this.name, this.width/2, this.height/2 - 45, Color.WHITE.getRGB());
		super.drawScreen(par1, par2, par3);
	}

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}
	
}
