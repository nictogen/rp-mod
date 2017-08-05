package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class WeaponCraftsman extends Job {

	public WeaponCraftsman(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 3 + getData().getJobLvl();
	}

	@Override
	public String getName() {
		return "Weapon and Armor Craftsman";
	}
	
	@Override
	public Item[] getAvailableRecipes() {
		switch(this.getData().getJobLvl()){
		case 5: return new Item[]{Items.WOODEN_SWORD, Items.BOW, Items.ARROW, Items.STONE_SWORD, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.SHIELD,
				Items.IRON_SWORD, Items.TIPPED_ARROW, Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.GOLDEN_SWORD, Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, 
				Items.GOLDEN_HELMET, Items.DIAMOND_SWORD, Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET};
		case 4: return new Item[]{Items.WOODEN_SWORD, Items.BOW, Items.ARROW, Items.STONE_SWORD, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.SHIELD,
				Items.IRON_SWORD, Items.TIPPED_ARROW, Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.GOLDEN_SWORD, Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET};
		case 3: return new Item[]{Items.WOODEN_SWORD, Items.BOW, Items.ARROW, Items.STONE_SWORD, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.SHIELD,
				Items.IRON_SWORD, Items.TIPPED_ARROW, Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET};
		case 2: return new Item[]{Items.WOODEN_SWORD, Items.BOW, Items.ARROW, Items.STONE_SWORD, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.SHIELD};
		default: return new Item[]{Items.WOODEN_SWORD, Items.BOW, Items.ARROW};
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
		return EnumJobType.WEAPON_CRAFTSMAN;
	}

}
