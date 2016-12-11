package com.afg.rpmod.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

import com.afg.rpmod.capabilities.IPlayerData;

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

	public abstract Block[] getAvailableMineables();


	public static boolean isExclusiveMineable(Block block){
		for(EnumJobType job : jobTypeList){
			for(Block b : job.getExclusiveMineables()){
				if(block == b)
					return true;
			}
		}
		return false;
	}
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
	public void afterCraft(ItemCraftedEvent e){}
	public void onBlockDrops(HarvestDropsEvent e){}
	public void afterSmelting(ItemSmeltedEvent e){}
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
		UNEMPLOYED(0, Item.getItemFromBlock(Blocks.DIRT), Unemployed.class, null),
		HUNTER(1, Items.BOW, Hunter.class, null, Items.LEATHER),
		INVENTOR(2, Item.getItemFromBlock(Blocks.LEVER), Inventor.class, null),
		COOK(3, Items.RABBIT_STEW, Cook.class, null, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_FISH, Items.COOKED_MUTTON,
				Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUSHROOM_STEW, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, 
				Items.COOKIE, Items.CAKE, Items.BREAD, Items.BEETROOT_SOUP, Items.BAKED_POTATO),
				//TODO Items.PUMPKIN_PIE, 
		MINER(4, Items.STONE_PICKAXE, Miner.class, new Block[]{Blocks.STONE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
				Blocks.REDSTONE_ORE, Blocks.QUARTZ_ORE, Blocks.LAPIS_ORE, Blocks.EMERALD_ORE, Blocks.GLOWSTONE}),
		TOOL_CRAFTSTMAN(5, Item.getItemFromBlock(Blocks.CRAFTING_TABLE), ToolCraftsman.class, null, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL,
				Items.STONE_AXE, Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SHOVEL,
				Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, 
				Items.SHEARS, Items.FISHING_ROD),
		WEAPON_CRAFTSMAN(6, Items.LEATHER_CHESTPLATE, WeaponCraftsman.class, null, Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD,
				Items.SHIELD, Items.ARROW, Items.TIPPED_ARROW, Items.BOW, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS,
				Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET,
				Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET);
		
		//Variables for JobType
		private int id;
		private Class<? extends Job> job;
		private Item displayItem;
		private Item[] exclusiveItems;
		private Block[] exclusiveMineables;
		//Constructor
		EnumJobType(int id, Item displayItem, Class<? extends Job> job, @Nullable Block[] exclusiveMineables, Item...exclusiveItems){
			this.id = id;
			this.job = job;
			this.displayItem = displayItem;
			this.exclusiveItems = exclusiveItems;
			this.exclusiveMineables = exclusiveMineables;
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

		public Block[] getExclusiveMineables(){
			if(this.exclusiveMineables != null)
				return this.exclusiveMineables;
			else
				return new Block[]{};
		}

		public ItemStack getDisplayItem(){
			return new ItemStack(this.displayItem, 1);
		}
	}

}
