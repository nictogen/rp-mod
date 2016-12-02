package com.afg.rpmod.client.gui;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.afg.rpmod.network.UpdateTileEntityServer;

public class PlotGui extends GuiScreen {
	private GuiTextField text;
	private GuiTextField text2;
	private String owner = "";
	private ResourceLocation location = new ResourceLocation(RpMod.MODID, "textures/gui/background.png");
	private BlockPos pos;

	public PlotGui(BlockPos pos){
		this.pos = pos;
	}

	@Override
	public void initGui(){
		this.text = new GuiTextField(0, this.fontRendererObj, this.width/2 - 75, this.height/2 - 15, 110, 20);
		text.setMaxStringLength(23);
		this.text.setFocused(true);
		PlotBlockTE te = (PlotBlockTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te != null)
			this.owner = te.getName();
		this.buttonList.add(new GuiButton(0, this.width / 2 + 30, this.height/2 + 18, 20, 20, "+"));
		this.text2 = new GuiTextField(0, this.fontRendererObj, this.width/2 - 50, this.height/2 + 18, 30, 20);
		this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height/2 + 18, 20, 20, "-"));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 31, this.height/2 - 20, 60, 20, "Set Owner"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		PlotBlockTE te = (PlotBlockTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			int amount = 1;
			if(Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
				amount = 5;
			switch(button.id){
			case 0:
				tag.setInteger("range", te.range + amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 1: 
				tag.setInteger("range", te.range - amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 2: 
				tag.setString("playername", text.getText());
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			}
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		super.keyTyped(par1, par2);
		if(par2 == Keyboard.KEY_RETURN){
			Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		}
		this.text.textboxKeyTyped(par1, par2);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(location);
		PlotBlockTE te = (PlotBlockTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.pos);
		if(te != null){
			this.owner = te.getName();
			this.text2.setText("" + te.range);
		}
		int i = (this.width - 200) / 2;
		int j = (this.height - 100) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);
		this.text.xPosition = this.width/2 - 85;
		this.text.yPosition = this.height/2 - 20;
		this.text.drawTextBox();
		this.text2.xPosition = this.width/2 - this.text2.width/2;
		this.text2.yPosition = this.height/2 + 18;
			
		this.text2.drawTextBox();	
		this.drawCenteredString(this.fontRendererObj, "Plot Block Settings", this.width/2, this.height/2 - 45, new Color(255,255,255).getRGB());
		this.drawCenteredString(this.fontRendererObj, "Range", this.width/2, this.height/2 + 7, new Color(255,255,255).getRGB());
		this.drawCenteredString(this.fontRendererObj, "Owner: " + this.owner, this.width/2, this.height/2 - 34, new Color(90,90,255).getRGB());
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		this.text.mouseClicked(x, y, btn);
	}
}
