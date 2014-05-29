package cori.EssentialAlchemy.block.paving;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;

public class VenomStone extends EffectBlock {

	public VenomStone() {
		super(Aspect.POISON, 4);
		
		setBlockName("VenomStone");
		
		SetCharge(0.5f, 0.95f, 0);
		centerColor = new Color(core.getColor()); // Poison color (obviously)
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		TileEffectStone tfs = (TileEffectStone)te;
		
		// Empowered case
		if (tfs.getAspects().visSize() > 1) {
			PotionEffect cur = eb.getActivePotionEffect(Potion.poison);
			
			if (cur != null) // If the living has a weaker effect, skip it
				if (cur.getAmplifier() > 2) return; // Doh
			
			cur = new PotionEffect(Potion.poison.id,100,4,true); // Poison IV for 5s (ouch)
			eb.addPotionEffect(cur);
			
			tfs.getAspects().reduce(core, 1); // Remove one
			tfs.ventEffect(20); // Vent
			return;
		}
		
		// Normal case
		PotionEffect cur = eb.getActivePotionEffect(Potion.poison);
		
		if (cur != null) return; // Don't repoison, they don' take the damage then
		
		PotionEffect pe = new PotionEffect(Potion.poison.id,30,0,true); // Minimum Poison effect to register
		eb.addPotionEffect(pe);
	}
}
