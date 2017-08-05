package com.afg.rpmod;

import com.afg.rpmod.blocks.*;
import com.afg.rpmod.blocks.CookPan.CookPanTE;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.capabilities.IPlayerData.PlayerData;
import com.afg.rpmod.capabilities.IPlayerData.Storage;
import com.afg.rpmod.client.render.tileentities.CookPanTESR;
import com.afg.rpmod.commands.CommandAcceptTrade;
import com.afg.rpmod.commands.CommandTrade;
import com.afg.rpmod.entities.NPCJobGiver;
import com.afg.rpmod.network.SendJobChoiceToServer;
import com.afg.rpmod.network.UpdateClientPlayerData;
import com.afg.rpmod.network.UpdateTileEntityServer;
import com.afg.rpmod.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.lang.reflect.Field;

@Mod(
		modid = RpMod.MODID,
		name = "RP Mod",
		version = RpMod.VERSION,
		clientSideOnly = false,
		serverSideOnly = false
		)
@Mod.EventBusSubscriber
public class RpMod
{

	public static final String MODID = "rp-mod";
	public static final String VERSION = "0.2";
	public static SimpleNetworkWrapper networkWrapper;
	public static ResourceLocation guiTextures = new ResourceLocation(RpMod.MODID, "textures/gui/background.png");
	@SidedProxy(clientSide="com.afg.rpmod.proxy.ClientProxy", serverSide="com.afg.rpmod.proxy.CommonProxy")
	public static CommonProxy proxy;	

	@Mod.Instance
	public static RpMod instance;

	@ObjectHolder(MODID)
	public static class Blocks
	{
		public static final Block cityBlock = null;
		public static final Block plotBlock = null;
		public static final Block apartmentBlock = null;
		public static final Block apartmentDoor = null;
		public static final Block cookPan = null;
	}

	@ObjectHolder(MODID)
	public static class Items
	{
		public static final Item cityBlock = null;
		public static final Item plotBlock = null;
		public static final Item apartmentBlock = null;
		public static final Item apartmentDoor = null;
		public static final Item cookPan = null;
	}


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "npcJobGiver"), NPCJobGiver.class, "NPCJobGiver", 0, this, 64, 20, false);
		EntityRegistry.registerEgg(new ResourceLocation(MODID, "npcJobGiver"), Color.black.getRGB(), Color.blue.getRGB());

		proxy.registerRenders();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(IPlayerData.class, new Storage(), PlayerData.class);
		proxy.registerEventHandlers();
		int netIndex = 0;
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		networkWrapper.registerMessage(UpdateClientPlayerData.Handler.class, UpdateClientPlayerData.class,
				netIndex++, Side.CLIENT);
		networkWrapper.registerMessage(UpdateTileEntityServer.Handler.class, UpdateTileEntityServer.class,
				netIndex++, Side.SERVER);
		networkWrapper.registerMessage(SendJobChoiceToServer.Handler.class, SendJobChoiceToServer.class,
				netIndex++, Side.SERVER);

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandTrade());
		event.registerServerCommand(new CommandAcceptTrade());
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(new CityBlock().setRegistryName(RpMod.MODID, "cityBlock"), 
				new PlotBlock().setRegistryName(RpMod.MODID, "plotBlock"), 
				new ApartmentBlock().setRegistryName(RpMod.MODID, "apartmentBlock"),
				new ApartmentDoor().setRegistryName(RpMod.MODID, "apartmentDoor"),
				new CookPan("cookPan").setRegistryName(RpMod.MODID, "cookPan"));
		GameRegistry.registerTileEntity(CityBlock.CityBlockTE.class, RpMod.MODID + "_cityBlock");
		GameRegistry.registerTileEntity(PlotBlock.PlotBlockTE.class, RpMod.MODID + "_plotBlock");
		GameRegistry.registerTileEntity(ApartmentBlock.ApartmentBlockTE.class, RpMod.MODID + "_apartmentBlock");
		GameRegistry.registerTileEntity(ApartmentDoor.ApartmentDoorTE.class, RpMod.MODID + "_apartmentDoor");
		GameRegistry.registerTileEntity(CookPan.CookPanTE.class, RpMod.MODID + "_cookPan");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) throws Exception
	{
		Block[] blocks = {
				Blocks.cityBlock,
				Blocks.plotBlock,
				Blocks.apartmentBlock,
				Blocks.cookPan
		};
		for (Block block : blocks){
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()).setUnlocalizedName(block.getUnlocalizedName()));
		}
		event.getRegistry().register(new ItemDoor(Blocks.apartmentDoor).setRegistryName(Blocks.apartmentDoor.getRegistryName()).setUnlocalizedName("ApartmentDoor"));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) throws Exception
	{
		OBJLoader.INSTANCE.addDomain(RpMod.MODID);
		for (Field f : Items.class.getDeclaredFields()){
			Item item = (Item) f.get(null);
			ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			if(item.getUnlocalizedName().contains("cookPan")){
				ModelBakery.registerItemVariants(item, loc);
				ModelLoader.setCustomMeshDefinition(item, stack -> loc);
			}
		}
		ModelLoader.setCustomStateMapper(
				Blocks.apartmentDoor, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build()
				);

		ClientRegistry.bindTileEntitySpecialRenderer(CookPanTE.class, new CookPanTESR());
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
