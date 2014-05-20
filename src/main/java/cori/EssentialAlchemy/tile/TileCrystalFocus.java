package cori.EssentialAlchemy.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.nodes.INode;
import java.lang.reflect.Field;
import java.util.Random;

import javax.rmi.CORBA.Tie;

import cori.EssentialAlchemy.EssentialAlchemy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileCrystalFocus extends BaseTile implements IEssentiaTransport, IAspectContainer {

	public Aspect aspect;
	public boolean isCompound;
	public int count;
	
	// Logic / Processing
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	//private INode targetNode;
	private ChunkCoordinates NodeCoords;
	
	static Class TileCrystal;
	static Field orientation;
	
	public TileCrystalFocus() {
		try { 
			TileCrystal = Class.forName("thaumcraft.common.tiles.TileCrystal");
			orientation = TileCrystal.getField("orientation");
		} catch (Exception e) {
			
		}
	}
	
	
	private Random random = new Random();
	
	int idleTick = 0;
	@Override
	public void updateEntity() {
		ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
		
		// For debug, highlight the area in question
		/*worldObj.spawnParticle("depthsuspend",
				cacheX+random.nextFloat(), 
				cacheY+random.nextFloat(), 
				cacheZ+random.nextFloat(), 0, 0, 0);*/
		
		if (++idleTick < 0) return; // Allow for idling
		
		//ForgeDirection dir = ForgeDirection.getOrientation(blockMetadata);
		
		idleTick = -100; // Can't find the crystal? Only bother checking every five seconds
		TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
		
		if (te == null) { // No Crystal on us
			aspect = null; // Clear our aspect and count
			count = 0;
			return; 
		}
		
		int meta = worldObj.getBlockMetadata(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ); // Attached Crystal Meta
		
		// Figure out the crystal type so we can know what kind of node we will be dealing with
		if (te.getClass().equals(TileCrystal)) { // If the tile connected to us is a crystal
			try {
				int ort = orientation.getInt(te);
				if (ort != meta) {
					// Change the orientation and update (not working?)
					orientation.setInt(te, meta);
					worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
				}
			} catch (Exception e) {
				// Not a huge deal
			}
			
			idleTick = -50; // Only wait 2.5s between crystal type checks
			
			// Figure out our target aspect
			
			isCompound = false;
			switch (meta) {
			case 0 :
				aspect = Aspect.AIR;
				break;
			case 1: 
				aspect = Aspect.FIRE;
				break;
			case 2:
				aspect = Aspect.WATER;
				break;
			case 3:
				aspect = Aspect.EARTH;
				break;
			case 4:
				aspect = Aspect.ORDER;
				break;
			case 5:
				aspect = Aspect.ENTROPY;
				break;
			case 6:
				isCompound = true;
			default: 
				aspect = null;
			}
			
			if (aspect == null || !aspect.isPrimal()) { // Shard is mixed shard - Figure out the aspect type?
				idleTick = -200;
				
				INode in = findOrCacheNode();
				if (in == null)
					return;
				
				// For compound aspect draining - Only really going to care about first compound, unless suction asks
				for (Aspect a : in.getAspects().getAspects()) {
					if (a.isPrimal()) continue;
						aspect = a;
				}
			}
			
			
		}
	}
	
	private int cacheX, cacheY, cacheZ;
	private INode findOrCacheNode() {
		World w = getWorldObj();
		// Check our cached position for a node
		TileEntity te = w.getTileEntity(cacheX, cacheY, cacheZ);
		if (te == null || !(te instanceof INode)) {
			INode inode = getNode(w);
			if (inode == null) return null;
			
			/*cacheX = te.xCoord;
			cacheY = te.yCoord;
			cacheZ = te.zCoord;*/
			return inode;
		} 
		return (INode)te;
	}
	
	private INode getNode(World w) {
		
		int x = xCoord, y = yCoord, z = zCoord;
		ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
		
		x += dir.offsetX; y += dir.offsetY; z += dir.offsetZ;
		
		int i =0;
		do {
			x += dir.offsetX; y += dir.offsetY; z += dir.offsetZ;
			TileEntity te = w.getTileEntity(x, y, z);
			if (te == null) continue;
			if (te instanceof INode) {
				cacheX = te.xCoord;
				cacheY = te.yCoord;
				cacheZ = te.zCoord;
				//EssentialAlchemy.lg.warn(String.format("Found Node at %d,  %d, %d"),cacheX,cacheY,cacheZ);
				EssentialAlchemy.lg.warn("Found Node At " + cacheX +"," +cacheY+","+cacheZ);
				return (INode)te;
			}
		} while (++i<8);
		
		return null;
	}
	
	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		
		if (aspect != null || isCompound) { 
			// Look for the node to draw from
			ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
			
			INode node = findOrCacheNode();
			if (node == null) return 0; // There was no node after all.
			
			/*TileEntity te = worldObj.getTileEntity(
					xCoord+(dir.offsetX*2),
					yCoord+(dir.offsetY*2),
					zCoord+(dir.offsetZ*2));
			
			if (!(te instanceof INode)) {
				// TODO: Break the crystal if improperly setup? (Flux goo maybe?)
				return 0; // There isn't a node to pull
			}*/
			
			//if (aspect == null) return 0; // screwit
			
			//INode node = (INode)te;
			
			//if (node.containerContains(aspect) <= 5) return 0; // Skip if it doesn't contain our aspect
			if (node.getAspects().getAmount(aspect) <= 2) return 0; // Skip for real
			
			if (node.takeFromContainer(aspect, 1)) {
				// Skip the sound effect, we aren't doing lightning anymore
				/*worldObj.playSoundEffect(
						xCoord, yCoord, zCoord,  // Pos
						"thaumcraft:shock",  // Sound
						0.1f, // Vol
						(random.nextFloat() * 0.4f) + 1f); // Pitch*/
				
				//float rx = (float) random.nextGaussian(), ry = (float) random.nextGaussian(), rz = (float) random.nextGaussian();
				float rx = random.nextFloat(), ry = random.nextFloat(), rz = random.nextFloat();
				rx *= rx; ry *= ry; rz *= rz; // Exponential
				rx *= random.nextBoolean() ? 0.3f : -0.3f; 
				ry *= random.nextBoolean() ? 0.3f : -0.3f;
				rz *= random.nextBoolean() ? 0.3f : -0.3f;
				
				EssentialAlchemy.streamFx(
						worldObj, 
						cacheX+0.5f+rx,
						cacheY+0.5f+ry,
						cacheZ+0.5f+rz,
						xCoord+dir.offsetX+0.5f,
						yCoord+dir.offsetY+0.5f,
						zCoord+dir.offsetZ+0.5f,
						aspect.getColor());
				
				worldObj.markBlockForUpdate(cacheX, cacheY, cacheZ);
				
				return 1; // Essentia successfully drawn
			}
		}
		return 0;
	}
	
	// Save / Load
	@Override
	public void WriteCustom(NBTTagCompound nbtc) {
		super.WriteCustom(nbtc);
		if (aspect != null)
			nbtc.setString("Aspect", aspect.getTag());
		nbtc.setInteger("count", count);
		nbtc.getBoolean("compound");
		nbtc.setInteger("cacheX", cacheX);
		nbtc.setInteger("cacheY", cacheY);
		nbtc.setInteger("cacheZ", cacheZ);
	}
	
	@Override
	public void ReadCustom(NBTTagCompound nbtc) {
		super.ReadCustom(nbtc);
		aspect = Aspect.getAspect(nbtc.getString("Aspect"));
		count = nbtc.getInteger("count");
		isCompound = nbtc.getBoolean("compound");
		
		cacheX = nbtc.getInteger("cacheX");
		cacheY = nbtc.getInteger("cacheY");
		cacheZ = nbtc.getInteger("cacheZ");
	}
	
	// IEssentiaTransport
	
	@Override
	public boolean isConnectable(ForgeDirection face) {
		return face.getOpposite() == ForgeDirection.getOrientation(getBlockMetadata());
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		return false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return face == ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		return null;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		return aspect;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		return count;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		return true;
	}
	
	// IAspect container Methods
	
	@Override
	public AspectList getAspects() {
		AspectList al = new AspectList();
		if (aspect != null) {
			al.add(aspect, 0);
			
			INode in = findOrCacheNode();
			if (in != null)
				al.add(aspect,in.getAspects().getAmount(aspect));
		}
		return al;
	}

	@Override
	public void setAspects(AspectList aspects) {}

	@Override
	public boolean doesContainerAccept(Aspect tag) {return false; }

	@Override
	public int addToContainer(Aspect tag, int amount) { return 0; }

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) { return false; }

	@Override
	@Deprecated
	public boolean takeFromContainer(AspectList ot) { return false; }

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) { return false; }

	@Override
	@Deprecated
	public boolean doesContainerContain(AspectList ot) {return false;}

	@Override
	public int containerContains(Aspect tag) {return 0;}
}
