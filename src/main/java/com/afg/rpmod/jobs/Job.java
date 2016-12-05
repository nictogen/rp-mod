package com.afg.rpmod.jobs;

import com.afg.rpmod.capabilities.IPlayerData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public abstract class Job {
	private EntityPlayer player;
	public Job(EntityPlayer player){
		this.player = player;
	}
	//List of all the jobs, for translating to NBT to save and send through packets
	private static JobType[] jobTypeList = {JobType.UNEMPLOYED, JobType.HUNTER};

	/**
	 * Bridge method to create a new job instance
	 * @param jobID to create an instance of
	 * @param player to create a job for
	 * @return the job
	 */
	public static <T extends Job> T createJob(int jobID, EntityPlayer player){
		JobType type = jobTypeList[jobID];
		return type.createJob(player);
	}

	public abstract double getIncome();

	public abstract String getName();
	
	public abstract Item[] getExclusiveCraftingRecipes();
	
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

	public abstract JobType getType();



	//Enum of jobs that serve as a factory. Probs overkill but w/e
	public static enum JobType {
		//Declaration of Types
		UNEMPLOYED(0, Unemployed.class),
		HUNTER(1, Hunter.class);
		//Variables for JobType
		private int id;
		private Class<? extends Job> job;

		//Constructor
		JobType(int id, Class<? extends Job> job){
			this.id = id;
			this.job = job;
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
	}

}
