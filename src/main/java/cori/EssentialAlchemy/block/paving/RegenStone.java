package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import cori.EssentialAlchemy.tile.TileEffectStone;

public class RegenStone extends EffectBlock {

	public RegenStone() {
		super(Aspect.HEAL,4);
		
		setBlockName("RegenStone");
		//setBlockTextureName("essentialalchemy:PavingStoneHeal");
		
		centerColor = new Color(0xFF0000);
		SetCharge(0.8f, 0, 0); // Red
	}

	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		// First, do we have the desired aspect?
		IAspectContainer container = (IAspectContainer)te;
		
		if (container.getAspects().visSize() >= 2) {
			// Does Entity already have Resistance effect?
			Collection col = eb.getActivePotionEffects();
			for(Object ob : col) {
				if (!(ob instanceof PotionEffect))
					continue;
				
				PotionEffect currentEffect = (PotionEffect)ob;
				if (!(currentEffect.getPotionID() == 10))  // If not Regen effect
					continue;
				
				if (currentEffect.getAmplifier() == 0) { // If they have Regen I
					// Found effect to magnify!
					container.getAspects().reduce(core, 4); // Chew through that Sano
					//EssentialAlchemy.lg.warn("Removing 4 " + core.getName());
					PotionEffect pe = new PotionEffect(Potion.regeneration.id /* Regen */,100,4,true); // Regen V for 5s, aka, 10-Health?
					eb.addPotionEffect(pe);
					((TileEffectStone)te).ventEffect(20); // Vent for 1s
					te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord); // Update the visible counter
				}
			}
		}
		
		// Unlike the stone of resistance, only refresh the effect, don't top off
		
		PotionEffect active = eb.getActivePotionEffect(Potion.regeneration);
		
		if (active == null) {
			// Regeneration I for 2.5s, Ambient
			PotionEffect pe = new PotionEffect(Potion.regeneration.id,50,0,true); 
			eb.addPotionEffect(pe);
		}
	}
}
