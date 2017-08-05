package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class ToolCraftsman extends Job {

	public ToolCraftsman(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 3 + getData().getJobLvl();
	}

	@Override
	public String getName() {
		return "Tool Craftsman";
	}
	
	@Override
	public Item[] getAvailableRecipes() {
		switch(this.getData().getJobLvl()){
		case 5: return new Item[]{Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.FISHING_ROD, Items.STONE_AXE,
				Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.SHEARS, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SHOVEL,
				Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL};
		case 4: return new Item[]{Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.FISHING_ROD, Items.STONE_AXE,
				Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.SHEARS, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SHOVEL,
				Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL};
		case 3: return new Item[]{Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.FISHING_ROD, Items.STONE_AXE,
				Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.SHEARS, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SHOVEL};
		case 2: return new Item[]{Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.FISHING_ROD, Items.STONE_AXE,
				Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL};
		default: return new Item[]{Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL};
		}
	}

	public void afterCraft(ItemCraftedEvent e){
		for(Item i : this.getAvailableRecipes()){
			if(e.crafting.getItem() == i){
				this.getData().setJobXP(this.getData().getJobXP() + 1 + this.getPlayer().world.rand.nextInt(3));
				return;
			}
		}
	}
	
	@Override
	public Block[] getAvailableMineables() {
		return new Block[]{};
	}

	@Override
	public EnumJobType getType() {
		return EnumJobType.TOOL_CRAFTSTMAN;
	}

}
