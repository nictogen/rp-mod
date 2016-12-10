package com.afg.rpmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.afg.rpmod.blocks.ApartmentBlock;
import com.afg.rpmod.blocks.ApartmentDoor;
import com.afg.rpmod.blocks.CityBlock;
import com.afg.rpmod.blocks.InventorTable;
import com.afg.rpmod.blocks.PlotBlock;
import com.afg.rpmod.capabilities.IPlayerData;
import com.afg.rpmod.capabilities.IPlayerData.PlayerData;
import com.afg.rpmod.capabilities.IPlayerData.Storage;
import com.afg.rpmod.handlers.GuiHandler;
import com.afg.rpmod.jobs.Inventor;
import com.afg.rpmod.jobs.Job;
import com.afg.rpmod.jobs.crafting.CancelableShapedOreRecipe;
import com.afg.rpmod.jobs.crafting.CancelableShapedRecipe;
import com.afg.rpmod.jobs.crafting.CancelableShapelessOreRecipe;
import com.afg.rpmod.jobs.crafting.CancelableShapelessRecipe;
import com.afg.rpmod.network.UpdateClientDiscoveryData;
import com.afg.rpmod.network.UpdateClientPlayerData;
import com.afg.rpmod.network.UpdateTileEntityServer;
import com.afg.rpmod.proxy.CommonProxy;

@Mod(
		modid = RpMod.MODID,
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
        public static final Block inventorTableStone = null;
    }
    
    @ObjectHolder(MODID)
    public static class Items
    {
        public static final Item cityBlock = null;
        public static final Item plotBlock = null;
        public static final Item apartmentBlock = null;
        public static final Item apartmentDoor = null;
        public static final Item inventorTableStone = null;
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
		networkWrapper.registerMessage(UpdateClientDiscoveryData.Handler.class, UpdateClientDiscoveryData.class,
				netIndex++, Side.CLIENT);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		//Replace crafting recipes with custom ones
		List<Item> changedRecipes = new ArrayList<Item>();
		changedRecipes.addAll(Job.getAllExclusiveRecipes());
		changedRecipes.addAll(Inventor.getDiscoverableItems());
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		//Create duplicate to avoid concurrent modification
		List<IRecipe> vanillaRecipes = new ArrayList<IRecipe>();
		vanillaRecipes.addAll(recipeList);
		
 		for(IRecipe recipe : vanillaRecipes){
			if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() != null && changedRecipes.contains(recipe.getRecipeOutput().getItem())){
				recipeList.remove(recipe);
				if(recipe instanceof ShapelessRecipes){
					recipeList.add(new CancelableShapelessRecipe(recipe.getRecipeOutput(), ((ShapelessRecipes) recipe).recipeItems));
				} else if (recipe instanceof ShapedRecipes){
					ShapedRecipes sRecipe = (ShapedRecipes) recipe;
					recipeList.add(new CancelableShapedRecipe(sRecipe.recipeWidth, sRecipe.recipeHeight, sRecipe.recipeItems, sRecipe.getRecipeOutput()));
				} else if (recipe instanceof ShapedOreRecipe){
					recipeList.add(new CancelableShapedOreRecipe((ShapedOreRecipe) recipe));
				} else if (recipe instanceof ShapelessOreRecipe){
					recipeList.add(new CancelableShapelessOreRecipe((ShapelessOreRecipe) recipe));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(new CityBlock().setRegistryName(RpMod.MODID, "cityBlock"), 
				new PlotBlock().setRegistryName(RpMod.MODID, "plotBlock"), 
				new ApartmentBlock().setRegistryName(RpMod.MODID, "apartmentBlock"),
				new ApartmentDoor().setRegistryName(RpMod.MODID, "apartmentDoor"),
				new InventorTable().setRegistryName(RpMod.MODID, "inventorTableStone"));
		GameRegistry.registerTileEntity(CityBlock.CityBlockTE.class, RpMod.MODID + "_cityBlock");
		GameRegistry.registerTileEntity(PlotBlock.PlotBlockTE.class, RpMod.MODID + "_plotBlock");
		GameRegistry.registerTileEntity(ApartmentBlock.ApartmentBlockTE.class, RpMod.MODID + "_apartmentBlock");
		GameRegistry.registerTileEntity(ApartmentDoor.ApartmentDoorTE.class, RpMod.MODID + "_apartmentDoor");
		GameRegistry.registerTileEntity(InventorTable.InventorTableTE.class, RpMod.MODID + "_inventorTableStone");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) throws Exception
	{
		Block[] blocks = {
				Blocks.cityBlock,
				Blocks.plotBlock,
				Blocks.apartmentBlock,
				Blocks.inventorTableStone
		};
		for (Block block : blocks){
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
		event.getRegistry().register(new ItemDoor(Blocks.apartmentDoor).setRegistryName(Blocks.apartmentDoor.getRegistryName()));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) throws Exception
	{
		for (Field f : Items.class.getDeclaredFields()){
			Item item = (Item) f.get(null);
			ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			
		}
		ModelLoader.setCustomStateMapper(
			   	Blocks.apartmentDoor, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build()
			);
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
