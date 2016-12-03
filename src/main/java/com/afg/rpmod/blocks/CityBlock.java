package com.afg.rpmod.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CityBlock extends Block implements ITileEntityProvider{
	
	public CityBlock() {
		super(Material.ANVIL);
		this.isBlockContainer = true;
		this.setHardness(0.5f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new CityBlockTE();
	}

	public CityBlockTE getTE(World world, BlockPos pos) {
		return (CityBlockTE) world.getTileEntity(pos);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	public static class CityBlockTE extends TileEntity{

		@Override
		public NBTTagCompound getUpdateTag() {
			NBTTagCompound nbt = new NBTTagCompound();
			return nbt;
		}

		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			return new SPacketUpdateTileEntity(getPos(), 1, new NBTTagCompound());
		}

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			return tag;
		}


	}

}