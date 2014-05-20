package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.Random;

import thaumcraft.api.aspects.Aspect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.tile.TileEffectStone;

public class BiteStone extends EffectBlock {

	public BiteStone() {
		super(Aspect.WEAPON, 8);
		
		setBlockName("BiteStone");
		centerColor = new Color(0xc05050); // .75, .3, .3
		SetCharge(0.75f, 0.3f, 0.3f);
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		TileEffectStone tfs = (TileEffectStone)te;
		
		int amount = tfs.getAspects().visSize();
		if (amount > 1) {
			tfs.ventEffect(20); 
			eb.attackEntityFrom(DamageSource.magic, amount * 4f);
			tfs.getAspects().reduce(core, amount);
			Random r = eb.worldObj.rand;
			for(int i = 0; i < amount; ++i) {
				EssentialAlchemy.sparkle(
						te.xCoord + r.nextFloat(), 
						te.yCoord + r.nextFloat() + 1, 
						te.zCoord + r.nextFloat(),
						core.getColor());
			}
		}
		
		// Apply a negative resistance
		PotionEffect pe = new PotionEffect(Potion.resistance.id,20,-2,true);
		eb.addPotionEffect(pe);
	}
}
