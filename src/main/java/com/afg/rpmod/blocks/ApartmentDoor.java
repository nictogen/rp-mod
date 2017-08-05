package com.afg.rpmod.blocks;

import com.afg.rpmod.blocks.ApartmentBlock.ApartmentBlockTE;
import com.afg.rpmod.client.gui.DoorGui;
import com.afg.rpmod.utils.CityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ApartmentDoor extends BlockDoor implements ITileEntityProvider{

	public ApartmentDoor() {
		super(Material.WOOD);
		this.setHardness(0.5f);
		this.setUnlocalizedName("Apartment Door");
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ)
	{
		if(playerIn.isSneaking()){
			if(worldIn.isRemote && this.getTE(worldIn, pos).getPlayer() == playerIn){
				Minecraft.getMinecraft().displayGuiScreen(new DoorGui(this.getTE(worldIn, pos).getPos()));
			}
			return false;
		} else if(!this.getTE(worldIn, pos).isLocked()){
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		} else return false;

	}

	@Override public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		//Don't worry about checking for lock state if upper, since logic is on the bottom half
		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		}
		else
		{
			//If it's just going to destroy it, refer it to the super method
			if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn,  pos.down(), EnumFacing.UP) || worldIn.getBlockState(pos.up()).getBlock() != this)
			{
				super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
			}
			// If it's going to get a chance to respond to a redstone signal, only let it through if it's not locked
			else if(!this.getTE(worldIn, pos).isLocked())
				super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		}

	}


	public ApartmentDoorTE getTE(World world, BlockPos pos) {
		if(world.getBlockState(pos).getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
			return (ApartmentDoorTE) world.getTileEntity(pos);
		else if(world.getBlockState(pos).getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER)
			return (ApartmentDoorTE) world.getTileEntity(pos.up());
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(BlockDoor.isTop(meta)){
			return new ApartmentDoorTE();
		} else return null;
	}

	public static class ApartmentDoorTE extends TileEntity implements ITickable, IUpdatesFromClient{

		private boolean locked = false;

		@Override
		public void update() {
			if(CityUtils.apartmentInside(this.world, getPos()) == null)
				world.destroyBlock(getPos(), true);
		}

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
			if(tag.getBoolean("locked") != this.locked)
				this.locked = tag.getBoolean("locked");
			this.world.notifyBlockUpdate(pos, this.world.getBlockState(getPos()), this.world.getBlockState(getPos()), 3);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.locked = tag.getBoolean("locked");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("locked", this.locked);
			return tag;
		}
		@Override
		public boolean isApprovedPlayer(EntityPlayer player) {
			//			return player == this.getPlayer();
			return true;
		}

		public ApartmentBlockTE getApartment(){
			return CityUtils.apartmentInside(this.world, this.pos);
		}

		public EntityPlayer getPlayer(){
			if(this.world.isRemote)
				return Minecraft.getMinecraft().player;
			if(this.getApartment() != null)
				return getApartment().getPlayer();
			return null;
		}

		public boolean isLocked(){
			return this.locked;
		}
	}
}
