package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

public class Farmer extends Job {

	public Farmer(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 5 + getData().getJobLvl()*2;
	}

	@Override
	public String getName() {
		return "Farmer";
	}

	@Override
	public Item[] getAvailableRecipes() {
		return new Item[]{};
	}

	@Override
	public Block[] getAvailableMineables() {
		switch(this.getData().getJobLvl()){
		case 5: return new Block[]{Blocks.WHEAT, Blocks.CARROTS, Blocks.BEETROOTS, Blocks.POTATOES, Blocks.MELON_BLOCK, Blocks.PUMPKIN};
		case 4: return new Block[]{Blocks.WHEAT, Blocks.CARROTS, Blocks.BEETROOTS, Blocks.POTATOES, Blocks.MELON_BLOCK, Blocks.PUMPKIN};
		case 3: return new Block[]{Blocks.WHEAT, Blocks.CARROTS, Blocks.BEETROOTS, Blocks.POTATOES};
		case 2: return new Block[]{Blocks.WHEAT, Blocks.CARROTS};
		default : return new Block[]{Blocks.WHEAT};
		}
	}

	@Override
	public void onBlockDrops(HarvestDropsEvent e){
		Item[] dropsWithXP = {Items.WHEAT, Items.CARROT, Items.BEETROOT, Items.POTATO, Items.MELON};

		for(ItemStack i : e.getDrops()){
			Item item = i.getItem();
			if(item == Items.WHEAT || item == Items.BEETROOT|| i.getCount() > 1)
				for(int x = 0; x < dropsWithXP.length; x++){
					Item xpDrops = dropsWithXP[x];
					if(item == xpDrops){
						this.getData().setJobXP(this.getData().getJobXP() + 1);
					}
				}
		}
	}

	@Override
	public EnumJobType getType() {
		return EnumJobType.FARMER;
	}

}
