package cori.EssentialAlchemy.client;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.ModifierCyclePacket;
import cori.EssentialAlchemy.tile.ContainerModifier;
import cori.EssentialAlchemy.tile.TilePotionModifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiPotionModifier extends GuiContainer {
	public static ResourceLocation rl;
	
	TilePotionModifier tile;
	
	GuiPotionModifier(InventoryPlayer ip, TilePotionModifier tpm) {
		super(new ContainerModifier(ip, tpm));
		tile = tpm;
		
		
		if (rl == null)
			rl = new ResourceLocation("essentialalchemy:textures/gui/gui_modifier.png");
	}

	int xStart, yStart;
	
	ModifierButton modButton;
	
	@Override
	public void initGui() {
		super.initGui();
		
		xStart = (width - xSize) / 2;
		yStart = (height - ySize) / 2;
		
		modButton = new ModifierButton(this,1,xStart + 72,yStart + 42);
		modButton.Mode = tile.mode;
		
		buttonList.add(modButton);
	}
	
	@Override
	protected void actionPerformed(GuiButton gb) {
		if (gb.id == 1) {
			EssentialAlchemy.packetPipe.SendToServer(new ModifierCyclePacket(tile, 1));
			
			modButton.Mode += 1;
			if (modButton.Mode > 4)
				modButton.Mode = 0;
			
			if (modButton.Mode == 3)
				++modButton.Mode;
			
			tile.mode = modButton.Mode; // Update our local copy predictively
			tile.enabled = false;
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_,
			int p_146979_2_) {
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(rl);
		
		GL11.glColor4f(1, 1, 1, 1);
		
		drawTexturedModalRect(xStart, yStart,0 , 0, xSize, ySize);
		
		// Draw the tube filliness
		if (tile.isProcessing()) {
			AspectList reqList = tile.getRequirements();//.visSize();
			if (reqList != null) {
				int req = reqList.visSize();
				int cur = tile.getAspects().visSize();
				
				int width = (int) (35 * (cur / (float)req));
				drawTexturedModalRect(xStart+70, yStart+33, 183, 14, width, 8);//(int) (35 * (cur / (float)req)));
			}
		}
		
		// Draw the enabled doodad
		if (tile.enabled)
			drawTexturedModalRect(xStart + 126, yStart + 45, 184, 29, 8, 8);
		
		// Draw the tube part
		drawTexturedModalRect(xStart + 66 , yStart + 32, 179, 1, 43, 11);
		
		// Draw the glowing section if active
		if (tile.isProcessing())
			drawTexturedModalRect(xStart+120, yStart+15, 0, 236, 20, 20);
		
		// Draw the aspect requirements
		tile.refreshMask();
		if (tile.isModValid()) {
			AspectList al = tile.getRequirements();
			if (al != null) {
				Aspect[] aa = al.getAspects();
				
				UtilsFX.drawTag(xStart + 80, yStart + 10, aa[0], al.getAmount(aa[0]), 0, 0);
			}
			
		}
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	// See if the user is clicking the virtual 'enable' button
	@Override
	protected void mouseClicked(int mx, int my, int par3) {
		super.mouseClicked(mx, my, par3);
		
		mx -= xStart;
		my -= yStart;
		
		mx -= 126;
		my -= 45;
		
		if (mx < 0 || my < 0) return;
		if (mx > 8 || my > 8) return;
		
		tile.enabled = !tile.enabled;
		
		EssentialAlchemy.packetPipe.SendToServer(new ModifierCyclePacket(tile,2));
		
		tile.getWorldObj().playSoundEffect(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, "thaumcraft:cameraclack", 0.4F, 1);
	}
}
