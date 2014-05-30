package cori.EssentialAlchemy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.block.paving.EffectBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class Lib {
	
	
	// After much digging, turns out you can just use the Piston-method to determine facing
	public static ForgeDirection getDir(EntityLivingBase eb, int x, int y, int z) {
		return ForgeDirection.getOrientation(BlockPistonBase.determineOrientation(eb.worldObj, x, y, z, eb));
	}
	
	public static int getDirInt(EntityLivingBase eb, int x, int y, int z) {
		return BlockPistonBase.determineOrientation(eb.worldObj, x, y, z, eb);
	}
	
	private static Method getBaubles;
	public static ItemStack[] playerBaubles(EntityPlayer p) {
		try {
			List<ItemStack> baubles = new ArrayList();
			if (getBaubles == null) {
				Class rfl = Class.forName("baubles.common.lib.PlayerHandler");
				getBaubles = rfl.getMethod("getPlayerBaubles", EntityPlayer.class);
			}
			IInventory inventoryBaubles = (IInventory) getBaubles.invoke(null, p);
			for(int i = 0; i < inventoryBaubles.getSizeInventory(); ++i) 
				baubles.add(inventoryBaubles.getStackInSlot(i));
			
			return baubles.toArray(new ItemStack[baubles.size()]);
		} catch (Exception e) {
			EssentialAlchemy.lg.warn("Error mapping baubles");
			e.printStackTrace();
		}
		return null;
	}
	
	private static Method insertStack;
	public static ItemStack insertStack(IInventory inv, ItemStack stack, int side, boolean doit) {
		try {
			if (insertStack == null) {
				Class rfl = Class.forName("thaumcraft.common.lib.InventoryHelper");
				insertStack = rfl.getMethod("insertStack", IInventory.class, ItemStack.class, int.class, boolean.class);
			}
			return (ItemStack)insertStack.invoke(null, inv,stack,side,doit);
		} catch (Exception e) {
			EssentialAlchemy.lg.warn("Could not map Thaumcraft InventoryHelper insertStack function");
			e.printStackTrace();
		}
		return null;
	}
	
	private static Method drawFaces;
	public static void drawFaces(RenderBlocks renderblocks, Block block, IIcon icon) {
		try {
			if (drawFaces == null) {
				Class rfl = Class.forName("thaumcraft.client.renderers.block.BlockRenderer");
				drawFaces = rfl.getMethod("drawFaces", RenderBlocks.class, Block.class, IIcon.class, boolean.class);
			}
			drawFaces.invoke(null, renderblocks,block,icon,true);
		} catch (Exception e) {
			EssentialAlchemy.lg.warn("Could not map Thaumcraft's DrawFaces function");
		}
	}
	
	private static Method getParticleTexture;
	public static ResourceLocation getParticleTexture() {
		try {
			if (getParticleTexture == null) {
				Class rfl = Class.forName("thaumcraft.client.lib.UtilsFX");
				getParticleTexture = rfl.getMethod("getParticleTexture");
			}
			return (ResourceLocation)getParticleTexture.invoke(null);
		} catch (Exception e) {
			EssentialAlchemy.lg.warn("Could not map Thaumcraft's Particle Texture function");
		}
		return null;
	}
	
	// Check Player Research
	private static Method isResearchComplete;
	public static boolean isResearchComplete(String playerName, String key) {
		try {
			if (isResearchComplete == null) {
				Class rfl = Class.forName("thaumcraft.common.lib.research.ResearchManager");
				isResearchComplete = rfl.getMethod("isResearchComplete",String.class, String.class);
			}
			return Boolean.TRUE.equals(isResearchComplete.invoke(null,playerName,key)); 
		} catch (Exception e) {
			EssentialAlchemy.lg.warn("Could not map Thaumcraft's isResearchComplete function");
			e.printStackTrace();
		}
		return false;
	}
	
	public static void rotateUV(int meta, RenderBlocks r) {
		switch (meta) {
			case 0:
				r.uvRotateEast = 3;
	            r.uvRotateWest = 3;
	            r.uvRotateSouth = 3;
	            r.uvRotateNorth = 3;
	            return;
			case 1:
			default:
				return;
					
			case 2:
				r.uvRotateSouth = 1;
	            r.uvRotateNorth = 2;
	            return;
			case 3:
				r.uvRotateSouth = 2;
	            r.uvRotateNorth = 1;
	            r.uvRotateTop = 3;
	            r.uvRotateBottom = 3;
	            return;
			case 4:
				r.uvRotateEast = 1;
	            r.uvRotateWest = 2;
	            r.uvRotateTop = 2;
	            r.uvRotateBottom = 1;
	            return;
			case 5:
				r.uvRotateEast = 2;
	            r.uvRotateWest = 1;
	            r.uvRotateTop = 1;
	            r.uvRotateBottom = 2;
	            return;
		}
	}
	
	public static void unrotateUV(RenderBlocks r) {
		r.uvRotateEast = 0;
        r.uvRotateWest = 0;
        r.uvRotateSouth = 0;
        r.uvRotateNorth = 0;
        r.uvRotateTop = 0;
        r.uvRotateBottom = 0;
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderStandardInventory(Block b, RenderBlocks r, int m) {
		Tessellator t = Tessellator.instance;
		
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		
		t.startDrawingQuads();
		t.setNormal(0, -1, 0);
		r.renderFaceYNeg(b, 0, 0, 0, b.getBlockTextureFromSide(0));
		t.draw();
		
		t.startDrawingQuads();
		t.setNormal(0, 1, 0);
		r.renderFaceYPos(b, 0, 0, 0, b.getBlockTextureFromSide(1));
		t.draw();
		
		t.startDrawingQuads();
		t.setNormal(0, 0, -1);
		r.renderFaceZNeg(b, 0, 0, 0, b.getBlockTextureFromSide(2));
		t.draw();
		
		t.startDrawingQuads();
		t.setNormal(0, 0, 1);
		r.renderFaceZPos(b, 0, 0, 0, b.getBlockTextureFromSide(3));
		t.draw();
		
		t.startDrawingQuads();
		t.setNormal(-1, 0, 0);
		r.renderFaceXNeg(b, 0, 0, 0, b.getBlockTextureFromSide(4));
		t.draw();
		
		t.startDrawingQuads();
		t.setNormal(1, 0, 0);
		r.renderFaceXPos(b, 0, 0, 0, b.getBlockTextureFromSide(5));
		t.draw();
		
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
	}
}
