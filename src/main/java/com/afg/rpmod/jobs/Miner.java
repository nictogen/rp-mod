package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

public class Miner extends Job {

	public Miner(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 2 + getData().getJobLvl();
	}

	@Override
	public String getName() {
		return "Miner";
	}

	@Override
	public Item[] getAvailableRecipes() {
		return new Item[]{};
	}

	@Override
	public void onBlockDrops(HarvestDropsEvent e){
		Item[] dropsWithXP = {Items.GLOWSTONE_DUST, Items.COAL, Items.DYE, Items.REDSTONE, Items.QUARTZ, Items.DIAMOND, Items.EMERALD};

		for(ItemStack i : e.getDrops()){
			Item item = i.getItem();
			for(int x = 0; x < dropsWithXP.length; x++){
				Item xpDrops = dropsWithXP[x];
				if(item == xpDrops){
					this.getData().setJobXP(this.getData().getJobXP() + 1 + x/2);
				}
			}
		}
	}

	public void afterSmelting(ItemSmeltedEvent e){
		Item[] smeltsWithXP = {Items.IRON_INGOT, Items.GOLD_INGOT};
		Item item = e.smelting.getItem();
		for(Item xpSmelts: smeltsWithXP){
			if(item == xpSmelts)
				for(int i = 0; i < e.smelting.getCount(); i++)
					this.getData().setJobXP(this.getData().getJobXP() + 1);

		}
	}

	@Override
	public Block[] getAvailableMineables() {
		switch(this.getData().getJobLvl()){
		case 5: return new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.IRON_ORE, Blocks.REDSTONE_ORE, Blocks.GOLD_ORE, Blocks.GLOWSTONE, Blocks.QUARTZ_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE};
		case 4: return new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.IRON_ORE, Blocks.REDSTONE_ORE, Blocks.GOLD_ORE, Blocks.GLOWSTONE, Blocks.QUARTZ_ORE};
		case 3: return new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.IRON_ORE, Blocks.REDSTONE_ORE, Blocks.GOLD_ORE};
		case 2: return new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.IRON_ORE};
		default : return new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.LAPIS_ORE};
		}
	}

	@Override
	public EnumJobType getType() {
		return Job.EnumJobType.MINER;
	}

}
