package com.afg.rpmod.jobs.crafting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CancelableShapedOreRecipe extends ShapedOreRecipe {


	public CancelableShapedOreRecipe(ShapedOreRecipe r) {
		//Have to have a temporary recipe until we just manually set the input (Which is dumb)
		super(r.getRecipeOutput(), "AAA", "AAA", "AAA", 'A', Items.COOKIE);
		this.input = r.getInput(); //Overwriting super constructor cookies
		this.width = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, r, 4);
		this.height = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, r, 5);
	}


	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		boolean matches = super.matches(inv, worldIn);
		if(matches){
			EntityPlayer player = null;
			Container c = ReflectionHelper.getPrivateValue(InventoryCrafting.class, inv, 3);
			if(c instanceof ContainerPlayer){
				player = ReflectionHelper.getPrivateValue(ContainerPlayer.class, (ContainerPlayer) c, 4);
			} else if(c instanceof ContainerWorkbench){
				Slot slot = c.getSlot(0);
				if(slot instanceof SlotCrafting){
					player = ReflectionHelper.getPrivateValue(SlotCrafting.class, (SlotCrafting)slot, 1);
				}
			}
			if(player != null){
				CraftingEvent event = new CraftingEvent(this.getRecipeOutput(), player);
				matches = !MinecraftForge.EVENT_BUS.post(event);
			}
		}
		return matches;
	}
}
