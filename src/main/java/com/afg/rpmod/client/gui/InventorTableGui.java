package com.afg.rpmod.client.gui;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.block.state.IBlockProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.InventorTable.InventorTableContainer;
import com.afg.rpmod.blocks.InventorTable.InventorTableTE;
import com.afg.rpmod.network.UpdateTileEntityServer;

public class InventorTableGui extends GuiContainer{

	private BlockPos pos;
	public static final int GUI_ID = 0;
	private static final int PRERESEARCH = 1, COMPLETE = 2, REGULAR = 3;
	private int mode;
	public InventorTableGui(BlockPos pos, InventoryPlayer invPlayer){
		super(new InventorTableContainer(invPlayer, (InventorTableTE) Minecraft.getMinecraft().theWorld.getTileEntity(pos)));
		this.setPos(pos);

	}

	@Override
	public void initGui(){
		super.initGui();
		TileEntity t = this.mc.theWorld.getTileEntity(this.getPos());
		if(t instanceof InventorTableTE){
			InventorTableTE te = (InventorTableTE) t;

			if(te.requiredItem == null){
				this.mode = PRERESEARCH;
				this.buttonList.add(new GuiButton(0, this.width / 2 - 60, this.height/2 - 60, 120, 20, "Start Research: $500"));
				//Start research for $$
			} else if(te.discoverable != null){
				this.mode = REGULAR;
				this.buttonList.add(new GuiButton(1, this.width / 2 - 60, this.height/2 - 40, 120, 20, "Research Item:       "));
				//Regular, with research bar and item requirements 
			}

		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		InventorTableTE te = (InventorTableTE) this.mc.theWorld.getTileEntity(this.getPos());
		if(te != null){
			NBTTagCompound tag = new NBTTagCompound();
			switch(button.id){
			case 0: this.mc.displayGuiScreen(new DiscoveryListGui(this)); break;
			case 1: 
				if(this.mc.thePlayer.inventory.getItemStack() != null)
					if(this.mc.thePlayer.inventory.getItemStack().isItemEqual(te.requiredItem))
						if(this.mc.thePlayer.inventory.getItemStack().stackSize >= te.requiredItem.stackSize){
							this.mc.thePlayer.inventory.getItemStack().stackSize -= te.requiredItem.stackSize;
							if(this.mc.thePlayer.inventory.getItemStack().stackSize <= 0)
								this.mc.thePlayer.inventory.setItemStack(null);
							tag.setBoolean("research", true);
							RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.getPos()));
						}
				break;
			}

		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawScreen(par1, par2, par3);
	}

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int i = (this.width - 200) / 2;
		int j = ((this.height - 200) / 2);

		if(this.mode == REGULAR){
			TileEntity t = this.mc.theWorld.getTileEntity(this.getPos());
			if(t instanceof InventorTableTE){
				InventorTableTE te = (InventorTableTE) t;
				if(te.discoverable == null || te.requiredItem == null){
					this.mc.displayGuiScreen(null);
					return;
				}
				GlStateManager.color(1.0f, 1.0f, 1.0f);
				this.drawCenteredString(this.fontRendererObj, "Currently Inventing: ",	89, -10, Color.white.getRGB());
				GlStateManager.pushMatrix();
				GlStateManager.translate(10, 5, 0);
				GlStateManager.scale(1.5, 1.5, 1.5);
				this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(te.discoverable.getDisplayItem(), 1), 0, 0);
				this.drawString(this.fontRendererObj, I18n.format(te.discoverable.getName() + ".name"), 20, 4, Color.white.getRGB());
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.translate(120, 45, 0);
				GlStateManager.scale(0.9, 0.9, 0.9);
				GlStateManager.color(1.0f, 1.0f, 1.0f);
				this.itemRender.renderItemAndEffectIntoGUI(te.requiredItem, 0, 0);
				this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, new ItemStack(te.discoverable.getDisplayItem(), 1), 0, 0, "" + te.requiredItem.stackSize);
				GlStateManager.disableLighting();
				GlStateManager.scale(0.8, 0.8, 0.8);
//				this.drawCenteredString(this.fontRendererObj, "(" + te.requiredItem.getDisplayName() + ")", -45, 30, Color.white.getRGB());
				this.fontRendererObj.drawString("(" + te.requiredItem.getDisplayName() + ")", (float)(-45 - this.fontRendererObj.getStringWidth("(" + te.requiredItem.getDisplayName() + ")") / 2), (float)30, Color.black.getRGB(), false);
				GlStateManager.color(1.0f, 1.0f, 1.0f);
				GlStateManager.popMatrix();
			
				this.mc.renderEngine.bindTexture(RpMod.guiTextures);
			
				
				this.drawTexturedModalRect(160, 0, 200, 0, 5, 62);
				double shadeHeight = (62.0/100.0)*(double) te.completion;
				this.drawTexturedModalRect(160, 62 - (int) shadeHeight, 205, 62 - (int) (shadeHeight), 5, (int) shadeHeight);
				GlStateManager.enableLighting();
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,
			int mouseX, int mouseY) {
		this.mc.renderEngine.bindTexture(RpMod.guiTextures);
		//		this.drawDefaultBackground();
		int i = (this.width - 200) / 2;
		int j = ((this.height - 200) / 2);

		this.drawTexturedModalRect(i, j, 0, 0, 200, 100);
		ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		i = this.guiLeft;
		j = this.guiTop;
		this.drawTexturedModalRect(i, j + 83, 0, 126, this.xSize, 100);
		this.drawTexturedModalRect(i, j + 83, 0, 0, this.xSize, 10);
	}

}
