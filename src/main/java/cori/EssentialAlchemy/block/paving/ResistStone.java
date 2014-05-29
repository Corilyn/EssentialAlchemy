package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class ResistStone extends EffectBlock {

	public ResistStone() {
		super(Aspect.ARMOR,4);
		
		setBlockName("ResistStone");
		//setBlockTextureName("essentialalchemy:PavingStoneResist");
		
		centerColor = new Color(0x00FFFF); // Cyan
		SetCharge(0, 0.8f, 0.8f); // Charge color is cyan
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
				if (!(currentEffect.getPotionID() == 11))  // If not resistance effect
					continue;
				
				if (currentEffect.getAmplifier() == 1) {
					// Found effect to magnify!
					((TileEffectStone)te).ventEffect(20); // Draw a vent for 1s
					//ventCore(eb.worldObj,te.xCoord,te.yCoord,te.zCoord); // Draw the essentia spraying out
					
					container.getAspects().remove(core, 2);
					// Resistance IV for 30s, Ambient
					PotionEffect pe = new PotionEffect(11 /* Resistance */,600,3,true); 
					eb.addPotionEffect(pe);
					te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord); // Update the visible counter
				}
			}
		}
		// Resistance II for 8s, Ambient
		PotionEffect pe = new PotionEffect(11 /* Resistance */,160,1,true); 
		eb.addPotionEffect(pe);
	}
}
