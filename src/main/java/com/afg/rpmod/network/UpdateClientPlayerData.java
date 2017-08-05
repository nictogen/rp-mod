package com.afg.rpmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.capabilities.IPlayerData.PlayerData;

public class UpdateClientPlayerData implements IMessage{
	private NBTTagCompound tag;
	public UpdateClientPlayerData(){}
	public UpdateClientPlayerData(PlayerData data){
		this.tag = data.serializeNBT();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {	
		ByteBufUtils.writeTag(buf, this.tag);
	}

	public static class Handler implements IMessageHandler<UpdateClientPlayerData, IMessage> {
		@Override
		public IMessage onMessage(UpdateClientPlayerData message, MessageContext ctx){
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runner(message, ctx));
			return null;
		}
	}

	public static class Runner implements Runnable{
		private UpdateClientPlayerData message;
		private MessageContext ctx;
		public Runner(UpdateClientPlayerData message, MessageContext ctx){
			this.message = message;
			this.ctx = ctx;
		}

		@Override
		public void run() {
			PlayerData data = (PlayerData) Minecraft.getMinecraft().player.getCapability(IPlayerData.PLAYER_DATA, null);
			if(data != null)
				data.deserializeNBT(message.tag);
		}

	}
}