package cori.EssentialAlchemy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cori.EssentialAlchemy.block.paving.VenomStone;
import cori.EssentialAlchemy.client.GuiHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import cori.EssentialAlchemy.Net.PacketHandler;

@Mod(modid = EssentialAlchemy.MODID, version = EssentialAlchemy.VERSION,dependencies="required-after:Thaumcraft")
public class EssentialAlchemy
{
    public static final String MODID = "EssentialAlchemy";
    public static final String VERSION = "0.001a";
    
    @Instance(value="EssentialAlchemy")
    public static EssentialAlchemy instance;
    
    @SidedProxy(clientSide="cori.EssentialAlchemy.client.ClientProxy",serverSide="cori.EssentialAlchemy.client.CommonProxy")
    public static CommonProxy proxy;
    
    public static final Logger lg = LogManager.getLogger("EssentialAlchemy");
    
    // Some paving stones
    public static Block 
    	ProtectStone, RegenStone, TransitStone, ColdStone, 
    	SuckStone, VenomStone, BindStone, BiteStone, HealthStone;
    public static Block PotionModifier;
	public static Item ArcanePotion;
    
	public static final PacketHandler packetPipe = new PacketHandler();
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {	
    	packetPipe.init();
    	packetPipe.registerPacket(ModifierCyclePacket.class);
    	
    	try {
    		Class thaum = Class.forName("thaumcraft.common.Thaumcraft");
    		thaumTab = (CreativeTabs) thaum.getField("tabTC").get(null);
    	} catch (Exception e) {
    		lg.warn("Couldn't map the Thaumcraft creative tab");
    		lg.warn(e.toString());
    		thaumTab = CreativeTabs.tabMisc;
    	}
    	
    	proxy.RegisterBlocks();
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    	//ResistStone.initStones();
    	
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	packetPipe.finalizeInit();
    	
    	Research.registerRecipes();
    	Research.registerPages();
    }
    
    public static CreativeTabs thaumTab;
    public static Object thaumProxy;
    
    public static Object getThaumProxy() {
    	try {
    		if (thaumProxy == null) {
    			Class placeholder = Class.forName("thaumcraft.common.Thaumcraft");
				thaumProxy = placeholder.getField("proxy").get(null); // Attempt to grab the proxy
    		}
    		return thaumProxy;
    	} catch (Exception e) {
    		lg.warn("Proxy map failed");
			e.printStackTrace();
    	}
		return null; // If all else fails.
    }
    
    private static Method drawVentParticles;
	public static void drawVent(World world,double x, double y, double z,double vx, double vy, double vz,int color) {
		
		try {
			if (drawVentParticles == null )
				drawVentParticles = getThaumProxy().getClass().getMethod("drawVentParticles",
						World.class,
						double.class,double.class,double.class, // X Y Z
						double.class,double.class,double.class, // vX, vY, vZ
						int.class);
			
			drawVentParticles.invoke(getThaumProxy(),world, x,y,z,vx,vy,vz,color);
		} catch (Exception e) {
			lg.warn("Vent effect failed");
			e.printStackTrace();
		}
	}
	
	private static Method drawBolt;
	public static void bolt(World w, float x, float y, float z, float dx, float dy, float dz) {
		try {
			if (drawBolt == null) 
				drawBolt = getThaumProxy().getClass().getMethod("nodeBolt", World.class,
						float.class,float.class,float.class,
						float.class,float.class,float.class);
			drawBolt.invoke(getThaumProxy(),
					w,
					x,y,z, 
					dx,dy,dz);
		} catch (Exception e) {
			lg.warn("Bolt effect failed");
			e.printStackTrace();
		}
	}
	
	private static Method wispFX;
	public static void wispFX(World w, float x, float y, float z, float dx, float dy, float dz) {
		try {
			if (wispFX == null) 
				wispFX = getThaumProxy().getClass().getMethod("nodeBolt", World.class,
						float.class,float.class,float.class,
						float.class,float.class,float.class);
			wispFX.invoke(getThaumProxy(),
					w,
					x,y,z, 
					dx,dy,dz);
		} catch (Exception e) {
			lg.warn("Bolt effect failed");
			e.printStackTrace();
		}
	}
	
	private static Method sparkle;
	public static void sparkle(float x, float y, float z, int color) {
		try {
			if (sparkle == null) 
				sparkle = getThaumProxy().getClass().getMethod("sparkle",
						float.class,float.class,float.class,
						int.class);
			sparkle.invoke(getThaumProxy(),
					x,y,z, 
					color);
		} catch (Exception e) {
			lg.warn("Sparkle effect failed");
			e.printStackTrace();
		}
	}
	
	private static Method streamFx;
	public static void streamFx(World w, double x, double y, double z, float dx, float dy, float dz, int tagColor) {
		try {
			if (streamFx == null) 
				streamFx = getThaumProxy().getClass().getMethod("sourceStreamFX", 
						World.class,
						double.class,double.class,double.class,
						float.class,float.class,float.class,
						int.class);
			streamFx.invoke(getThaumProxy(),
					w,
					x,y,z, 
					dx,dy,dz,tagColor);
		} catch (InvocationTargetException ite) {
			lg.warn("Stream effect exception");
			try {
				throw(ite.getCause());
			} catch (Throwable e) {
				lg.warn("Thaumcraft Internal Exception in effect - This message is harmless");
				//lg.warn("Internal Exception --");
				//e.printStackTrace();
			}
		} catch (Exception e) {
			lg.warn("Stream effect failed");
			e.printStackTrace();
		} 
	}
	
	private static Class TilePedestal;
	public static Class getPedestal() {
		if (TilePedestal == null)
			try {
				return TilePedestal = Class.forName("thaumcraft.common.tiles.TilePedestal");
			} catch (ClassNotFoundException e) {
				lg.warn("Couldn't map TilePedestal from Thaumcraft!");
				e.printStackTrace();
			}
		return TilePedestal;
	}
	
	private static Class BlockStoneDevice;
	public static Class getStoneDevice() {
		if (BlockStoneDevice == null)
			try {
				return BlockStoneDevice = Class.forName("thaumcraft.common.blocks.BlockStoneDevice");
			} catch (ClassNotFoundException e) {
				lg.warn("Couldn't map BlockStoneDevice from Thaumcraft!");
				e.printStackTrace();
			}
		return BlockStoneDevice;
	}
}
