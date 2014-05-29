package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.List;

import thaumcraft.api.aspects.Aspect;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.ITickedByTile;

public class BindStone extends EffectBlock implements ITickedByTile {

	public BindStone() {
		super(Aspect.TRAP, 4);
		
		setBlockName("BindStone");
		centerColor = new Color(core.getColor());
		SetCharge(0.6f, 0.5f, 0.5f);
	}

	@Override
	public void OnTileUpdate(World w, TileEntity te, int x, int y, int z) {
		//EssentialAlchemy.lg.warn("Ticking Tile");
		TileEffectStone tfs = (TileEffectStone)te;
		if (tfs.getAspects().visSize() < 1) return;
		
		List<Entity> Entities;
		
		// Get an AABB of of the 3x2x3 area centered above us
		double dx = x, dy = y, dz = z;
		dx+=0.5; dy += 1; dz += 0.5;
		AxisAlignedBB bounding = AxisAlignedBB.getAABBPool().getAABB(dx-1.5, dy-0.1, dz-1.5, dx+1.5, dy+3, dz+1.5);
			
		IEntitySelector selector = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity var1) {
				return true;
			}
		};
		
		Entities = w.selectEntitiesWithinAABB(IMob.class, bounding,selector);
		
		// Terminate after finding an applicable entity
		for(Entity e : Entities) {
			//EssentialAlchemy.lg.warn("Found Entity " + e.toString());
			if (!(e instanceof EntityLivingBase)) return;
			EntityLivingBase elb = (EntityLivingBase) e;
			//if (elb == null) return; // If not valid
			//EntityMob em = (EntityMob)e;
			PotionEffect pe = elb.getActivePotionEffect(Potion.moveSlowdown);
			if (pe == null || pe.getAmplifier() < 9) { // Entity isn't bound
				elb.addPotionEffect(
					new PotionEffect(Potion.moveSlowdown.id,30,9,false)); // for 1.5s
				
				// Stream in both directions to show an obvious link:
				EssentialAlchemy.streamFx(w, dx, dy, dz, (float)e.posX, (float)e.posY, (float)e.posZ, core.getColor());
				EssentialAlchemy.streamFx(w, e.posX, e.posY, e.posZ, (float)dx, (float)dy, (float)dz, core.getColor());
				
				EssentialAlchemy.proxy.BindFX(w, (float)e.posX, (float)e.posY, (float)e.posZ);
				
				// 50% drain chance - So it should cost 1 Viniculum per 10s of binding
				if (w.rand.nextFloat() < 0.16f) { // About 1:6 for that burn rate
					tfs.ventEffect(5); // Very short puff
					tfs.getAspects().reduce(core, 1); // Drain
					w.markBlockForUpdate(x, y, z);
				}
				return;
			}
		}
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		PotionEffect pe = new PotionEffect(Potion.moveSlowdown.id,40,5,true);
		eb.addPotionEffect(pe);
	}
}
