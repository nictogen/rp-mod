package com.afg.rpmod.blocks;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import com.afg.rpmod.RpMod;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.client.gui.InventorTableGui;
import com.afg.rpmod.jobs.Inventor;
import com.afg.rpmod.jobs.Inventor.EnumDiscoverableType;

public class InventorTable extends Block implements ITileEntityProvider{

	public InventorTable(String name) {
		super(Material.ROCK);
		this.setUnlocalizedName(name);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new InventorTableTE();
	}

	public InventorTableTE getTE(World world, BlockPos pos) {
		return (InventorTableTE) world.getTileEntity(pos);
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(!playerIn.worldObj.isRemote){
			if(this.getTE(worldIn, pos).isApprovedPlayer(playerIn)){
				FMLNetworkHandler handler = new FMLNetworkHandler();
				handler.openGui(playerIn, RpMod.instance, InventorTableGui.GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			this.getTE(worldIn, pos).setPlayer((EntityPlayer) placer);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	public static class InventorTableTE extends TileEntity implements IUpdatesFromClient{

		private UUID uuid;
		private String playername;
		public int cost = 500;
		public EnumDiscoverableType discoverable;
		public ItemStack requiredItem;
		public int completion = 0;

		@Override
		public NBTTagCompound getUpdateTag() {
			return writeToNBT(new NBTTagCompound());
		}

		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			NBTTagCompound nbtTag = new NBTTagCompound();
			this.writeToNBT(nbtTag);
			return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
		}

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
			this.readFromNBT(packet.getNbtCompound());
		}

		@Override
		public void updateServerData(NBTTagCompound tag) {
			if(!tag.getString("purchase").isEmpty()){
				IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
				EnumDiscoverableType discoverable = null;
				for(EnumDiscoverableType d : Inventor.discoverables)
					if(d.getName().contentEquals(tag.getString("purchase")))
						discoverable = d;
				if(discoverable != null && data.getMoney() >= this.cost){
					this.discoverable = discoverable;
					data.setMoney(data.getMoney() - this.cost);
					this.requiredItem = this.newRequiredItem();
				}
			}
			if(tag.getBoolean("research") == true){
				if(this.getPlayer() != null){
					ItemStack pItem = this.getPlayer().inventory.getItemStack();
					if(pItem != null)
						if(pItem.isItemEqual(this.requiredItem))
							if(pItem.stackSize >= this.requiredItem.stackSize){
								pItem.stackSize -= this.requiredItem.stackSize;
								if(pItem.stackSize <= 0)
									this.getPlayer().inventory.setItemStack(null);
								this.completion += this.requiredItem.stackSize;
								if(this.completion > 100){
									this.completion = 0;
									this.discoverable.setDiscovered(true);
									IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
									data.setMoney(data.getMoney() + this.cost*5);
									this.discoverable = null;
									this.requiredItem = null;
									for(int i = 0; i < 4; i++){
										EntityFireworkRocket firework = new EntityFireworkRocket(this.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ(), null);
										this.worldObj.spawnEntityInWorld(firework);
									}
								} else {
									this.requiredItem = this.newRequiredItem();
								}


							}
				}
			}
			this.worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(getPos()), this.worldObj.getBlockState(getPos()), 3);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.playername = tag.getString("playername");
			UUID u = tag.getUniqueId("uuid");
			if(u != null)
				this.uuid = u;
			this.completion = tag.getInteger("completion");
			if(tag.getInteger("requiredItemNum") != 0){
				this.requiredItem = new ItemStack(Item.getItemById(tag.getInteger("requiredItem")), tag.getInteger("requiredItemNum"));
			} else {
				this.requiredItem = null;
			}
			if(tag.getString("discoverable").contentEquals("NULL"))
				this.discoverable = null;
			else
				for(EnumDiscoverableType d : Inventor.discoverables)
					if(d.getName().contentEquals(tag.getString("discoverable")))
						this.discoverable = d;

		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			if(this.uuid != null)
				tag.setUniqueId("uuid", this.uuid);
			tag.setString("playername", this.playername);
			tag.setInteger("completion", this.completion);
			if(this.requiredItem != null){
				tag.setInteger("requiredItemNum", this.requiredItem.stackSize);
				tag.setInteger("requiredItem", Item.getIdFromItem(this.requiredItem.getItem()));
			}
			if(this.discoverable != null)
				tag.setString("discoverable", this.discoverable.getName());
			else
				tag.setString("discoverable", "NULL");
			return tag;
		}

		public void setPlayer(EntityPlayer player){
			if(player != null){
				this.uuid = player.getOfflineUUID(player.getName());
				this.playername = player.getName();
			}
		}

		public EntityPlayer getPlayer(){
			if(this.uuid != null)
				return this.worldObj.getPlayerEntityByUUID(this.uuid);
			return null;
		}

		public ItemStack newRequiredItem(){
			Item item = null;
			int size = 1 + this.worldObj.rand.nextInt(16);
			Item[] randomItemList = {Items.ARROW, Items.APPLE, Items.BED, Items.BEEF, Items.BANNER, Items.BLAZE_POWDER, Items.BOAT,
					Items.BONE, Items.BOOK, Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_SWORD,
					Items.STONE_AXE, Items.STONE_HOE, Items.STONE_PICKAXE, Items.STONE_SHOVEL, Items.STONE_SWORD, Items.IRON_AXE, Items.IRON_HOE,
					Items.IRON_BOOTS, Items.IRON_CHESTPLATE, Items.IRON_HELMET, Items.IRON_HOE, Items.IRON_INGOT, Items.IRON_LEGGINGS, Items.LEATHER,
					Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.BLAZE_ROD, Items.SLIME_BALL,
					Items.STRING, Items.SNOWBALL, Items.BUCKET, Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.COAL, Items.CARROT, Items.FISH, Items.FISHING_ROD,
					Items.CHICKEN, Items.RABBIT_HIDE, Items.RABBIT_FOOT};
			while(item == null || !Inventor.isDiscovered(item)){
				item = randomItemList[this.worldObj.rand.nextInt(randomItemList.length)];
			}
			return new ItemStack(item, size); 
		}
		
		
		@Override
		public boolean isApprovedPlayer(EntityPlayer player) {
			IPlayerData data = player.getCapability(IPlayerData.PLAYER_DATA, null);
			
//			return data.getJob() != null && data.getJob().getType() == EnumJobType.INVENTOR && 
				return	player == this.getPlayer();
		}
	}

	public static class InventorTableContainer extends Container{

		private InventorTableTE te;
		public InventorTableContainer(InventoryPlayer invPlayer, InventorTableTE te){
			this.te = te;

			for (int y = 0; y < 3; ++y) {
				for (int x = 0; x < 9; ++x) {
					this.addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + x * 18, 97 + y * 18));
				}
			}

			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 155));
			}


		}

		@Override
		@Nullable
		public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
		{
			return null;
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return te.isApprovedPlayer(playerIn);
		}

	}
}
