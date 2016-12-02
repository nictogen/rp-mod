package com.afg.rpmod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

import com.afg.rpmod.blocks.CityBlock;
import com.afg.rpmod.blocks.PlotBlock;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.capabilities.IPlayerData.PlayerData;
import com.afg.rpmod.capabilities.IPlayerData.Storage;
import com.afg.rpmod.network.UpdateClientPlayerData;
import com.afg.rpmod.network.UpdateTileEntityServer;
import com.afg.rpmod.proxy.CommonProxy;

@Mod(
		modid = RpMod.VERSION,
		name = "RP Mod",
		version = RpMod.VERSION,
		clientSideOnly = false,
		serverSideOnly = false,
		dependencies = "required-after:Forge@[12.18.2.2171,)"
		)
@Mod.EventBusSubscriber
public class RpMod
{
	public static final String MODID = "rp-mod";
	public static final String VERSION = "0.1";
	public static SimpleNetworkWrapper networkWrapper;
	@SidedProxy(clientSide="com.afg.rpmod.proxy.ClientProxy", serverSide="com.afg.rpmod.proxy.CommonProxy")
	public static CommonProxy proxy;	
	
	
    @ObjectHolder(MODID)
    public static class Blocks
    {
        public static final Block cityBlock = null;
        public static final Block plotBlock = null;
    }
    
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(IPlayerData.class, new Storage(), PlayerData.class);
		this.proxy.registerEventHandlers();
		int netIndex = 0;
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		networkWrapper.registerMessage(UpdateClientPlayerData.Handler.class, UpdateClientPlayerData.class,
				netIndex++, Side.CLIENT);
		networkWrapper.registerMessage(UpdateTileEntityServer.Handler.class, UpdateTileEntityServer.class,
				netIndex++, Side.SERVER);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(new CityBlock().setRegistryName(RpMod.MODID, "cityBlock"), new PlotBlock().setRegistryName(RpMod.MODID, "plotBlock"));
		GameRegistry.registerTileEntity(CityBlock.CityBlockTE.class, RpMod.MODID + "_cityBlock");
		GameRegistry.registerTileEntity(PlotBlock.PlotBlockTE.class, RpMod.MODID + "_plotBlock");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) throws Exception
	{
		Block[] blocks = {
				Blocks.cityBlock,
				Blocks.plotBlock
		};
		for (Block block : blocks)
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

	}
	
	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> e){
		if(e.getObject() instanceof EntityPlayer){
			e.addCapability(new ResourceLocation(RpMod.MODID, "playerdata"), new PlayerData((EntityPlayer) e.getObject()));
		}
	}
	
	@SubscribeEvent
	public void cloneCapabilitiesEvent(PlayerEvent.Clone event)
	{
		//Should only fire on return from death, otherwise the data is already there
		if(event.isWasDeath()){
			PlayerData sho = (PlayerData) event.getOriginal().getCapability(IPlayerData.PLAYER_DATA, null);
			NBTTagCompound nbt = sho.serializeNBT();
			PlayerData shn = (PlayerData) event.getEntityPlayer().getCapability(IPlayerData.PLAYER_DATA, null);
			shn.deserializeNBT(nbt);
		}
	}



}
