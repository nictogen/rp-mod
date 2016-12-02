package com.afg.rpmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.afg.rpmod.blocks.IUpdatesFromClient;
import com.afg.rpmod.blocks.PlotBlock.PlotBlockTE;

public class UpdateTileEntityServer implements IMessage{
	private NBTTagCompound tag;
	private BlockPos pos;
	public UpdateTileEntityServer(){}
	public UpdateTileEntityServer(NBTTagCompound tag, BlockPos pos){
		this.tag = tag;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tag = ByteBufUtils.readTag(buf);
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {	
		ByteBufUtils.writeTag(buf, this.tag);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	public static class Handler implements IMessageHandler<UpdateTileEntityServer, IMessage> {
		@Override
		public IMessage onMessage(UpdateTileEntityServer message, MessageContext ctx){
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runner(message, ctx));
			return null;
		}
	}

	public static class Runner implements Runnable{
		private UpdateTileEntityServer message;
		private MessageContext ctx;
		public Runner(UpdateTileEntityServer message, MessageContext ctx){
			this.message = message;
			this.ctx = ctx;
		}

		@Override
		public void run() {
			TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if(te != null && te instanceof IUpdatesFromClient)
				((IUpdatesFromClient) te).updateServerData(message.tag);
		}

	}
}