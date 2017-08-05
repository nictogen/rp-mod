package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class Cook extends Job {

	public Cook(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 4 + getData().getJobLvl()*2;
	}

	@Override
	public String getName() {
		return "Cook";
	}

	@Override
	public Item[] getAvailableRecipes() {
		switch(this.getData().getJobLvl()){
		case 5: return new Item[]{Items.COOKIE, Items.BREAD, Items.BAKED_POTATO, Items.COOKED_FISH, Items.COOKED_RABBIT, Items.PUMPKIN_PIE, Items.BEETROOT_SOUP, Items.MUSHROOM_STEW,
				Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.CAKE, Items.GOLDEN_CARROT, Items.RABBIT_STEW, Items.GOLDEN_APPLE};
		case 4: return new Item[]{Items.COOKIE, Items.BREAD, Items.BAKED_POTATO, Items.COOKED_FISH, Items.COOKED_RABBIT, Items.PUMPKIN_PIE, Items.BEETROOT_SOUP, Items.MUSHROOM_STEW,
				Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.CAKE, Items.GOLDEN_CARROT, Items.RABBIT_STEW};
		case 3: return new Item[]{Items.COOKIE, Items.BREAD, Items.BAKED_POTATO, Items.COOKED_FISH, Items.COOKED_RABBIT, Items.PUMPKIN_PIE, Items.BEETROOT_SOUP, Items.MUSHROOM_STEW,
				Items.COOKED_CHICKEN, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP};
		case 2: return new Item[]{Items.COOKIE, Items.BREAD, Items.BAKED_POTATO, Items.COOKED_FISH, Items.COOKED_RABBIT, Items.PUMPKIN_PIE, Items.BEETROOT_SOUP, Items.MUSHROOM_STEW};
		default : return new Item[]{Items.COOKIE, Items.BREAD, Items.BAKED_POTATO};
		}
	}

	public void afterCraft(ItemCraftedEvent e){
		for(Item i : this.getAvailableRecipes()){
			if(e.crafting.getItem() == i){
				this.getData().setJobXP(this.getData().getJobXP() + 1);
				this.getData().setMoney(this.getData().getMoney() + this.getData().getJobLvl()*4);
				return;
			}
		}
	}
	
	@Override
	public EnumJobType getType() {
		return EnumJobType.COOK;
	}

	@Override
	public Block[] getAvailableMineables() {
		return new Block[]{};
	}

}
