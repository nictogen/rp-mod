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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.afg.rpmod.blocks.CityBlock.CityBlockTE;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.client.gui.PlotGui;
import com.afg.rpmod.utils.CityUtils;

public class ApartmentBlock extends Block implements ITileEntityProvider{

	public ApartmentBlock() {
		super(Material.ANVIL);
		this.isBlockContainer = true;
		this.setHardness(0.5f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new PlotBlockTE();
	}

	public ApartmentBlockTE getTE(World world, BlockPos pos) {
		return (ApartmentBlockTE) world.getTileEntity(pos);
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
		if(placer instanceof EntityPlayer){
			this.getTE(worldIn, pos).setPlayer((EntityPlayer) placer);
			CityBlockTE city = CityUtils.closestCity(worldIn, pos);
			if(city == null || Math.sqrt(Math.pow((city.getPos().getX() - pos.getX()), 2) + Math.pow((city.getPos().getZ() - pos.getZ()), 2)) > 100)
				worldIn.destroyBlock(pos, true);
			else
				this.getTE(worldIn, pos).plot = city.getPos();
			if(!CityUtils.roomForApartment(worldIn, this.getTE(worldIn, pos)))
				worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	public static class ApartmentBlockTE extends TileEntity implements IUpdatesFromClient, ITickable{

		public int range = 1;
		public int height = 1;
		public int maxRange = 100;
		public int maxHeight = 50;
		private UUID uuid;
		private String playername;
		private BlockPos plot;
		private boolean renting = false;

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
				this.setPlayer(this.worldObj.getPlayerEntityByName(tag.getString("playername")));
			}
			if(tag.getInteger("range") != 0)
				this.setRange(tag.getInteger("range"));
			if(tag.getInteger("height") != 0)
				this.setHeight(tag.getInteger("height"));
			this.worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(getPos()), this.worldObj.getBlockState(getPos()), 3);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.range = tag.getInteger("range");
			this.playername = tag.getString("name");
			BlockPos plot = new BlockPos(tag.getInteger("plotX"), tag.getInteger("plotY"), tag.getInteger("plotZ"));
			this.setPlot(plot);
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
			if(this.plot != null){
				tag.setInteger("plotX", this.getPlot().getX());
				tag.setInteger("plotY", this.getPlot().getY());
				tag.setInteger("plotZ", this.getPlot().getZ());
			}
			tag.setInteger("height", this.height);
			return tag;
		}

		public void setPlayer(EntityPlayer player){
			if(player != null){
				this.uuid = player.getOfflineUUID(player.getName());
				this.playername = player.getName();
			}
		}

		public void setRange(int range){
			if(range > this.range && !CityUtils.canExpand(worldObj, this, range - this.range))
				return;
			this.range = range;
			if(this.range > this.maxRange)
				this.range = this.maxRange;
			if(this.range < 1)
				this.range = 1;
		}

		public void setHeight(int height){
			if(height > this.height && !CityUtils.canExpandY(worldObj, this, height - this.height))
				return;
			this.height = height;
			if(this.height > this.maxHeight)
				this.height = this.maxHeight;
			if(this.height < 1)
				this.height = 1;
		}
		
		public String getName(){
			return this.playername;
		}

		public EntityPlayer getPlayer(){
			if(this.uuid != null)
				return this.worldObj.getPlayerEntityByUUID(this.uuid);
			return null;
		}

		@Override
		public void update() {
			if(this.getPlot() == null || !(this.worldObj.getBlockState(this.getPlot()).getBlock() instanceof PlotBlock))
				this.worldObj.destroyBlock(getPos(), true);
			if(this.getPlayer() != null){
				IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
				if(data.getTotalPlaytime() != 0 && data.getTotalPlaytime() % 168000 == 0){
					//TODO charge rent
				}
			}
		}

		public BlockPos getPlot() {
			return plot;
		}

		public void setPlot(BlockPos plot) {
			this.plot = plot;
		}
	}
}
