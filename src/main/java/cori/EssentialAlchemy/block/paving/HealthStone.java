package cori.EssentialAlchemy.block.paving;

import thaumcraft.api.aspects.Aspect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;

public class HealthStone extends EffectBlock {

	public HealthStone() {
		super(Aspect.LIFE, 4);
		setBlockName("HealthStone");
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		PotionEffect pe;
		
		TileEffectStone tbs = (TileEffectStone)te;
		
		boolean amped = tbs.getAspects().visSize() > 1;
		
		if (eb.isEntityUndead()) // ID 21 is HealthBoost
			pe = new PotionEffect(21,amped ? 200 : 40,amped ? -3 : -1,amped);
		else // 40s on players, 20s on others, 2s unboosted
			pe = new PotionEffect(21,amped ? (eb instanceof EntityPlayer) ? 800 : 400 : 40,amped ?  4 : 0,amped);
		
		// figure out if the new effect is worth applying over the old one
		PotionEffect current = eb.getActivePotionEffect(Potion.field_76443_y); // Health Boost Potion (Forge get your damn mapping together)
		
		// If the potion *isn't* the amped variant
		if (current != null) {
			if (!(Math.abs(current.getAmplifier()) > 2 || amped)) {
				current.combine(pe);
				return;
			}
			
			if (current.getDuration() < 20) { 
				// If the duration is near expiring, refresh it
				current.combine(pe);
				//eb.addPotionEffect(pe);
				tbs.getAspects().reduce(core, 1);
				tbs.ventEffect(20);
			}
			return; // Ready to amp, but the effect isn't near expiring
		} else {
			// Refresh the sort lived effect
			//current.combine(pe);
			eb.addPotionEffect(pe);
		}
	}
}
