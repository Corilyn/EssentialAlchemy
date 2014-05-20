package cori.EssentialAlchemy.client;

import org.lwjgl.opengl.GL11;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.Lib;

import thaumcraft.api.aspects.Aspect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BindFX extends EntityFX {

	public BindFX(World w, double x, double y, double z,
			double dx, double dy, double dz) {
		super(w, x, y, z, dx, dy, dz);
		init();
	}

	public BindFX(World w, double x, double y, double z) {
		super(w, x, y, z);
		init();
	}
	
	void init(){
		particleMaxAge = 30; // 3s
	}
	
	// Screw such calculations as gravity
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		// Vent out some of the aspect to show the effect
		/*if (rand.nextFloat() < 0.2) {
			float 	rx = rand.nextFloat() - 0.5f,
					rz = rand.nextFloat() - 0.5f;
			EssentialAlchemy.drawVent(worldObj, prevPosX+rx, prevPosY, prevPosZ+rz, 0, 0.05f, 0, Aspect.TRAP.getColor());
		}*/
		
		if (particleAge++ > particleMaxAge)
			setDead();
	}
	
	
	static ResourceLocation rl;
	static ResourceLocation getRL() {
		if (rl == null)
			rl = new ResourceLocation("thaumcraft:textures/misc/home.png");
		return rl;
	};
	
	@Override
	public void renderParticle(Tessellator t, float interp,
			float x, float y, float z, float scaleX, float scaleY) {
		
		t.draw(); // Draw the given tesselator so we can start
		
		GL11.glPushMatrix();
		//GL11.glTranslatef(x-0.5f, y+0.1f, z-0.5f); // 'centered' on the point
		// Using Thaumcrafts "Home" texture for golems for binding
		Minecraft.getMinecraft().renderEngine.bindTexture(getRL());
		
		GL11.glColor4f(1, 1, 1, 1); // Full bright
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		//GL11.glBlendFunc(770, 1); // Presumed Alpha Blend?
		// Alpha Blend that
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		float itrX = (float)(prevPosX + (posX - prevPosX) * (double)interp - interpPosX);
        float itrY = (float)(prevPosY + (posY - prevPosY) * (double)interp - interpPosY);
        float itrZ = (float)(prevPosZ + (posZ - prevPosZ) * (double)interp - interpPosZ);
        
		GL11.glTranslated(itrX, itrY, itrZ);
		GL11.glRotatef(particleAge*3, 0, 1, 0); // Spin
		GL11.glTranslated(-0.5, 0, -0.5);
		
		t.startDrawingQuads(); // Quads
		//t.setColorOpaque_I(Aspect.TRAP.getColor());
		t.setColorRGBA_I(Aspect.TRAP.getColor(), 255-(particleAge*8)); // Testing as full-white
		//t.setTranslation(x, y, z); // ??
		// Draw the quad
		t.addVertexWithUV(0, 0.1, 1, 0, 1);
		t.addVertexWithUV(1, 0.1, 1, 1, 1);
		t.addVertexWithUV(1, 0.1, 0, 1, 0);
		t.addVertexWithUV(0, 0.1, 0, 0, 0);
		t.draw();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(Lib.getParticleTexture());
		t.startDrawingQuads();
	}
	
	// Render as fullbright (doh)
	@Override
	public float getBrightness(float par1) {
		return 1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 1;
	}
}
