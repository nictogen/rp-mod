package com.afg.rpmod.blocks;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.client.gui.CityGui;
import com.afg.rpmod.utils.CityUtils;
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

import javax.annotation.Nullable;
import java.util.UUID;

public class CityBlock extends Block implements ITileEntityProvider{

	public CityBlock() {
		super(Material.ANVIL);
		this.isBlockContainer = true;
		this.setHardness(0.5f);
		this.setUnlocalizedName("City Block");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new CityBlockTE();
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(playerIn.world.isRemote){
			if(this.getTE(worldIn, pos).getPlayer() == playerIn)
				Minecraft.getMinecraft().displayGuiScreen(new CityGui(pos));
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			this.getTE(worldIn, pos).setPlayer((EntityPlayer) placer);
		if(!CityUtils.roomForCity(worldIn, this.getTE(worldIn, pos)))
			worldIn.destroyBlock(pos, true);
	}

	public CityBlockTE getTE(World world, BlockPos pos) {
		return (CityBlockTE) world.getTileEntity(pos);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	public static class CityBlockTE extends TileEntity implements IUpdatesFromClient{
		public int range = 1;
		public int maxRange = 100;
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
			if(tag.getString("playername") != ""){
				this.setPlayer(this.world.getPlayerEntityByName(tag.getString("playername")));
			}
			if(tag.getInteger("range") != 0)
				this.setRange(tag.getInteger("range"));
			this.world.notifyBlockUpdate(pos, this.world.getBlockState(getPos()), this.world.getBlockState(getPos()), 3);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.range = tag.getInteger("range");
			this.maxRange = tag.getInteger("maxrange");
			this.playername = tag.getString("name");
			UUID u = tag.getUniqueId("uuid");
			if(u != null)
				this.uuid = u;
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("range", this.range);
			tag.setInteger("maxrange", this.maxRange);
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
			if(range > this.range && !CityUtils.canExpand(world, this, range - this.range))
				return;
			if(this.getPlayer() != null){
				IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
				if(range > this.maxRange){
					int amount = range - this.maxRange;
					if(data.getMoney() > amount*100){
						data.setMoney(data.getMoney() - amount*100);
						this.maxRange += amount;
					}
				}
			}
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
				return this.world.getPlayerEntityByUUID(this.uuid);
			return null;
		}

		@Override
		public boolean isApprovedPlayer(EntityPlayer player) {
			return this.getPlayer() == player;
		}

	}

}