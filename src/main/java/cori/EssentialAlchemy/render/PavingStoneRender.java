package cori.EssentialAlchemy.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.block.paving.EffectBlock;
import cori.EssentialAlchemy.client.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PavingStoneRender implements ISimpleBlockRenderingHandler {

	private static float BYTEINV = 1f/255f;
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		
		Color c = new Color(((EffectBlock)block).getBlendColor());
		
		EffectBlock.BorderPhase = true;
		Lib.renderStandardInventory(block, renderer, 0);
		GL11.glColor3f(c.getRed() * BYTEINV, c.getGreen() * BYTEINV, c.getBlue() * BYTEINV);
		EffectBlock.BorderPhase = false;
		Lib.renderStandardInventory(block, renderer, 0);
		GL11.glColor3f(1, 1, 1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks r) {
		// Shockingly the simplest block renderer I've had to do
		Block b = world.getBlock(x, y, z);
		EffectBlock.BorderPhase = true;
		r.renderStandardBlock(b, x, y, z);
		EffectBlock.BorderPhase = false;
		r.renderStandardBlock(b, x, y, z);
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.pavingStoneRenderType;
	}

}
