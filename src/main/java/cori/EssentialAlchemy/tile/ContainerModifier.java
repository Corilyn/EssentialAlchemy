package cori.EssentialAlchemy.tile;

import cori.EssentialAlchemy.potions.PotionSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerModifier extends Container {
	TilePotionModifier wrappedModifier;
	
	//EnitityPlayer 
	
	public ContainerModifier(InventoryPlayer ip, TilePotionModifier tpm) {
		wrappedModifier = tpm;
		
		for(int slot : wrappedModifier.slotsTop) {
			addSlotToContainer(new PotionSlot(tpm, slot, 37, 17));
			//addSlotToContainer(new Slot(tpm,slot,37,17));
		}
		
		for(int slot : wrappedModifier.slotsBottom) {
			addSlotToContainer(new PotionSlot(tpm, slot, 122, 17));
			//addSlotToContainer(new Slot(tpm, slot, 122, 17));
		}
		
		//  player inventory
		for (int i = 0; i < 3; ++i)
			for (int k = 0; k < 9; ++k)
				addSlotToContainer(new Slot(
						ip,
						k + i * 9 + 9,
						8 + k * 18,
						84 + i * 18));
		
		//  player Hotbar
		for (int i = 0; i < 9; ++i) 
			addSlotToContainer(new Slot(ip,i,8 + i * 18, 142));
		
		
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		
		Slot slotObject = (Slot)inventorySlots.get(slot);
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack inSlot = slotObject.getStack();
			stack = inSlot.copy();
			
			// If the slot is 
			if (slot < wrappedModifier.getSizeInventory()) {
				if (!mergeItemStack(inSlot, 2, 38, true)) {
					return null;
				}
			} else if (!mergeItemStack(inSlot, 0, 1, false)) {
				return null;
			}
			
			if (inSlot.stackSize == 0) 
				slotObject.putStack(null);
			else
				slotObject.onSlotChanged();
			
			
			if (inSlot.stackSize == stack.stackSize) {
				return null;
			}
			
			slotObject.onPickupFromSlot(player,inSlot);
		}
		return stack;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return wrappedModifier.isUseableByPlayer(player);
	}

}
