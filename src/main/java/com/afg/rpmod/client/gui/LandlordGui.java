package com.afg.rpmod.client.gui;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.ApartmentBlock;
import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.network.UpdateTileEntityServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class LandlordGui extends GuiScreen {
	protected GuiTextField buyCost, rentCost, yRange, xzRange;
	protected String owner = "";
	protected BlockPos pos;
	protected String name;
	private boolean rented = false;
	public LandlordGui(BlockPos pos){
		this.pos = pos;
		this.name = "Landlord Settings";
	}

	@Override
	public void initGui(){
		TileEntity t = Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(t instanceof ApartmentBlockTE){
			ApartmentBlockTE te = (ApartmentBlockTE) t;
			this.buyCost = new GuiTextField(0, this.fontRenderer, 0, 0, 110, 20);
			buyCost.setMaxStringLength(9);
			this.buyCost.setFocused(true);

			this.rentCost = new GuiTextField(0, this.fontRenderer, 0, 0, 110, 20);
			rentCost.setMaxStringLength(9);

			this.buttonList.add(new GuiButton(0, this.width / 2 - 90, this.height/2 + 32, 20, 20, "-"));
			this.yRange = new GuiTextField(0, this.fontRenderer, 0, 0, 30, 20);
			this.buttonList.add(new GuiButton(1, this.width / 2 - 30, this.height/2 + 32, 20, 20, "+"));

			this.buttonList.add(new GuiButton(2, this.width / 2 + 10, this.height/2 + 32, 20, 20, "-"));
			this.xzRange = new GuiTextField(0, this.fontRenderer, 0, 0, 30, 20);
			this.buttonList.add(new GuiButton(3, this.width / 2 + 70, this.height/2 + 32, 20, 20, "+"));



			this.buyCost.setText("" + te.getCost());
			this.rentCost.setText("" + te.getRent());
			this.yRange.setText("" + te.height);
			this.xzRange.setText("" + te.range);


			if(te.playername != ApartmentBlock.EMPTY){
				this.buyCost.setVisible(false);
				this.rentCost.setVisible(false);
				this.rented = true;
				this.buttonList.add(new GuiButton(4, this.width / 2 - 70, this.height/2 - 20, 140, 20, "Evict Tenant: " + te.playername));
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		ApartmentBlockTE te = (ApartmentBlockTE) Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			int amount = 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				amount = 5;
			switch(button.id){
			case 0:
				tag.setInteger("height", te.height - amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 1: 
				tag.setInteger("height", te.height + amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 2:
				tag.setInteger("range", te.range - amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 3: 
				tag.setInteger("range", te.range + amount);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				break;
			case 4: 
				tag.setBoolean("evict", true);
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
				Minecraft.getMinecraft().displayGuiScreen(null);
				break;
			}
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		super.keyTyped(par1, par2);
		char[] validNum = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		boolean type = false;
		for(char c : validNum){
			if(par1 == c)
				type = true;
		}
		if(type == true || Keyboard.KEY_BACK == par2 && !this.rented){
			this.buyCost.textboxKeyTyped(par1, par2);
			this.rentCost.textboxKeyTyped(par1, par2);
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te instanceof ApartmentBlockTE){
			NBTTagCompound tag = new NBTTagCompound();
			if(!this.buyCost.getText().isEmpty() && Integer.parseInt(this.buyCost.getText()) != ((ApartmentBlockTE) te).getCost()){
				tag.setBoolean("costChange", true);
				tag.setInteger("cost", Integer.parseInt(this.buyCost.getText()));
			}

			if(!this.rentCost.getText().isEmpty() && Integer.parseInt(this.rentCost.getText()) != ((ApartmentBlockTE) te).getRent()){
				tag.setBoolean("rentChange", true);
				tag.setInteger("rentCost", Integer.parseInt(this.rentCost.getText()));
			}

			if(!tag.hasNoTags()){
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.pos));
			}
			this.yRange.setText("" + ((ApartmentBlockTE) te).height);
			this.xzRange.setText("" + ((ApartmentBlockTE) te).range);
		}
		this.buyCost.updateCursorCounter();
		this.rentCost.updateCursorCounter();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(RpMod.guiTextures);

		int i = (this.width - 200) / 2;
		int j = ((this.height - 100) / 2);
		//Background(a bit lower than it should be)
		this.drawTexturedModalRect(i, j + 13, 0, 0, 200, 100);
		//Foreground(a bit shorter than it should be)
		//Creates a longer box without making new texture
		this.drawTexturedModalRect(i, j, 0, 0, 200, 90);

		this.buyCost.x = this.width/2 - 40;
		this.buyCost.y = this.height/2 - 32;
		this.buyCost.drawTextBox();
		if(!this.rented)
			this.fontRenderer.drawStringWithShadow("Buy Cost", this.width/2 - 93, this.height/2 - 27, Color.WHITE.getRGB());

		this.rentCost.x = this.width/2 - 40;
		this.rentCost.y = this.height/2 - 8;
		this.rentCost.drawTextBox();
		if(!this.rented)
			this.fontRenderer.drawStringWithShadow("Rent Cost", this.width/2 - 93, this.height/2 - 3, Color.WHITE.getRGB());

		this.yRange.x = this.width/2 - 65;
		this.yRange.y = this.height/2 + 32;
		this.yRange.drawTextBox();	

		this.xzRange.x = this.width/2 + 35;
		this.xzRange.y = this.height/2 + 32;
		this.xzRange.drawTextBox();

		this.drawCenteredString(this.fontRenderer, this.name, this.width/2, this.height/2 - 45, Color.WHITE.getRGB());
		this.drawCenteredString(this.fontRenderer, "Height", this.width/2 - 50, this.height/2 + 20, Color.WHITE.getRGB());
		this.drawCenteredString(this.fontRenderer, "Length", this.width/2 + 50, this.height/2 + 20, Color.WHITE.getRGB());
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		if(!this.rented){
			this.buyCost.mouseClicked(x, y, btn);
			this.rentCost.mouseClicked(x, y, btn);
		}
	}
}