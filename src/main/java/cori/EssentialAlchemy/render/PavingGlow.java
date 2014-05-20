package cori.EssentialAlchemy.render;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.CommonProxy;
import cori.EssentialAlchemy.IChargedTile;
import cori.EssentialAlchemy.block.paving.SuctionStone;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class PavingGlow extends TileEntitySpecialRenderer {

	ResourceLocation rl;
	public PavingGlow() {
		rl = new ResourceLocation("essentialalchemy:textures/blocks/PavingStoneCharged.png");
	}
	
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
			double z, float scale) {
		Minecraft.getMinecraft().renderEngine.bindTexture(rl);
		
		
		
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y + 1.001, z);
		
		// For Suction stone, if under an inventory, draw the highlight over it
		if (te.getBlockType() instanceof SuctionStone)
			if (te.getWorldObj().getTileEntity(te.xCoord, te.yCoord+1, te.zCoord) instanceof IInventory) {
				GL11.glScaled(2, 1, 2);
				GL11.glTranslated(-0.25, 0, -0.25);
			}
				//GL11.glTranslated(0, 1, 0);
		
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive
		
		//GL11.glDisable(GL11.GL_DEPTH_WRITEMASK);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		Tessellator t = Tessellator.instance;
		//if (te instanceof IChargedTile) {
		//	((IChargedTile)te).setChargedColor(t);
		//} else t.setColorRGBA_F(1, 1, 1, 1);
		t.setColorOpaque_F(1, 1, 1);
		GL11.glColor4f(1,1,1,1);
		if (te instanceof IChargedTile)
			((IChargedTile)te).setChargedColor();
		
		t.startDrawingQuads();
		t.setBrightness((15 << 20 | 15 << 4)); // Max brightness on both types
		//t.setBrightness(0xFFFFFF);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		t.addVertexWithUV(0, 0, 0, 0, 0);
		t.addVertexWithUV(0, 0, 1, 0, 1);
		t.addVertexWithUV(1, 0, 1, 1, 1);
		t.addVertexWithUV(1, 0, 0, 1, 0);
		
		t.draw();
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		//GL11.glEnable(GL11.GL_DEPTH_WRITEMASK);
		
		GL11.glPopMatrix();
	}

}
