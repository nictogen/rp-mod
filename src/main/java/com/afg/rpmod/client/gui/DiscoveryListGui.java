package com.afg.rpmod.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.blocks.InventorTable.InventorTableTE;
import com.afg.rpmod.jobs.Inventor;
import com.afg.rpmod.jobs.Inventor.EnumDiscoverableType;
import com.afg.rpmod.network.UpdateTileEntityServer;

public class DiscoveryListGui extends GuiScreen {
	public int page = 0, maxPage;
	public InventorTableGui table;
	public DiscoveryListGui(){}
	public DiscoveryListGui(InventorTableGui table){
		this.table = table;
	}

	@Override
	public void initGui(){
		int i = (this.width - 200) / 2;
		int j = ((this.height - 180) / 2);

		this.buttonList.add(new PageButton(0, i + 10, this.height/2, null, true));
		this.buttonList.add(new PageButton(1, i + 180, this.height/2, null, false));
		int y = j + 13;
		int page = 0;
		int buttonID = 1;
		for(Item item : Inventor.getDiscoverableItems()){
			if(y > j + 150){
				y = j + 13;
				page++;
			}
			this.buttonList.add(new DiscoveryButton(buttonID++, this.width / 2 - 70, y, 140, 20, item.getUnlocalizedName(), new ItemStack(item, 1), page));
			y += 22;
		}
		this.maxPage = page;
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		for(GuiButton b : this.buttonList){
			if(b instanceof DiscoveryButton){
				b.visible = ((DiscoveryButton) b).page == this.page;
			}
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch(button.id){
		case 0: this.page--; break;
		case 1: this.page++; break;
		}
		if(button.id > 1){
			InventorTableTE te = (InventorTableTE) Minecraft.getMinecraft().theWorld.getTileEntity(this.table.getPos());
			EnumDiscoverableType discoverable = null;
			for(EnumDiscoverableType d : Inventor.discoverables)
				if(d.getName() == this.buttonList.get(button.id - 2).displayString)
					discoverable = d;
			if(te != null && discoverable != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("purchase", discoverable.getName());
				RpMod.networkWrapper.sendToServer(new UpdateTileEntityServer(tag, this.table.getPos()));
				Minecraft.getMinecraft().displayGuiScreen(new InventorTableGui(this.table.getPos()));
			}
		}
		if(this.page < 0)
			this.page = 0;
		if(this.page > this.maxPage)
			this.page = this.maxPage;
		
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(RpMod.guiTextures);

		int i = (this.width - 200) / 2;
		int j = ((this.height - 180) / 2);

		//Background(lower than it should be)
		this.drawTexturedModalRect(i, j + 80, 0, 0, 200, 100);
		//Foreground(shorter than it should be)
		//Creates a longer box without making new texture
		this.drawTexturedModalRect(i, j, 0, 0, 200, 90);

		//		this.drawCenteredString(this.fontRendererObj, this.name, this.width/2, this.height/2 - 45, Color.WHITE.getRGB());
		super.drawScreen(par1, par2, par3);
	}

	public static class DiscoveryButton extends GuiButton{
		private ItemStack stack;
		private int page;
		public DiscoveryButton(int buttonId, int x, int y, int widthIn,
				int heightIn, String buttonText, ItemStack stack, int page) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
			this.stack = stack;
			this.page = page;
		}


		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
				this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.xPosition + 3, this.yPosition + 2, 0);
				GlStateManager.scale(0.8, 0.8, 1.0);
				mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer, this.stack, 0, 0);
				GlStateManager.popMatrix();
				this.mouseDragged(mc, mouseX, mouseY);
				int j = 14737632;

				if (packedFGColour != 0)
				{
					j = packedFGColour;
				}
				else
					if (!this.enabled)
					{
						j = 10526880;
					}
					else if (this.hovered)
					{
						j = 16777120;
					}

				GlStateManager.pushMatrix();
				GlStateManager.translate(this.xPosition + 20, this.yPosition + (this.height - 8) / 2, 0);
				GlStateManager.scale(0.8, 0.8, 1.0);
				
				String name = I18n.format(this.displayString + ".name");
				if(name.length() > 15)
					name = name.substring(0, 15);
				this.drawString(fontrenderer, "Research: " + name, 0, 0, j);
				GlStateManager.popMatrix();
			}
		}
	}

	public static class PageButton extends GuiButton{
		private boolean left;

		public PageButton(int buttonId, int x, int y, ItemStack stack, boolean left) {
			super(buttonId, x, y - 10, 14, 22, "");
			this.left = left;
		}


		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				FontRenderer fontrenderer = mc.fontRendererObj;
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered) - 1;

				if(this.left){
					GlStateManager.translate(this.xPosition + 10, this.yPosition + 10, 0);
					GlStateManager.rotate(180, 0.0f, 0.0f, 1.0f);
					GlStateManager.translate(0, -10f, 0);
				} else {
					GlStateManager.translate(this.xPosition, this.yPosition	- 3, 0);
				}

				ResourceLocation loc = new ResourceLocation("textures/gui/world_selection.png");
				mc.getTextureManager().bindTexture(loc);
				this.drawTexturedModalRect(0, 0, 10, 4 + i*32, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);

				GlStateManager.popMatrix();
			}
		}
	}
}
