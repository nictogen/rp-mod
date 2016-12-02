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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.afg.rpmod.client.gui.PlotGui;

public class PlotBlock extends Block implements ITileEntityProvider{

	public PlotBlock() {
		super(Material.ANVIL);
		this.isBlockContainer = true;
		this.setHardness(0.5f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new PlotBlockTE();
	}

	public PlotBlockTE getTE(World world, BlockPos pos) {
		return (PlotBlockTE) world.getTileEntity(pos);
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(playerIn.worldObj.isRemote){
			Minecraft.getMinecraft().displayGuiScreen(new PlotGui(pos));
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

	public static class PlotBlockTE extends TileEntity implements IUpdatesFromClient{

		public int range = 10;
		public int maxRange = 10;
		private UUID uuid;
		private String playername;

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
			if(tag.getInteger("range") != 0)
				this.setRange(tag.getInteger("range"));
			if(tag.getString("playername") != ""){
				this.setPlayer(this.worldObj.getPlayerEntityByName(tag.getString("playername")));
			}
			this.worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(getPos()), this.worldObj.getBlockState(getPos()), 3);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.range = tag.getInteger("range");
			this.playername = tag.getString("name");
			UUID u = tag.getUniqueId("uuid");
			if(u != null)
				this.uuid = u;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("range", this.range);
			if(this.uuid != null)
				tag.setUniqueId("uuid", this.uuid);
			tag.setString("name", this.playername);
			return tag;
		}

		public void setPlayer(EntityPlayer player){
			if(player != null){
				this.uuid = player.getOfflineUUID(player.getName());
				this.playername = player.getName();
			}
		}

		public void setRange(int range){
			this.range = range;
			if(this.range > this.maxRange)
				this.range = this.maxRange;
			if(this.range < 1)
				this.range = 1;
		}

		public String getName(){
			return this.playername;
		}

		public EntityPlayer getPlayer(){
			if(this.uuid != null)
				return this.worldObj.getPlayerEntityByUUID(this.uuid);
			return null;
		}
	}
}
