package cori.EssentialAlchemy.render;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.client.ClientProxy;
import cori.EssentialAlchemy.potions.PotionModifier;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@SideOnly(Side.CLIENT)
public class PotionModifierRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		
		double unit = 0.0625;
		
		renderer.setRenderBounds(0, 8 * unit, 0, 1, 1, 1);
		Lib.renderStandardInventory(block, renderer, 0);
		
		renderer.setRenderBounds(
				unit * 5, unit * 2, unit * 5, 
				unit * 11, unit * 8, unit * 11);
		Lib.renderStandardInventory(block, renderer, 0);
		
		PotionModifier.sideSwitch = true;
		renderer.setRenderBounds(0,0,0,1,unit * 2, 1);
		Lib.renderStandardInventory(block, renderer, 0);
		PotionModifier.sideSwitch = false;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		double unit = 0.0625;
		renderer.renderAllFaces = true;
		
		renderer.setRenderBounds(0, 8 * unit, 0, 1, 1, 1);
		renderer.renderStandardBlock(block, x, y, z);
		
		renderer.setRenderBounds(unit * 5, unit*2, unit * 5, unit * 11, unit * 8, unit * 11);
		renderer.renderStandardBlock(block, x, y, z);
		
		PotionModifier.sideSwitch = true;
		renderer.setRenderBounds(0,0,0,1,unit * 2, 1);
		renderer.renderStandardBlock(block, x, y, z);
		PotionModifier.sideSwitch = false;
		renderer.renderAllFaces = false;
		
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.potionModifierRenderType;
	}

}
