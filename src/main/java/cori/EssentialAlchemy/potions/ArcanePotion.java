package cori.EssentialAlchemy.potions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.EssentialResearchItem;
import cori.EssentialAlchemy.KeyLib;
import cori.EssentialAlchemy.Research;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.command.CommandGive;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ArcanePotion extends ItemPotion {
	//public Aspect Type;
	public static Map<Aspect,Potion> Assocations = new HashMap<Aspect, Potion>();
	public static Map<Integer,Aspect> ReverseAssocations = new HashMap<Integer, Aspect>(); // for getting color
	
	public ArcanePotion() {
		super();
		iconString="potion";
		hasSubtypes = false; // Don't list subtypes in tabs
		setCreativeTab(EssentialAlchemy.thaumTab);
	}
	
	public static void initAssociation() {
		Assocations.put(Aspect.HUNGER, Potion.hunger);
		Assocations.put(Aspect.DEATH, Potion.potionTypes[20]); // Wither
		Assocations.put(Aspect.WEAPON, Potion.harm);
		Assocations.put(Aspect.FLIGHT, Potion.jump);
		Assocations.put(Aspect.ARMOR, Potion.resistance);
		Assocations.put(Aspect.DARKNESS, Potion.blindness);
		//Assocations.put(Aspect.LIGHT, Potion.nightVision);
		Assocations.put(Aspect.ENERGY, Potion.damageBoost);
		Assocations.put(Aspect.UNDEAD, Potion.weakness);
		Assocations.put(Aspect.TRAP, Potion.moveSlowdown);
		Assocations.put(Aspect.POISON, Potion.poison);
		Assocations.put(Aspect.HEAL, Potion.regeneration);
		Assocations.put(Aspect.MAN, Potion.potionTypes[23]); // Saturation
		Assocations.put(Aspect.LIFE, Potion.heal);
		Assocations.put(Aspect.MOTION, Potion.moveSpeed);
		Assocations.put(Aspect.MINE, Potion.digSpeed);
		Assocations.put(Aspect.COLD, Potion.fireResistance);
		Assocations.put(Aspect.AURA, Potion.invisibility);
		Assocations.put(Aspect.VOID, Potion.waterBreathing);
		Assocations.put(Aspect.SENSES, Potion.nightVision);
		Assocations.put(Aspect.GREED, Potion.potionTypes[22]); // Absorption
		Assocations.put(Aspect.BEAST, Potion.potionTypes[21]); // Health Boost
		Assocations.put(Aspect.SOUL, Potion.digSlowdown);
		Assocations.put(Aspect.ELDRITCH, Potion.confusion);
		
		// Build the reverse list
		for(Aspect a : Assocations.keySet()) 
			ReverseAssocations.put(Assocations.get(a).getId(), a);
	}
	
	// Pretend not to have sub items
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_,
			List l) {
		return;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (pass != 0) return 0xFFFFFF; // Draw the bottle as white
		
		List Effects = getEffects(stack);
		if (e == null) return 0xFFFFFF;
		for (Object e : Effects) {
			PotionEffect pe = (PotionEffect)e;
				
			int id = pe.getPotionID();
			if (ReverseAssocations.containsKey(id))
				return ReverseAssocations.get(id).getColor();
		}
		
		return super.getColorFromItemStack(stack, pass);
	}
	
	public static String getKey(Aspect a) {
		return "ESBREW_"+a.getTag().toUpperCase();
	}
	public static Collection<String> getKeys() {
		ArrayList<String> ar = new ArrayList<String>();
		for(Aspect a : Assocations.keySet())
			ar.add(getKey(a));
		return ar;
	}
	
	// Automatically register a shitload of pages and recipes
	public static void register() {
		// Initialize Aspect <-> Effect Assocations
		initAssociation();
		// Register Arcane Potion
		GameRegistry.registerItem(EssentialAlchemy.ArcanePotion = new ArcanePotion(), "ESArcanePotion");
		
		Set<Aspect> keys = Assocations.keySet();
		
		int startCol = -3;
		int startRow = -4;
		int at = 0;
		
		ItemStack bottle = new ItemStack(Items.glass_bottle);
		
		for(Aspect a : keys) {
			// Get an aspect list of eight of these
			AspectList al = new AspectList(); al.add(a, 8);
			
			
			int row = startRow + at/4;
			int col = startCol - at%4;
			// Avoid drawing over the master icon
			if (row == -1 && col == -3) {
				++at;
				row = startRow + at/4;
				col = startCol - at%4;
			}
			// Increment draw slot
			++at;
			
			ItemStack stack = getStackFor(a);
			// Register the recipe
			CrucibleRecipe recipe = ThaumcraftApi.addCrucibleRecipe(
					getKey(a), 
					stack, 
					bottle, 
					al);
			
			ResearchItem r = 
				new EssentialResearchItem(
					getKey(a), 
					Research.CATEGORY,
					al,
					row,
					col,
					1,
					stack)
				//.setParentsHidden(KeyLib.ESS_BREWING)
				.setHidden()
				.setAspectTriggers(a)
				.setSecondary()
				.setPages(new ResearchPage(recipe))
				.registerResearchItem();
		}
	}
	
	public static boolean useVanillaWhenPossible = false;
	
	private static ItemStack getStackFor(Aspect a) {
		int effectID = Assocations.get(a).id;
		// Handle any vanilla potions first
		
		if (useVanillaWhenPossible)
		switch (effectID) {
			case 10: // Regen
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8193);
			case 1: // Swiftness
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8194);
			case 12: // Fire-R
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8195);
			case 19: // Poison
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8196);
			case 6: // I-Health
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8197);
			case 16: // Night-Vision
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8198);
			case 18: // Weakness
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8200);
			case 5: // Strength
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8201);
			case 2: // Slowness
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8202);
			case 7: // Harming
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8204);
			case 13: // Water Breathing
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8205);
			case 14: // Invisibility
				return new ItemStack(EssentialAlchemy.ArcanePotion,1,8206);
		}
		
		ItemStack is = new ItemStack(EssentialAlchemy.ArcanePotion, 1, 16);
		NBTTagCompound tag = null;
		int amp = 0;
		int dur = 1200;
		
		// Handle cases where the default duration should be longer
		switch (effectID) {
			case 1: // Speed
			case 2: // Slowness
			case 4: // Fatigue
			case 8: // Jump
			case 9: // Nausea
			case 11: // Resistance
			case 12: // Fire Resist
			case 15: // Blindness
			case 16: // Night Vision
			case 17: // Hunger
			case 21: // Health Boost
				dur *= 2; // 2m
		}
		
		String json = "{CustomPotionEffects:[";
		//json += "{Id:"+effectID+",Amplifier:"+amp+",Duration:"+dur+"}";
		json += effectToJSON(new PotionEffect(effectID, dur, amp));
		
		json += "]}";
		
		try {
			tag = (NBTTagCompound) JsonToNBT.func_150315_a(json);
		} catch (NBTException e) {
			EssentialAlchemy.lg.warn("Encountered Error generating potion ID " + effectID);
			e.printStackTrace();
		}
		
		//if (tag != null)
		is.setTagCompound(tag);
		
		return is;
	}
	
	private static String effectToJSON(PotionEffect pe) {
		return "{Id:"+pe.getPotionID()+",Amplifier:"+pe.getAmplifier()+",Duration:"+pe.getDuration()+"}";
	}
	
	// Denote that this is was an Essential Alchemy potion
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List extraLines, boolean par4) {
		extraLines.add(StatCollector.translateToLocal("ES.mouseoverArcanePotion"));
		super.addInformation(par1ItemStack, par2EntityPlayer, extraLines, par4);
	}
}
