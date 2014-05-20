package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.util.Random;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.tile.TileEffectStone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class ColdStone extends EffectBlock {

	public ColdStone() {
		super(Aspect.COLD, 32);
		setBlockName("ColdStone");
		//setBlockTextureName("essentialalchemy:PavingStoneFrost");
		
		SetCharge(0.6f, 0.6f, 0.8f); // Light blue-ish
		
		setTickRandomly(true);
		centerColor = new Color(0.6f,0.6f,0.8f);
	}
	
	@Override
	public void updateTick(World w, int x, int y, int z, Random r) {
		IAspectContainer ia = (IAspectContainer)w.getTileEntity(x, y, z);
		int ox = x, oy = y, oz = z;
		
		boolean isCharged = false;
		if (ia.getAspects().getAmount(Aspect.COLD) > 1) {
			isCharged = true;
		}
		
		// Now figure out about placing snow, pick one of the cells above us random-like
		y+=1;
		
		for (int i = isCharged ? 0 : 2; i < 3; ++i) { // Increase range if charged
			if (w.rand.nextBoolean()) x+=1;
			if (w.rand.nextBoolean()) x-=1;
			if (w.rand.nextBoolean()) z+=1;
			if (w.rand.nextBoolean()) z-=1;
		}
		
		Block current = w.getBlock(x, y, z);
		if (current == Blocks.snow_layer) return; // Skip as we have filled that one already
		
		boolean expend = false;
		
		// I actually am finding I like it more when it places snow illegally
		//if (current.getMaterial() == Material.air /*&& Blocks.snow_layer.canPlaceBlockAt(w, x, y, z)*/) {
		if(current.getMaterial() == Material.air && 
				(w.getBlock(x, y-1, z).isSideSolid(w, x, y, z, ForgeDirection.UP) || w.getBlock(x, y, z) == Blocks.ice)) {
			current.breakBlock(w, x, y, z, current, w.getBlockMetadata(x, y, z));
			w.setBlock(x, y, z, Blocks.snow_layer);
			expend = true;
		} else { // If we can't place the snow, see if we are next to water - if so, freeze it!
			y -= 1;
			current = w.getBlock(x, y, z);
			if (current == Blocks.water || current == Blocks.flowing_water) {
				w.setBlock(x, y, z, Blocks.ice);
				expend = true;
			}
		}
		
		if (isCharged && expend) {
			ia.getAspects().remove(Aspect.COLD, 1); // Remove one
			w.markBlockForUpdate(ox, oy, oz);
			// A nice wispy-stream wave for effect
			EssentialAlchemy.streamFx(w, 
					x+0.5f, y, z+0.5f, 
					x+w.rand.nextFloat(), y, z+w.rand.nextFloat(),
					Aspect.COLD.getColor());
			w.scheduleBlockUpdate(ox, oy, oz, this, 5); // Make that next update much faster
		} else {
			if (isCharged) {
				if(w.rand.nextFloat() < 0.1f) { // 10% change of wasting essence, so it drains and stops needing fast updates.
					ia.getAspects().remove(Aspect.COLD, 1); // Remove one
					w.markBlockForUpdate(ox, oy, oz);
					EssentialAlchemy.streamFx(w, 
							x+0.5f, y, z+0.5f, 
							x+w.rand.nextFloat(), y, z+w.rand.nextFloat(),
							Aspect.COLD.getColor());
				}
				w.scheduleBlockUpdate(ox, oy, oz, this, 20); // Check again slightly more delayed than if we were successful
			}
		}
	}
	
	@Override
	public int tickRate(World p_149738_1_) {
		return 200; // 10s - too slow?
	}
	
	@Override
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		//super.ApplyEffect(eb, te);
		if (eb instanceof EntityPlayer) return; // Not going to touch the player
		
		PotionEffect pe = new PotionEffect(8, 100, -2); // Negative Amps why are you so hard
		eb.addPotionEffect(pe);
	}
}
