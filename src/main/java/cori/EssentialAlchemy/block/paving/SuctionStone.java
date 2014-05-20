package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.ITickedByTile;
import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.tile.TileEffectStone;
import thaumcraft.api.aspects.Aspect;

public class SuctionStone extends EffectBlock implements ITickedByTile {

	public SuctionStone() {
		super(Aspect.VOID, 2);
		core = Aspect.VOID;
		
		setBlockName("SuckStone");
		SetCharge(1, 1, 1);
		centerColor = new Color(0); // It's not black, just dark grey thanks to the blend function
		//setBlockTextureName("essentialalchemy:suctionStone");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void OnTileUpdate(World w, TileEntity te, int x, int y, int z) {
		TileEffectStone tfs = (TileEffectStone)te;
		
		List<Entity> Entities;
		
		boolean isEmpowered = tfs.counter > 0;
		if (!isEmpowered && tfs.getAspects().visSize() > 0) {
			if (tfs.takeFromContainer(core, 1)) {
				tfs.counter = 50; // Refuel the counter
				w.markBlockForUpdate(x, y, z); // Update
				isEmpowered = true; // Remain empowered
			}
		}
		
		IEntitySelector selector = new IEntitySelector() { 
			@Override
			public boolean isEntityApplicable(Entity e) {
				return e instanceof EntityItem;
			}
		};
		
		// Get the area based on if we are empowered or not
		float fx = x, fy = y, fz = z;
		fx+=0.5f; fy += 1; fz += 0.5f;
		AxisAlignedBB bounding;
		if (isEmpowered) 
			bounding = AxisAlignedBB.getBoundingBox(fx-3.5, fy-0.1f, fz-3.5, fx+3.5, fy+0.5, fz+3.5);
		else
			bounding = AxisAlignedBB.getBoundingBox(fx-1.5, fy-0.1f, fz-1.5, fx+1.5, fy+0.5, fz+1.5);
			
		Entities = w.selectEntitiesWithinAABB(EntityItem.class, bounding,selector);
		
		Entity best = null;
		double maxDist = Double.POSITIVE_INFINITY;
		for(Entity e : Entities) {
			// Find closest-ish (taxicab) item
			double dist = Math.max(Math.abs(fx-e.posX),Math.abs(fz-e.posZ));
			if (dist < maxDist) {
				maxDist = dist;
				best = e; // running best
			}
		}
		
		if (best == null) return; // No item, no deal
		
		//EssentialAlchemy.lg.warn("Pulling in item!");
		// Draw it in!
		double xOff = fx-best.posX, zOff = fz-best.posZ;
		double speed = isEmpowered ? 0.1 : 0.02;
		speed *= Math.min(1, maxDist); // Slow down when we get close
		best.motionX += xOff > 0 ? speed : -speed;
		best.motionZ += zOff > 0 ? speed : -speed;
		tfs.counter -= 1;
		
		// Do some rad sparkles! (Is this too close to TT? I realized in retrospect this is very close to the magnet thing)
		if (maxDist > 0.8f)
		EssentialAlchemy.sparkle((float)best.posX, (float)best.posY, (float)best.posZ, 0x88888888);
		
		// If our maximum distance is less than 0.65 (aka, right near the edges!)
		if (maxDist < 0.65) {
			if (best instanceof EntityItem && !best.isDead) {
				// We have our item, find the inventory
				TileEntity invTile = w.getTileEntity(x, y+1, z);
				if (invTile instanceof IInventory) { // Inventory found, do it
					EntityItem ei = (EntityItem)best;
					ItemStack eis = ei.getEntityItem();
					
					tfs.counter -= eis.stackSize; // Remove essence based on how much is moved
					ItemStack afterInsert = Lib.insertStack((IInventory)invTile, eis, 1, true);
					w.markBlockForUpdate(x, y+1, z); // Update the inventory
					if (afterInsert == null) {
						ei.setDead();
						return;
					} else {
						ei.setEntityItemStack(afterInsert); 
					}
				}
			}
		}
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		
		PotionEffect pe = new PotionEffect(Potion.blindness.id, 1, 5, true);
		eb.addPotionEffect(pe);
	}

}
