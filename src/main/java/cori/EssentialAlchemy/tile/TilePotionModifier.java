package cori.EssentialAlchemy.tile;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectSourceHelper;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.Thaumcraft;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.potions.ArcanePotion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePotionModifier extends BaseTile implements ISidedInventory, IAspectContainer {
	static final int[] slotsTop = new int[] { 0 };
	static final int[] slotsSide = new int[] { 0 };
	static final int[] slotsBottom = new int[] { 1 };
	
	ItemStack[] contents = new ItemStack[2];
	AspectList aspects = new AspectList();
	
	public boolean isProcessing() {
		if (contents[1] != null) return false; // If something is in our slot
		//refreshMask();
		
		return enabled;//worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	
	public boolean enabled = false;
	public int mode = 0; 
	// Modes: 
	// 0 - Amplify
	// 1 - Corrupt
	// 2 - Extend
	// 3 - Mix (?)
	// 4 - Splash
	
	public int modeMask = 0;
	public void refreshMask() {
		ItemStack stack = getStackInSlot(0);
		if (stack == null) modeMask = 0;
		
		modeMask = ArcanePotion.getModifierMask(stack);
	}
	
	int drawTime = 0;
	
	@Override
	public void updateEntity() {
		if (isProcessing()) {
			boolean dirty = false;
			refreshMask();
			if (!isModValid()) return;
			
			AspectList req = getRequirements();
			if (req == null) return;
			
			Aspect sought = req.getAspects()[0];
			// Remove the irrelevant aspects we have gathered while active
			for(Aspect a : aspects.getAspects())
				if (a != sought) {
					dirty = true;
					aspects.remove(a);
				}
			
			Color asp = new Color(sought.getColor());
			float rByte = 1f/255f;
			float r = asp.getRed() * rByte, b = asp.getBlue() * rByte, g = asp.getGreen() * rByte;
			
			if (drawTime==8)
				Thaumcraft.proxy.blockRunes(getWorldObj(), xCoord, yCoord + 0.5D, zCoord, r, g, b, 40, 0.01f);
			
			// draw required essences
			if (++drawTime > 10) { // Draw @2essence/second
				drawTime = 0; 
				if (AspectSourceHelper.drainEssentia(this, sought, ForgeDirection.UP, 12)) {
					dirty = true;
					aspects.add(sought,1);
				}
				
				if (getWorldObj().rand.nextInt(4) == 0)
					worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "thaumcraft:creak", 1f, 1.2f);
			}
			
			if (drawTime==5) // Just a slight delay so it doesn't finish off immediately
			if (aspects.getAmount(sought) >= req.getAmount(sought)) {
				if (getWorldObj().isRemote) return; // Don't move potions clientside, that jitters
				
				// Modify the potion
				contents[1] = ArcanePotion.applyModifer(mode, contents[0].copy());
				contents[0] = null; // Remove
				dirty = true;
				aspects = new AspectList();
				
				worldObj.playSoundEffect(xCoord + 0.5D,yCoord + 0.5D, zCoord + 0.5D, "thaumcraft:bubble", 0.5f, 0.8f);
				
				Thaumcraft.proxy.splooshFX(worldObj, xCoord, yCoord, zCoord);
			}
			
			if (dirty)
				getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
			
		} else {
			if (aspects.visSize() > 0) {
				// About every three seconds, bleed off our excess essentia
				if (getWorldObj().rand.nextInt(60) == 0) {
					aspects.remove(aspects.getAspects()[0], 1);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	public boolean isModValid() {
		return ((1 << mode) & modeMask) != 0;
	}
	public AspectList getRequirements() {
		if (contents[0] == null) return null;
		if (contents[0].getItem() instanceof ArcanePotion)
			return ArcanePotion.toModify(mode, contents[0]);
		
		return null;
	}
	
	public void handleButton(int id) {
		
		if (id == 1) {
			//EssentialAlchemy.lg.warn("Cycled Mode");
			mode += 1;
			if (mode > 4)
				mode = 0;
			
			// No mixing for now
			if (mode == 3)
				++mode;
			
			enabled = false;
			
			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
		} else { // enabled button was hit
			//EssentialAlchemy.lg.warn("Button: " + id + " pressed");
			enabled = !enabled;
			getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public void WriteCustom(NBTTagCompound nbtc) {
		
		NBTTagList items = new NBTTagList();
		for (int i=0; i<contents.length; ++i) {
			if (contents[i] != null) {
				NBTTagCompound cmp = new NBTTagCompound();
				cmp.setByte("Slot", (byte)i);
				contents[i].writeToNBT(cmp);
				items.appendTag(cmp);
			}
		}
		nbtc.setTag("Items", items);
		nbtc.setInteger("mode", mode);
		nbtc.setBoolean("enabled", enabled);
		
		aspects.writeToNBT(nbtc);
		
	}
	@Override
	public void ReadCustom(NBTTagCompound nbtc) {
		NBTTagList items = nbtc.getTagList("Items", 10);
		contents = new ItemStack[getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound cmp = items.getCompoundTagAt(i);
			int slot = cmp.getByte("Slot");
			contents[slot] = ItemStack.loadItemStackFromNBT(cmp);
		}
		mode = nbtc.getInteger("mode");
		enabled = nbtc.getBoolean("enabled");
		
		aspects.readFromNBT(nbtc);
	}
	
	@Override
	public int getSizeInventory() {
		return contents.length;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return contents[slot];
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0)
					setInventorySlotContents(slot, null);
			}
		}
		
		return stack;
	}
	
	/*@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack inSlot = contents[slot];
		if (inSlot == null) return null;
		
		ItemStack fresh = inSlot.copy();
		
		inSlot = null;
		return fresh;
	}*/
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (contents[slot] != null) {
			ItemStack stack = contents[slot];
			contents[slot] = null;
			return stack;
		} else return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		contents[slot] = stack;
	}
	@Override
	public String getInventoryName() {
		return "ES.container.modifier";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	// Same distance as furnace usability
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return 
			getWorldObj().getTileEntity(xCoord, yCoord, zCoord) != this ? false : 
				player.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64.0D;
	}
	@Override
	public void openInventory() {}
	
	@Override
	public void closeInventory() {}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		// If the slot is the top slot
		if (slot == 0) 
			if (stack.getItem() instanceof ArcanePotion)
				return true;
		
		return false;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == 1) return slotsTop;
		if (side == 0) return slotsBottom;
		return slotsSide;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return isItemValidForSlot(slot, item);
	}
	
	// Only allow withdraw from bottom slot
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		for(int valid : slotsBottom)
			if (slot == valid)
				return true;
		return false;
	}
	
	// Aspects Container
	@Override
	public AspectList getAspects() {
		return aspects;
	}
	
	@Override
	public void setAspects(AspectList al) {
		aspects = al;
	}
	
	@Override
	public boolean doesContainerAccept(Aspect tag) {
		return false;
	}
	
	@Override
	public int addToContainer(Aspect tag, int amount) {
		int freeSpace = 16 - aspects.getAmount(tag);
		
		if (freeSpace > amount) {
			aspects.add(tag, amount);
			return 0;
		} else {
			int willInsert = Math.min(freeSpace, amount);
			aspects.add(tag, willInsert);
			return amount - willInsert;
		}
	}
	
	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		return false;
	}
	
	@Override
	@Deprecated
	public boolean takeFromContainer(AspectList ot) {
		return false;
	}
	
	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		return aspects.getAmount(tag) >= amount;
	}
	
	@Override
	@Deprecated
	public boolean doesContainerContain(AspectList ot) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int containerContains(Aspect tag) {
		return aspects.getAmount(tag);
	}
}
