package cori.EssentialAlchemy.block.paving;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectSourceHelper;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.IChargedTile;
import cori.EssentialAlchemy.ITickedByTile;
import cori.EssentialAlchemy.tile.BaseTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEffectStone extends BaseTile implements IChargedTile, IEssentiaTransport, IAspectContainer {
	
	//public int charges;
	//public boolean isPowered = false;
	
	public Aspect coreAspect;
	public int maximum = 16;
	
	private int ventTime = 0;
	
	public int counter = 0; // Used for specific blocks that do counting from block class (Polymorphism, Ho!)
	
	public void ventEffect(int time) {
		ventTime = time;
		worldObj.playSoundEffect(xCoord + 0.5D,yCoord + 1D, zCoord + 0.5D, "thaumcraft:bubble", 0.2f, 0.8f);
	}
	
	//public int ArmorHeld = 0;
	AspectList contained = new AspectList();
	
	/*public TileBuffStone(Aspect core) {
		coreAspect = core;
	}*/
	
	public TileEffectStone() {
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 576; // 24-blocks
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	private int drawTick = 0;
	
	//public void ventCore(World w,int x, int y, int z) {
	//	EssentialAlchemy.drawVent(w,x, y+0.5f, z, 0, 0.25f, 0, core.getColor());
	//}
	
	private Random r = new Random();
	
	@Override
	public void updateEntity() {
		if (--ventTime > 0) {
			// Draw the essentia venting as long as our vent time is positive
			for (int i = 0; i < 3; ++i) {
				float rx = (float)r.nextFloat(), ry = (float)r.nextFloat();
				rx *= 0.5f; ry *= 0.5f;
				EssentialAlchemy.drawVent(worldObj,xCoord+rx+0.25f, yCoord+1f, zCoord+ry+0.25f, 0, 0.25f, 0, coreAspect.getColor());
			}
		}
		
		// Update the block's on tick if it has one (Sucking, Biting, etc)
		Block thisBlock = getWorldObj().getBlock(xCoord, yCoord, zCoord);
		if (thisBlock instanceof ITickedByTile)
			((ITickedByTile)thisBlock).OnTileUpdate(worldObj, this, xCoord, yCoord, zCoord);
		
		if (contained.visSize() < maximum) {
			if (++drawTick % 5 != 0)
				return; // Only draw 4-essentia a second (one per five ticks)
			
			contained.add(coreAspect,0); // Have it display the symbol even if dry
			
			//if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
			if (getBlockMetadata() != 0) { // If the stone is augmented
				if (AspectSourceHelper.drainEssentia(this, coreAspect, ForgeDirection.UP, 8)) {
					contained.add(coreAspect,1);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			
			// Check all valid directions
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, xCoord, yCoord, zCoord, dir);
				if (te == null) continue;
				
				IEssentiaTransport ie = (IEssentiaTransport)te;
				// Is the pipe actually connected to us?
				ForgeDirection op = dir.getOpposite();
				
				if (!ie.canOutputTo(op))
					continue;
				
				if ((ie.getSuctionType(op) == coreAspect) && (ie.getSuctionAmount(op) < 128)) {
					//ArmorHeld += 1;
					
					int amt = ie.takeEssentia(coreAspect, 1, dir);
					
					if (amt != 0) {
						contained.add(coreAspect,amt);
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					}
				}
			}
		}
	}
	
	@Override
	public void WriteCustom(NBTTagCompound nbtc) {
		contained.writeToNBT(nbtc);
		nbtc.setInteger("ventTime", ventTime);
		nbtc.setString("coreAspect", coreAspect.getTag());
		nbtc.setInteger("maximum",maximum);
		nbtc.setInteger("Counter", counter);
	}

	@Override
	public void ReadCustom(NBTTagCompound nbtc) {
		contained.readFromNBT(nbtc);
		ventTime = nbtc.getInteger("ventTime");
		coreAspect = Aspect.getAspect(nbtc.getString("coreAspect"));
		maximum = nbtc.getInteger("maximum");
		counter = nbtc.getInteger("Counter");
	}
	
	/* 
	 * Sets the color of the glow effect - 0-1 per channel
	 */
	public void setChargedColor(float r, float g, float b) {
		red = r; green = g; blue = b;
	}
	private float red = 1, green = 1, blue = 1;
	
	@Override
	public void setChargedColor() {
		GL11.glColor4f(red, green, blue,(float) (contained.visSize() * (1f/maximum)));
	}

	// -- IEssentia Transport -- 
	
	@Override
	public boolean isConnectable(ForgeDirection face) {
		return face != ForgeDirection.UP;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		return false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		return coreAspect;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return contained.visSize() < maximum ? 128 : 0;
		//return 128;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		return null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		return 0;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}
	
	// -- IAspect Container -- 
	
	@Override
	public AspectList getAspects() {
		//AspectList al = new AspectList();
		//al.add(Aspect.ARMOR, ArmorHeld);
		//AspectList al = new AspectList();
		//al.add(coreAspect, contained.visSize());
		//al.add(Aspect.VOID, maximum);
		//return al;
		return contained;
	}

	@Override
	// Not actually sure what this method is used for?
	public void setAspects(AspectList aspects) {
		
	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {
		//if (tag.equals(Aspect.ARMOR)) return true;
		return false;
	}

	@Override
	public int addToContainer(Aspect tag, int amount) {
		//if (!tag.equals(Aspect.ARMOR)) return 0;
		return 0;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		if (contained.getAmount(tag) >= amount) {
			contained.reduce(tag, amount);
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	public boolean takeFromContainer(AspectList ot) {
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		if (tag.equals(coreAspect) && amount < contained.getAmount(coreAspect))
			return true;
		return false;
	}

	@Override
	@Deprecated
	public boolean doesContainerContain(AspectList ot) {
		return false;
	}

	@Override
	public int containerContains(Aspect tag) {
		return contained.getAmount(tag);
	}
}
