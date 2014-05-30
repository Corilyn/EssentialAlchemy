package cori.EssentialAlchemy.baubles;

import java.text.DecimalFormat;
import java.util.List;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectSourceHelper;
import thaumcraft.common.Thaumcraft;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.Research;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class AlchemyAmulet extends Item implements IBauble {

	public AlchemyAmulet() {
		setMaxStackSize(1);
		setCreativeTab(EssentialAlchemy.thaumTab);
		setUnlocalizedName("AlchemyAmulet");
		canRepair = false;
		
		setTextureName("essentialalchemy:AlchemistAmulet");
	}
	
	@Override
	public boolean getShareTag() {
		return true;
	}
	
	public IIcon icon;
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		icon = ir.registerIcon("essentialalchemy:AlchemistAmulet");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return icon;
	}
	
	
	
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.rare;
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.AMULET;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack,
			EntityPlayer player, List lines, boolean dunno) {
		AspectList currentHeld;
		if ((currentHeld = getAspectList(stack)) == null) {
			setAspectList(stack, Research.ofPrimals(100, 100, 100, 100, 100, 100));
			currentHeld = getAspectList(stack);
		}
		
		DecimalFormat format = new DecimalFormat("#####.##");
		
		lines.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.capacity.text") + " 100");
		for(Aspect a : Aspect.getPrimalAspects()) {
			String ln = format.format(currentHeld.getAmount(a)/100F); 
			lines.add(" §" + a.getChatcolor() + a.getName() + "§r x " + ln);
		}
	}
	
	// Notice -> Recycled across alchemy amulets
	static AspectList al = new AspectList();
	public static AspectList getAspectList(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			al.readFromNBT(tag);
			return al;//.readFromNBT(tag);
		}
		return null;
	}
	
	public static void setAspectList(ItemStack stack, AspectList toSet) {
		NBTTagCompound tag = new NBTTagCompound();
		toSet.writeToNBT(tag);
		stack.setTagCompound(tag);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		if ((al = getAspectList(stack)) == null) return 0;
		int min = 10000;
		for (Aspect a : Aspect.getPrimalAspects()) {
			min=Math.min(al.getAmount(a),min);
		}
		
		/*for(Aspect a : al.getAspects()) {
			min = Math.min(min,al.getAmount(a));
		}*/
		
		return 1-(min/10000D);
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player instanceof EntityPlayer) {
		
			boolean doSet = false;
			AspectList currentHeld;
			if ((currentHeld = getAspectList(stack)) == null) {
				setAspectList(stack, Research.ofPrimals(100, 100, 100, 100, 100, 100));
				currentHeld = getAspectList(stack);
			}
			
			for (Aspect a : Aspect.getPrimalAspects()) {
				if (currentHeld.getAmount(a) < 10000)
					if (Thaumcraft.proxy.wandManager.consumeVisFromInventory((EntityPlayer) player, new AspectList().add(a, 100))) {
						currentHeld.add(a, 100);
						doSet = true;
					}
			}
			
			if (doSet)
				setAspectList(stack, currentHeld);
		}
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}
	
}
