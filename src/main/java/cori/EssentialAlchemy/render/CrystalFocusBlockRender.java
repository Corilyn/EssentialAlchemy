package cori.EssentialAlchemy.render;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.block.CrystalFocus;
import cori.EssentialAlchemy.client.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CrystalFocusBlockRender implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block b, int metadata, int modelId,
			RenderBlocks r) {
		//Tessellator t = Tessellator.instance;
		
		metadata = 1; // Draw it facing up
		
		Lib.rotateUV(1, r);
		setupVentFace(metadata, r);
		Lib.renderStandardInventory(b, r, metadata);
		setupVentBody(metadata, r);
		Lib.renderStandardInventory(b, r, metadata);
		Lib.unrotateUV(r);
		
		//Lib.drawFaces(renderer, block, CrystalFocus.Crucible4);
	}

	private double lidMin = 0.76f;
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks r) {
		
		//renderer.renderStandardBlock(block, x, y, z);
		r.renderAllFaces = true;
		int meta = world.getBlockMetadata(x, y, z);
		Lib.rotateUV(meta, r);
		setupVentFace(meta, r);
		r.renderStandardBlock(block, x, y, z);
		setupVentBody(meta, r);
		r.renderStandardBlock(block, x, y, z);
		Lib.unrotateUV(r);
		r.renderAllFaces = false;
		
		return true;
	}

	private void setupVentFace(int meta,
			RenderBlocks renderer) {
		
		renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
		
		switch(ForgeDirection.getOrientation(meta)) {
		case UP:
			renderer.renderMinY = lidMin;
			break;
		case DOWN:
			renderer.renderMaxY = 1-lidMin;
			break;
		case EAST:
			renderer.renderMinX = lidMin;
			break;
		case WEST:
			renderer.renderMaxX = 1-lidMin;
			break;
		case NORTH:
			renderer.renderMaxZ = 1-lidMin;
			break;
		case SOUTH:
			renderer.renderMinZ = lidMin;
			break;
		default:
			break;
		}
	}
	
	private double ventEnd = 0.125;
	private double ventCap = 0.9;
	
	private void setupVentBody(int meta, RenderBlocks r) {
		r.setRenderBounds(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
		
		switch(ForgeDirection.getOrientation(meta)) {
			case UP:
				r.renderMinY = ventEnd;
				r.renderMaxY = ventCap;
				return;
			case DOWN:
				r.renderMinY = 1-ventCap;
				r.renderMaxY = 1-ventEnd;
				return;
			case EAST:
				r.renderMinX = ventEnd;
				r.renderMaxX = ventCap;
				return;
			case WEST:
				r.renderMinX = 1-ventCap;
				r.renderMaxX = 1-ventEnd;
				return;
			case SOUTH:
				r.renderMinZ = ventEnd;
				r.renderMaxZ = ventCap;
				return;
			case NORTH:
				r.renderMinZ = 1-ventCap;
				r.renderMaxZ = 1-ventEnd;
				return;
			default:
				return;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.crystalFocusRenderType;
	}
	
}
