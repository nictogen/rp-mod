package com.afg.rpmod.commands;

import com.afg.rpmod.commands.CommandTrade.TradeOffer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandAcceptTrade extends CommandBase{

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override public String getName()
	{
		return "accepttrade";
	}

	@Override public String getUsage(ICommandSender sender)
	{
		return "/accepttrade <player>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender,
			String[] args) throws CommandException {
		EntityPlayer entityplayer = getPlayer(server, sender, args[0]);

		TradeOffer remove = null;
		for(TradeOffer to : CommandTrade.pendingOffers){
			if(remove == null && to.offeree == sender && to.offerer == entityplayer){
				remove = to;
				TextComponentBase t = to.doTrade();
				if(sender instanceof EntityPlayer)
				{
					((EntityPlayer) sender).sendStatusMessage(t, true);
				}
			}
		}
		CommandTrade.pendingOffers.remove(remove);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if(args.length == 1)
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		else return Collections.<String>emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		if(index == 0)
			return true;

		return false;
	}

}
