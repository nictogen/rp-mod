package com.afg.rpmod.jobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.jobs.Inventor.EnumDiscoverableType;

public abstract class Job {
	private EntityPlayer player;
	public Job(EntityPlayer player){
		this.player = player;
	}
	//List of all the jobs, for translating to NBT to save and send through packets
	private static EnumJobType[] jobTypeList = EnumJobType.values();

	/**
	 * Bridge method to create a new job instance
	 * @param jobID to create an instance of
	 * @param player to create a job for
	 * @return the job
	 */
	public static <T extends Job> T createJob(int jobID, EntityPlayer player){
		EnumJobType type = jobTypeList[jobID];
		return type.createJob(player);
	}

	public abstract double getIncome();

	public abstract String getName();
	
	public abstract Item[] getAvailableRecipes();
	
	public static boolean isExclusiveRecipe(Item item){
		for(EnumJobType job : jobTypeList){
			for(Item i : job.getExclusiveItems())
				if(item == i)
					return true;
		}
		return false;
	}
	
	public static List<Item> getAllExclusiveRecipes(){
		ArrayList<Item> exclusiveItems = new ArrayList<Item>();
		for(EnumJobType job : jobTypeList)
			for(Item i : job.getExclusiveItems())
				exclusiveItems.add(i);
		return exclusiveItems;
	}
	
	//Logic methods to do custom abilities/give xp
	public void onUpdate(){}
	public void onKill(LivingDeathEvent e){}
	public void onLivingDrops(LivingDropsEvent e){}
	
	public EntityPlayer getPlayer(){
		return this.player;
	}
	
	protected IPlayerData getData(){
		return player.getCapability(IPlayerData.PLAYER_DATA, null);
	}

	public abstract EnumJobType getType();



	//Enum of jobs that serve as a factory. Probs overkill but w/e
	public static enum EnumJobType {
		//Declaration of Types
		UNEMPLOYED(0, Unemployed.class),
		HUNTER(1, Hunter.class, Items.LEATHER),
		INVENTOR(2, Inventor.class);
		//Variables for JobType
		private int id;
		private Class<? extends Job> job;
		private Item[] exclusiveItems;
		//Constructor
		EnumJobType(int id, Class<? extends Job> job, Item...exclusiveItems){
			this.id = id;
			this.job = job;
			this.exclusiveItems = exclusiveItems;
		}

		//Factory
		public <T extends Job> T createJob(EntityPlayer player){
			try {
				return (T) this.job.getConstructor(EntityPlayer.class).newInstance(player);
				//TODO log this exception or something
			} catch (Exception e){};

			return null;
		}

		//Getters
		public int getID(){
			return this.id;
		}
		
		public Item[] getExclusiveItems(){
			return this.exclusiveItems;
		}
	}

}
