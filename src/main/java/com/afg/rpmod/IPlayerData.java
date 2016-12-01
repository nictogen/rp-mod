package com.afg.rpmod;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerData {

	@CapabilityInject(IPlayerData.class)
	public static final Capability<IPlayerData> PLAYER_DATA = null;
	
	public double getMoney();
	
	public void setMoney(double amount);
	
	public double getBankMoney();
	
	public void setBankMoney(double amount);
	
	/**
	 * Default NBTStorage required by Forge (Just refers to instance)
	 *
	 */
	static class Storage implements Capability.IStorage<IPlayerData> {

		@Override
		public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
			if(instance instanceof PlayerData)
				return ((PlayerData)instance).serializeNBT();
			return null;
		}

		@Override
		public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
			if(instance instanceof PlayerData && nbt instanceof NBTTagCompound)
				((PlayerData)instance).deserializeNBT((NBTTagCompound) nbt);
		}
	}

	static class PlayerData implements IPlayerData, ICapabilityProvider, INBTSerializable<NBTTagCompound> {
		private double money = 0;
		private double bankMoney = 0;
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("money", this.money);
			tag.setDouble("bankmoney", this.bankMoney);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.money = nbt.getInteger("money");
			this.bankMoney = nbt.getInteger("bankmoney");
		}

		@Override
		public boolean hasCapability(Capability<?> capability,
				@Nullable EnumFacing facing) {
			return (PLAYER_DATA != null && capability == PLAYER_DATA);
		}

		@Override
		public <T> T getCapability(Capability<T> capability,
				@Nullable EnumFacing facing) {
			if (PLAYER_DATA != null && capability == PLAYER_DATA)
				return PLAYER_DATA.cast(this);
			return null;
		}

		@Override
		public double getMoney() {
			return this.money;
		}

		@Override
		public void setMoney(double amount) {
			this.money = amount;
		}

		@Override
		public double getBankMoney() {
			return this.bankMoney;
		}

		@Override
		public void setBankMoney(double amount) {
			this.bankMoney = amount;
		}



	}
}
