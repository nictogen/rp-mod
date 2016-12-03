package com.afg.rpmod.client.gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.network.UpdateTileEntityServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ApartmentInfoGui extends GuiScreen {
	private GuiTextField name;
	private String landlord, rent, xz, y, time;
	private boolean renting;
	private ResourceLocation location = new ResourceLocation(RpMod.MODID, "textures/gui/background.png");
	private BlockPos pos;

	public ApartmentInfoGui(BlockPos pos){
		this.pos = pos;
	}

	@Override
	public void initGui(){
		this.name = new GuiTextField(0, this.fontRendererObj, 0, 0, 110, 20);
		name.setMaxStringLength(23);
		this.name.setFocused(true);

		TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te instanceof ApartmentBlockTE){
			ApartmentBlockTE t = (ApartmentBlockTE) te;
			this.name.setText("" + t.getName());
			this.renting = t.isRenting();
			this.rent = "" + t.getRent();
			this.xz = "Length: " + t.range;
			this.y = "Height: " + t.height;
		}
	}


	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		super.keyTyped(par1, par2);
		this.name.textboxKeyTyped(par1, par2);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te instanceof ApartmentBlockTE){
			NBTTagCompound tag = new NBTTagCompound();
			if(!this.name.getText().isEmpty() && ((ApartmentBlockTE) te).getName() != this.name.getText()){
				tag.setString("name", this.name.getText());
			}
			if(!tag.hasNoTags()){
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
			}
		}
		IPlayerData data = Minecraft.getMinecraft().thePlayer.getCapability(IPlayerData.PLAYER_DATA, null);
		if(data != null){
			int ticks = (168000 - data.getTotalPlaytime() % 168000);
			int hours = ticks/20/60/60;
			int minutes = ticks/20/60 % 60;
			int seconds = ticks/20 % 60;
			this.time = "" + hours + "h" + minutes + "m" + seconds + "s"; 
		}
		this.name.updateCursorCounter();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(location);

		int i = (this.width - 200) / 2;
		int j = ((this.height - 100) / 2);
		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);
		
		this.name.xPosition = this.width/2 - 40;
		this.name.yPosition = this.height/2 - 32;
		this.name.drawTextBox();
		this.fontRendererObj.drawStringWithShadow("Apt Name: ", this.width/2 - 93, this.height/2 - 27, Color.WHITE.getRGB());
		
		this.drawCenteredString(this.fontRendererObj, "Apartment Info", this.width/2, this.height/2 - 45, Color.WHITE.getRGB());
		this.drawCenteredString(this.fontRendererObj, this.xz, this.width/2 - 60, this.height/2, Color.WHITE.getRGB());
		this.drawCenteredString(this.fontRendererObj, this.y , this.width/2 - 60, this.height/2 + 20, Color.WHITE.getRGB());
		if(this.renting){
			this.drawCenteredString(this.fontRendererObj, "Time until next payment: ", this.width/2 + 33, this.height/2, Color.WHITE.getRGB());
			this.drawCenteredString(this.fontRendererObj, time + "", this.width/2 + 33, this.height/2 + 15, Color.WHITE.getRGB());
			this.drawCenteredString(this.fontRendererObj, "Payment: $" + this.rent, this.width/2 + 33, this.height/2 + 29, Color.green.getRGB());
		}
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		this.name.mouseClicked(x, y, btn);
	}
}
