package com.afg.rpmod.blocks;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

import com.afg.rpmod.client.gui.InventorTableGui;
import com.afg.rpmod.jobs.Inventor;
import com.afg.rpmod.jobs.Inventor.EnumDiscoverableType;

public class InventorTable extends Block implements ITileEntityProvider{

	public InventorTable() {
		super(Material.ROCK);
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
		if(playerIn.worldObj.isRemote){
			if(this.getTE(worldIn, pos).getPlayer() == playerIn)
				Minecraft.getMinecraft().displayGuiScreen(new InventorTableGui(pos));
		}
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
				EnumDiscoverableType discoverable = null;
				for(EnumDiscoverableType d : Inventor.discoverables)
					if(d.getName() == tag.getString("purchase"))
						discoverable = d;
				if(discoverable != null){
					this.discoverable = discoverable;
					while(this.requiredItem == null || this.requiredItem.stackSize == 0)
						this.requiredItem = new ItemStack(Items.APPLE, this.worldObj.rand.nextInt(17));
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
			if(tag.getInteger("requiredItemNum") != 0)
				this.requiredItem = new ItemStack(Item.getItemById(tag.getInteger("requiredItem")), tag.getInteger("requiredItemNum"));
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

		@Override
		public boolean isApprovedPlayer(EntityPlayer player) {
			return player == this.getPlayer();
		}
	}
}
