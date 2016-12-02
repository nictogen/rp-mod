package com.afg.rpmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

	public static class PlotBlockTE extends TileEntity{

		public static int range = 30;
		public EntityPlayer player;
		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.range = tag.getInteger("range");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("range", this.range);
			return tag;
		}

		public void setPlayer(EntityPlayer player){
			this.player = player;
		}

		public EntityPlayer getPlayer(){
			return this.player;
		}
	}
}
