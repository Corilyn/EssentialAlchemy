package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.Random;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.tile.TileEffectStone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class TransitStone extends EffectBlock {

	public TransitStone() {
		super(Aspect.ELDRITCH, 8); // Consider Aspect - Travel?
		
		setBlockName("TransitStone");
		//setBlockTextureName("essentialalchemy:PavingStoneEnder");
		SetCharge(0.5f, 0.3f, 0.5f); // Ender-Purple
		centerColor = new Color(0.5f,0.3f,0.5f); // Match the purple
	}
	
	static Random r = new Random();
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		for(Object ob : eb.getActivePotionEffects()) {
			if (!(ob instanceof PotionEffect))
				continue;
			PotionEffect curEffect = (PotionEffect)ob;
			
			// Don't teleport a player actively slowed
			if (curEffect.getPotionID() == 2)
				return; 
			
		}
		
		TileEffectStone tbs = (TileEffectStone)te;
		
		ForgeDirection heading = ForgeDirection.UP;
		int yaw = (int)eb.rotationYaw;
		yaw += 360; yaw+=22; yaw%=360;
		int facing = yaw/90;
		switch(facing) {
		case 0 :
			heading = ForgeDirection.SOUTH;
			break;
		case 1 : 
			heading = ForgeDirection.WEST;
			break;
		case 2 :
			heading = ForgeDirection.NORTH;
			break;
		case 3 :
			heading = ForgeDirection.EAST;
			break;
		}
		
		// Debug teleport facing
		//EssentialAlchemy.lg.warn("Teleporting with heading " + heading.toString());
		
		float arriveX = te.xCoord + 0.5f, arriveY = te.yCoord + 1.1f, arriveZ = te.zCoord + 0.5f;
		
		
		if (tbs.getAspects().visSize() >= 1) { // Teleport forward 8 blocks
			arriveX += heading.offsetX * 8;
			arriveY += heading.offsetY * 8;
			arriveZ += heading.offsetZ * 8;
			
			tbs.ventEffect(5);
			tbs.getAspects().reduce(core, 1); // Remove one
		} else {
			arriveX += heading.offsetX * 2;
			arriveY += heading.offsetY * 2;
			arriveZ += heading.offsetZ * 2;
		}
		
		// Teleport walker as if by enderpearl
		EnderTeleportEvent event = new EnderTeleportEvent(eb, 
				arriveX, 
				arriveY, // Slightly up 
				arriveZ,
				0.0f);
		MinecraftForge.EVENT_BUS.post(event);
		
		eb.setPositionAndUpdate(
				arriveX,
				arriveY,
				arriveZ );
		
		// Have the player be 'hit' for trivial damage
		eb.attackEntityFrom(DamageSource.fall, 5F); // Featherfalling should still work!
		
		// Port Sound
		eb.playSound("mob.endermen.portal", 0.4f, 1);
		te.getWorldObj().playSoundEffect(eb.posX, eb.posY, eb.posZ, "mob.endermen.portal", 0.4f, 1f);
		
		PotionEffect pe = new PotionEffect(2 /* SLOW */,5,0,true); // Slow for 0.25s
		eb.addPotionEffect(pe);
		
		// Spawn portal-y effects
		for (int i = 0; i < 32; ++i) {
			te.getWorldObj().spawnParticle(
					"portal", 
					arriveX, 
					arriveY + r.nextDouble(), 
					arriveZ, 
					r.nextGaussian(), 
					0, 
					r.nextGaussian());
		}
	}
}
