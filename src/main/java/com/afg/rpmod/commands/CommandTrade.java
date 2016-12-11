package com.afg.rpmod.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import com.afg.rpmod.capabilities.IPlayerData;

public class CommandTrade extends CommandBase{

	public static ArrayList<TradeOffer> pendingOffers = new ArrayList<TradeOffer>();

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "trade";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "'/trade <playername> <youroffer> <theiroffer>'. Proper offer format is either '$<amount>' or '<item> <amount>' ";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.<String>emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 4 || args.length > 5)
			return;
		EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
		if(entityplayer == sender.getCommandSenderEntity())
			return;
		if(args[1].contains("$")){
			Item item = getItemByText(sender, args[2]);
			int offerAmount = parseInt(args[1].substring(1), 1);
			int amount = parseInt(args[3], 1);
			TradeOffer offer = new TradeOffer((EntityPlayer) sender.getCommandSenderEntity(), entityplayer, offerAmount, item, amount);
			this.addOffer(offer);
			entityplayer.addChatComponentMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " wants to trade " + args[1] + " for " + amount + " " + I18n.format(item.getUnlocalizedName() + ".name")));
			((EntityPlayer) sender.getCommandSenderEntity()).addChatComponentMessage(new TextComponentString("You sent a trade to " + entityplayer.getName() + " asking for " + amount + " " + I18n.format(item.getUnlocalizedName() + ".name") + " for " + args[1]));
		} else {
			Item item = getItemByText(sender, args[1]);
			int offerAmount = parseInt(args[2], 1);
			if(args[3].contains("$")){
				int askingAmount =  parseInt(args[3].substring(1), 1);
				TradeOffer offer = new TradeOffer((EntityPlayer) sender.getCommandSenderEntity(), entityplayer, item, offerAmount, askingAmount);
				this.addOffer(offer);
				entityplayer.addChatComponentMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " wants to trade " + offerAmount + " " + I18n.format(item.getUnlocalizedName() + ".name") + " for " + args[3]));
				((EntityPlayer) sender.getCommandSenderEntity()).addChatComponentMessage(new TextComponentString("You sent a trade to " + entityplayer.getName() + " asking for " + args[3] + " for " + offerAmount + " " + I18n.format(item.getUnlocalizedName() + ".name")));
			} else {
				Item askingItem = getItemByText(sender, args[3]);
				int askingAmount = parseInt(args[4], 1);
				TradeOffer offer = new TradeOffer((EntityPlayer) sender.getCommandSenderEntity(), entityplayer, item, offerAmount, askingItem, askingAmount);
				this.addOffer(offer);
				entityplayer.addChatComponentMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " wants to trade " + offerAmount + " " + I18n.format(item.getUnlocalizedName() + ".name") + " for " + askingAmount + " " + I18n.format(askingItem.getUnlocalizedName() + ".name")));
				((EntityPlayer) sender.getCommandSenderEntity()).addChatComponentMessage(new TextComponentString("You sent a trade to " + entityplayer.getName() + " asking for " + askingAmount + " " + I18n.format(askingItem.getUnlocalizedName() + ".name") + " for " + offerAmount + " " + I18n.format(item.getUnlocalizedName() + ".name")));
			}
		}
	}

	public void addOffer(TradeOffer offer){
		ArrayList<TradeOffer> currentOffers = new ArrayList<TradeOffer>();
		currentOffers.addAll(pendingOffers);
		for(TradeOffer o : currentOffers)
			if(offer.offerer == o.offerer)
				if(offer.offeree == o.offeree)
					pendingOffers.remove(o);
		pendingOffers.add(offer);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server,
			ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if(args.length == 1)
			return getListOfStringsMatchingLastWord(args, server.getAllUsernames());
		else if((args.length == 2 && !args[1].contains("$")) || 
				(args.length == 4 && !args[3].contains("$") && isNumerical(args[2])) ||
				(args.length == 3 && args[1].contains("$")))
			return(getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys()));
		else 
			return Collections.<String>emptyList();
	}

	private boolean isNumerical(String s){
		char[] validNum = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		for(int i = 0; i < s.length(); i++){
			boolean isNumber = false;
			for(char c : validNum)
				if(c == s.charAt(i))
					isNumber = true;
			if(!isNumber)
				return false;
		}
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		if(index == 0)
			return true;

		return false;
	}


	public static class TradeOffer{

		EntityPlayer offerer;
		EntityPlayer offeree;
		private Mode mode;
		private Item itemOffer;
		private int itemOfferAmount;
		private Item itemReturn;
		private int itemReturnAmount;
		private int moneyOffer, moneyReturn;

		private static enum Mode{
			MONEY_FOR_ITEM(),
			ITEM_FOR_MONEY(),
			ITEM_FOR_ITEM();
		}
		private TradeOffer(EntityPlayer offerer, EntityPlayer offeree){
			this.offeree = offeree;
			this.offerer = offerer;
		}
		public TradeOffer(EntityPlayer offerer, EntityPlayer offeree, int moneyOffer, Item itemReturn, int itemReturnAmount){
			this(offerer, offeree);
			this.mode = Mode.MONEY_FOR_ITEM;
			this.moneyOffer = moneyOffer;
			this.itemReturn = itemReturn;
			this.itemReturnAmount = itemReturnAmount;
		}
		public TradeOffer(EntityPlayer offerer, EntityPlayer offeree, Item itemOffer, int itemOfferAmount, int moneyReturn){
			this(offerer, offeree);
			this.mode = Mode.ITEM_FOR_MONEY;
			this.itemOffer = itemOffer;
			this.itemOfferAmount = itemOfferAmount;
			this.moneyReturn = moneyReturn;
		}
		public TradeOffer(EntityPlayer offerer, EntityPlayer offeree,  Item itemOffer, int itemOfferAmount, Item itemReturn, int itemReturnAmount){
			this(offerer, offeree);
			this.mode = Mode.ITEM_FOR_ITEM;
			this.itemOffer = itemOffer;
			this.itemOfferAmount = itemOfferAmount;
			this.itemReturn = itemReturn;
			this.itemReturnAmount = itemReturnAmount;
		}

		public TextComponentBase doTrade(){
			if(this.offeree == null || this.offeree.isDead){
				return createRedString("You can't accept trades when you're dead.");
			}
			if(this.offerer == null || this.offerer.isDead){
				return createRedString("You can't accept trades when the offerer is dead.");
			}
			if(this.offerer.getDistanceToEntity(this.offeree) > 10){
				return createRedString("You are too far away from each other to trade.");
			}
			IPlayerData offererData = this.offerer.getCapability(IPlayerData.PLAYER_DATA, null);
			IPlayerData offereeData = this.offeree.getCapability(IPlayerData.PLAYER_DATA, null);
			InventoryPlayer offererInv = this.offerer.inventory;
			InventoryPlayer offereeInv = this.offeree.inventory;	

			switch(this.mode){
			case MONEY_FOR_ITEM:
				
				if(offererData.getMoney() < this.moneyOffer){
					return createRedString(this.offerer.getName() + " doesn't have enough money to complete this trade.");
				}
				if(!hasItemAmount(offereeInv, this.itemReturn, this.itemReturnAmount))
					return createRedString(this.offeree.getName() + " doesn't have enough " + I18n.format(itemReturn.getUnlocalizedName() + ".name") + " to complete this trade.");
				//TODO check for room in inv
				offererData.setMoney(offererData.getMoney() - this.moneyOffer);
				offereeData.setMoney(offereeData.getMoney() + this.moneyOffer);
				this.tradeItem(offereeInv, offererInv,  this.itemReturn, this.itemReturnAmount);
				return createGreenString("Trade Complete!");
			case ITEM_FOR_MONEY:
				if(offereeData.getMoney() < this.moneyOffer)
					return createRedString(this.offeree.getName() + " doesn't have enough money to complete this trade.");
				if(!hasItemAmount(offererInv, this.itemOffer, this.itemOfferAmount))
					return createRedString(this.offerer.getName() + " doesn't have enough " + I18n.format(itemOffer.getUnlocalizedName() + ".name") + " to complete this trade.");
				//TODO check for room in inv
				offereeData.setMoney(offereeData.getMoney() - this.moneyOffer);
				offererData.setMoney(offererData.getMoney() + this.moneyOffer);
				this.tradeItem(offererInv, offereeInv,  this.itemReturn, this.itemReturnAmount);
				return createGreenString("Trade Complete!");
			case ITEM_FOR_ITEM:
				if(!hasItemAmount(offererInv, this.itemOffer, this.itemOfferAmount))
					return createRedString(this.offerer.getName() + " doesn't have enough " + I18n.format(itemOffer.getUnlocalizedName() + ".name") + " to complete this trade.");
				if(!hasItemAmount(offereeInv, this.itemReturn, this.itemReturnAmount))
					return createRedString(this.offeree.getName() + " doesn't have enough " + I18n.format(itemReturn.getUnlocalizedName() + ".name") + " to complete this trade.");
				
				this.tradeItemForItem(offererInv, offereeInv, itemOffer, itemOfferAmount, itemReturn, itemReturnAmount);
				return createGreenString("Trade Complete!");
			default:
				return createRedString("");
			}
		}

		public void tradeItemForItem(InventoryPlayer offerInv, InventoryPlayer returnInv, Item itemOffer, int amountOffer, Item itemReturn, int amountReturn){
			List<ItemStack> toOfferer = this.removeStacks(returnInv, itemReturn, amountReturn);
			List<ItemStack> toOfferee = this.removeStacks(offerInv, itemOffer, amountOffer);
			
			for(ItemStack stack: toOfferer)
				offerInv.addItemStackToInventory(stack);
			for(ItemStack stack: toOfferee)
				returnInv.addItemStackToInventory(stack);
		}
		
		public List<ItemStack> removeStacks(InventoryPlayer from, Item item, int amount){
			List<ItemStack> transfer = new ArrayList<ItemStack>();
			ItemStack addOnly = null;
			int amountToTransfer = 0;

			for(ItemStack stack : from.mainInventory){
				if(stack != null && stack.getItem() == item){
					if(amountToTransfer + stack.stackSize <= amount){
						amountToTransfer += stack.stackSize;
						transfer.add(stack);
					} else if(amountToTransfer < amount){
						int size = amount - amountToTransfer;
						amountToTransfer += size;
						ItemStack temp = stack.copy();
						temp.stackSize = size;
						//TODO I think I can do this?
						stack.stackSize -= size;
						addOnly = temp;
					}
				}
			}
			for(ItemStack stack : transfer){
				from.deleteStack(stack);
			}
			if(addOnly != null)
				transfer.add(addOnly);
			
			return transfer;
		}
		
		public void tradeItem(InventoryPlayer from, InventoryPlayer to, Item item, int amount){
			List<ItemStack> transfer = this.removeStacks(from, item, amount);
			for(ItemStack stack: transfer)
				to.addItemStackToInventory(stack);
		}

		public boolean hasItemAmount(InventoryPlayer inventory, Item item, int amount){
			int amountHas = 0;
			for(ItemStack stack : inventory.mainInventory){
				if(stack != null && stack.getItem() == item)
					amountHas += stack.stackSize;
			}
			return amountHas >= amount;
		}
		
		public static TextComponentString createRedString(String s){
			TextComponentString string = new TextComponentString(s);
			string.getStyle().setColor(TextFormatting.RED);
			return string;
		}
		
		public static TextComponentString createGreenString(String s){
			TextComponentString string = new TextComponentString(s);
			string.getStyle().setColor(TextFormatting.GREEN);
			return string;
		}
	}
}
