package com.afg.rpmod.client.gui;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.afg.rpmod.network.UpdateTileEntityServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.io.IOException;

public class PurchaseGui extends GuiScreen {
	private BlockPos pos;
	private String name;
	private String owner;
	public PurchaseGui(BlockPos pos){
		this.pos = pos;
	}

	@Override
	public void initGui(){
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te instanceof ApartmentBlockTE){

			if(((ApartmentBlockTE) te).getRent() != 0)
				this.buttonList.add(new GuiButton(0, this.width / 2 - 90, this.height/2 + 18, 180, 20, "Rent: $" + ((ApartmentBlockTE)te).getRent() + " per MC week played"));
			if(((ApartmentBlockTE) te).getCost() != 0)
				this.buttonList.add(new GuiButton(1, this.width / 2 - 90, this.height/2 - 10, 180, 20, "Buy: $" + ((ApartmentBlockTE)te).getCost()));

			te = ((ApartmentBlockTE) te).getPlotTE();
			this.owner = ((PlotBlockTE) te).getName();
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		ApartmentBlockTE te = (ApartmentBlockTE) Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te != null){
			if(te.getPlayer() == Minecraft.getMinecraft().player)
				Minecraft.getMinecraft().displayGuiScreen(new ApartmentInfoGui(this.pos));
		}
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		ApartmentBlockTE te = (ApartmentBlockTE) Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			switch(button.id){
			case 0: 
				tag.setString("rent", Minecraft.getMinecraft().player.getName());
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 1:
				tag.setString("purchase", Minecraft.getMinecraft().player.getName());
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			}
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		super.keyTyped(par1, par2);
	}


	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(RpMod.guiTextures);

		int i = (this.width - 200) / 2;
		int j = (this.height - 100) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);

		this.drawCenteredString(this.fontRenderer, "Purchase", this.width/2 - 23, this.height/2 - 45, Color.green.getRGB());
		this.drawCenteredString(this.fontRenderer, "or", this.width/2 + 12, this.height/2 - 45, Color.white.getRGB());
		this.drawCenteredString(this.fontRenderer, "Rent", this.width/2 + 34, this.height/2 - 44, Color.BLUE.getRGB());
		this.drawCenteredString(this.fontRenderer, "Landlord: " + this.owner, this.width/2, this.height/2 - 34, Color.WHITE.getRGB());
		super.drawScreen(par1, par2, par3);
	}
}
