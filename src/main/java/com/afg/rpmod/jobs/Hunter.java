package com.afg.rpmod.jobs;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class Hunter extends Job {

	public Hunter(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 4 + getData().getJobLvl()*2;
	}

	@Override
	public void onKill(LivingDeathEvent e){
		if(!(e.getEntity() instanceof EntityPlayer)){
			int amount = 1;
			if(e.getEntityLiving().getMaxHealth() > 50 && !(e.getEntityLiving() instanceof EntityIronGolem))
				amount = (int) (e.getEntityLiving().getMaxHealth()/10);
			this.getData().setJobXP(this.getData().getJobXP() + amount);
		}
	}
	
	@Override
	public void onLivingDrops(LivingDropsEvent e){
		//TODO figure out if this event fires when killing players
		if(!(e.getEntity() instanceof EntityPlayer)){
			for(EntityItem item : e.getDrops()){
				while(e.getEntity().worldObj.rand.nextInt(10/this.getData().getJobLvl()) == 0){
					e.getEntity().worldObj.spawnEntityInWorld(new EntityItem(e.getEntity().worldObj, e.getEntity().posX, e.getEntity().posY, e.getEntity().posZ, item.getEntityItem()));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "Hunter";
	}

	@Override
	public Item[] getAvailableRecipes() {
		if(getData().getJobLvl() > 1)
			return this.getType().getExclusiveItems();
		else 
			return new Item[]{};
	}

	@Override
	public EnumJobType getType() {
		return EnumJobType.HUNTER;
	}

	@Override
	public Block[] getAvailableMineables() {
		return new Block[]{};
	}


}
