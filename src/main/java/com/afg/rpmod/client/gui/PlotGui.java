package com.afg.rpmod.client.gui;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.afg.rpmod.network.UpdateTileEntityServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class PlotGui extends LandGui {

	public PlotGui(BlockPos pos) {
		super(pos);
		this.name = "Plot Block Settings";
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te instanceof PlotBlockTE){
			this.owner = ((PlotBlockTE) te).getName();
			this.text2.setText("" + ((PlotBlockTE) te).range);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		PlotBlockTE te = (PlotBlockTE) Minecraft.getMinecraft().world.getTileEntity(this.pos);
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			int amount = 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
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
}
