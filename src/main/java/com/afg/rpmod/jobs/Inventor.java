package com.afg.rpmod.jobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.network.UpdateClientDiscoveryData;

@Mod.EventBusSubscriber
public class Inventor extends Job {

	public static EnumDiscoverableType[] discoverables = EnumDiscoverableType.values();
	public static boolean discoveriesChanged = false;
	public Inventor(EntityPlayer player) {
		super(player);
	}

	@Override
	public double getIncome() {
		return 2 + getData().getJobLvl()*3;
	}

	@Override
	public String getName() {
		return "Inventor";
	}

	@Override
	public Item[] getAvailableRecipes() {
		return null;
	}

	@Override
	public EnumJobType getType() {
		return EnumJobType.INVENTOR;
	}

	public static boolean isDiscovered(Item item){
		for(EnumDiscoverableType d : discoverables){
			if(d.item != null)
				if(d.item == item)
					return d.isDiscovered();
		}
		return true;
	}

	public static List<Item> getDiscoverableItems(){
		ArrayList<Item> discoverableItems = new ArrayList<Item>();
		for(EnumDiscoverableType discoverable : discoverables){
			if(discoverable.item != null)
				discoverableItems.add(discoverable.item);
		}
		return discoverableItems;
	}

	public static enum EnumDiscoverableType{
		STONE_AXE(Items.STONE_AXE),
		STONE_PICK(Items.STONE_PICKAXE),
		STONE_SHOVEL(Items.STONE_SHOVEL),
		STONE_HOE(Items.STONE_HOE),
		STONE_SWORD(Items.STONE_SWORD),
		LEATHER_BOOTS(Items.LEATHER_BOOTS),
		LEATHER_LEGGINGS(Items.LEATHER_LEGGINGS),
		LEATHER_CHESTPLATE(Items.LEATHER_CHESTPLATE),
		LEATHER_HELMET(Items.LEATHER_HELMET),
		TORCH(Blocks.TORCH),
		BED(Blocks.BED),
		BOAT(Items.BOAT),
		MSTEW(Items.MUSHROOM_STEW),
		RSTEW(Items.RABBIT_STEW),
		BOW(Items.BOW),
		ARROW(Items.ARROW);

		private Item item;
		private boolean discovered = false;

		EnumDiscoverableType(Item item){
			this.item = item;
		}

		EnumDiscoverableType(Block block){
			this.item = Item.getItemFromBlock(block);
		}

		public boolean isDiscovered(){
			return this.discovered;
		}

		public void setDiscovered(boolean discovered){
			this.discovered = discovered;
			discoveriesChanged = true;
		}

		public String getName(){
			if(item != null)
				return item.getUnlocalizedName();
			return "";
		}
	}

	public static class DiscoveryData extends WorldSavedData {
		private static final String DATA_NAME = RpMod.MODID + "_DiscoveryData";

		public DiscoveryData() {
			super(DATA_NAME);
		}
		public DiscoveryData(String s) {
			super(s);
		}
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			for(EnumDiscoverableType d : discoverables){
				d.discovered = nbt.getBoolean(RpMod.MODID + d.getName());
			}
		}
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			for(EnumDiscoverableType d : discoverables){
				compound.setBoolean(RpMod.MODID + d.getName(), d.isDiscovered());
			}
			return compound;
		}

		public static DiscoveryData get(World world) {
			MapStorage storage = world.getMapStorage();
			DiscoveryData instance = (DiscoveryData) storage.getOrLoadData(DiscoveryData.class, DATA_NAME);

			if (instance == null) {
				instance = new DiscoveryData();
				storage.setData(DATA_NAME, instance);
			}
			return instance;
		}
	}

	@SubscribeEvent
	public static void sendDiscoveriesToClientOnSpawn(PlayerEvent.PlayerRespawnEvent e){
		if(!e.player.worldObj.isRemote)
			RpMod.networkWrapper.sendTo(new UpdateClientDiscoveryData(DiscoveryData.get(e.player.worldObj)), (EntityPlayerMP) e.player);
	}

	@SubscribeEvent
	public static void sendNewDiscoveriesToClient(TickEvent.WorldTickEvent e){
		if(!e.world.isRemote && discoveriesChanged){
			RpMod.networkWrapper.sendToAll(new UpdateClientDiscoveryData(DiscoveryData.get(e.world)));
			discoveriesChanged = false;
		}
	}
}
