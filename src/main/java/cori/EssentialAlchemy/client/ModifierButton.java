package cori.EssentialAlchemy.client;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.tile.TilePotionModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ModifierButton extends GuiButton {
	TilePotionModifier tile;
	public int Mode = 0;
	
	public ModifierButton(GuiPotionModifier gpm, int id, int x, int y) {
		super(id, x, y, 32, 32, "");
		tile = gpm.tile;
	}
	
	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_,
			int p_146112_3_) {
		
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiPotionModifier.rl);
		GL11.glEnable(GL11.GL_BLEND);
		
		if ((tile.modeMask & (1 << Mode)) != 0) {
			GL11.glColor4f(1, 1, 1, 1);
		} else {
			GL11.glColor4d(0.5, 0.5, 0.5, 0.5);
		}
			
			
		drawTexturedModalRect(xPosition, yPosition, 96 + (32 * Mode), 224, 32, 32);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
}
