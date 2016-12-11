package com.afg.rpmod.blocks;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.jobs.Cook;

public class CookPan extends Block implements ITileEntityProvider{

	public CookPan(String name) {
		super(Material.IRON);
		this.setUnlocalizedName(name);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new CookPanTE();
	}

	public CookPanTE getTE(World world, BlockPos pos) {
		return (CookPanTE) world.getTileEntity(pos);
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		IPlayerData data = playerIn.getCapability(IPlayerData.PLAYER_DATA, null);
		if(heldItem != null){
			if(this.getTE(worldIn, pos).getPlayer() == playerIn && data.getJob() instanceof Cook){
				if(this.getTE(worldIn, pos).cooking.getItem() == Item.getItemFromBlock(Blocks.AIR)){
					Item result = CookPanTE.getCookingResult(heldItem);
					if(result == null)
						return false;

					for(Item i : data.getJob().getAvailableRecipes()){
						if(i == result){
							this.getTE(worldIn, pos).cooking = new ItemStack(heldItem.getItem());
							heldItem.stackSize--;
							return true;
						}
					}

				} 
			}
		} 
		if(this.getTE(worldIn, pos).getPlayer() == playerIn && data.getJob() instanceof Cook){
			if(this.getTE(worldIn, pos).cooking.getItem() != Item.getItemFromBlock(Blocks.AIR)){
				if(!worldIn.isRemote){
					if(heldItem == null){
						playerIn.setHeldItem(hand, new ItemStack(this.getTE(worldIn, pos).cooking.getItem(), 1));
					} else if (heldItem.getItem() == this.getTE(worldIn, pos).cooking.getItem()){
						playerIn.getHeldItem(hand).stackSize++;
					} else {
						EntityItem item = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.getTE(worldIn, pos).cooking.getItem(), 1));
						worldIn.spawnEntityInWorld(item);
					}
					data.setMoney(data.getMoney() + data.getJobLvl()*4);
					data.setJobXP(data.getJobXP() + 1);
				}
				this.getTE(worldIn, pos).cooking = new ItemStack(Item.getItemFromBlock(Blocks.AIR));
				return true;
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

	public static class CookPanTE extends TileEntity implements ITickable{

		private UUID uuid;
		private String playername;
		public int timer = 0;
		ItemStack cooking = new ItemStack(Item.getItemFromBlock(Blocks.AIR));

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
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.playername = tag.getString("playername");
			UUID u = tag.getUniqueId("uuid");
			if(u != null)
				this.uuid = u;
			this.timer = tag.getInteger("time");
			this.cooking = new ItemStack(Item.getItemById(tag.getInteger("cooking")), 1);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			if(this.uuid != null)
				tag.setUniqueId("uuid", this.uuid);
			tag.setString("playername", this.playername);
			tag.setInteger("time", this.timer);
			tag.setInteger("cooking", Item.getIdFromItem(this.cooking.getItem()));
			return tag;
		}

		public void setPlayer(EntityPlayer player){
			if(player != null){
				this.uuid = player.getOfflineUUID(player.getName());
				this.playername = player.getName();
			}
		}

		public ItemStack getCooking(){
			return this.cooking;
		}

		public EntityPlayer getPlayer(){
			if(this.uuid != null)
				return this.worldObj.getPlayerEntityByUUID(this.uuid);
			return null;
		}

		public static Item getCookingResult(ItemStack itemIn){
			Item result = null;
			if(itemIn.getItem() == Items.BEEF)
				result = Items.COOKED_BEEF;
			else if(itemIn.getItem() == Items.CHICKEN)
				result = Items.COOKED_CHICKEN;
			else if(itemIn.getItem() == Items.FISH)
				result = Items.COOKED_FISH;
			else if(itemIn.getItem() == Items.MUTTON)
				result = Items.COOKED_MUTTON;
			else if(itemIn.getItem() == Items.PORKCHOP)
				result = Items.COOKED_PORKCHOP;
			else if(itemIn.getItem() == Items.RABBIT)
				result = Items.COOKED_RABBIT;
			else if(itemIn.getItem() == Items.POTATO)
				result = Items.BAKED_POTATO;
			return result;
		}

		@Override
		public void update() {
			if(getCookingResult(this.cooking) != null && this.getPlayer() != null){
				IPlayerData data = this.getPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
				if(data.getJob() instanceof Cook){
					this.timer += data.getJobLvl();
					if(this.timer > 200){
						this.cooking = new ItemStack(getCookingResult(this.cooking), 1 + this.worldObj.rand.nextInt(data.getJobLvl()));
						this.timer = 0;
					}
				}
			}
		}

	}
}