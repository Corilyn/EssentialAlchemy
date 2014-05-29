package cori.EssentialAlchemy.potions;

import thaumcraft.common.container.SlotGhost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PotionSlot extends Slot {
	
	public PotionSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
	boolean isGhost = false;
	
	public Slot asGhost() {
		isGhost = true;
		return this;
	}
	
	@Override
	public boolean isItemValid(ItemStack item) {
		return (item.getItem() instanceof ArcanePotion);
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !isGhost;
	}
}
