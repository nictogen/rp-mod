package com.afg.rpmod.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.afg.rpmod.blocks.InventorTable.InventorTableContainer;
import com.afg.rpmod.blocks.InventorTable.InventorTableTE;
import com.afg.rpmod.client.gui.InventorTableGui;

public class GuiHandler implements IGuiHandler {

	public static final int inventorTableGuiID = InventorTableGui.GUI_ID;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		InventoryPlayer inv = new InventoryPlayer(player);
		switch(ID){
		case 0:
			TileEntity t = world.getTileEntity(new BlockPos(x, y, z));
			if(t instanceof InventorTableTE)
				return new InventorTableContainer(player.inventory, (InventorTableTE) t);
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		InventoryPlayer inv = new InventoryPlayer(player);
		switch(ID){
		case 0: return new InventorTableGui(new BlockPos(x, y, z), player.inventory);
		}
		return null;
	}

}
