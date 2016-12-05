package com.afg.rpmod.jobs.crafting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CancelableShapelessRecipe extends ShapelessRecipes {

	public CancelableShapelessRecipe(ItemStack output, List<ItemStack> inputList) {
		super(output, inputList);
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
				return !MinecraftForge.EVENT_BUS.post(event);
			}
		}
		return matches;
	}
}
