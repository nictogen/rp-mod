package com.afg.rpmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.jobs.Job;
import com.afg.rpmod.jobs.Job.EnumJobType;

public class SendJobChoiceToServer implements IMessage{
	private EnumJobType job;
	public SendJobChoiceToServer(){}
	public SendJobChoiceToServer(EnumJobType job){
		this.job = job;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.job = Job.EnumJobType.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {	
		buf.writeInt(job.getID());
	}

	public static class Handler implements IMessageHandler<SendJobChoiceToServer, IMessage> {
		@Override
		public IMessage onMessage(SendJobChoiceToServer message, MessageContext ctx){
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runner(message, ctx));
			return null;
		}
	}

	public static class Runner implements Runnable{
		private SendJobChoiceToServer message;
		private MessageContext ctx;
		public Runner(SendJobChoiceToServer message, MessageContext ctx){
			this.message = message;
			this.ctx = ctx;
		}

		@Override
		public void run() {
			IPlayerData data = (IPlayerData) ctx.getServerHandler().playerEntity.getCapability(IPlayerData.PLAYER_DATA, null);
			if(data != null)
				data.setJob(message.job.createJob(ctx.getServerHandler().playerEntity));
		}

	}
}