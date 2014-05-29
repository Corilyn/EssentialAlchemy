package cori.EssentialAlchemy.potions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
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
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

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
	
	boolean shouldConsume(EntityPlayer p) {
		return true;
	}
	
	ItemStack modifyPreConsume(EntityPlayer p, ItemStack stack) {
		return stack;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World w,
			EntityPlayer player) {
		if (!player.capabilities.isCreativeMode && shouldConsume(player))
			--stack.stackSize;

		if (!w.isRemote)
		{
			List list = this.getEffects(stack);

			if (list != null)
			{
				Iterator iterator = list.iterator();

				while (iterator.hasNext())
				{
					PotionEffect potioneffect = (PotionEffect)iterator.next();
					player.addPotionEffect(new PotionEffect(potioneffect));
				}
			}
		}

		if (!player.capabilities.isCreativeMode)
		{
			if (stack.stackSize <= 0)
				return new ItemStack(Items.glass_bottle);

			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}

		return stack;
	}
	
	// 0 - Amplify (0x1)
	// 1 - Corrupt (0x2)
	// 2 - Extend  (0x4)
	// 3 - Mix (?) (0x8)
	// 4 - Splash  (0x10)
	public static int getModifierMask(ItemStack stack) {
		if (stack == null) return 0;
		Item item = stack.getItem();
		
		int mask = 0;
		
		if (item instanceof ArcanePotion) {
			ItemPotion ip = ((ItemPotion)item);
			List<PotionEffect> Effects = ip.getEffects(stack);
			if (Effects.size() > 1) return 0; // Mixed Potions can't be remixed
			
			PotionEffect effect = Effects.get(0);
			
			int id = effect.getPotionID();
			int amp = effect.getAmplifier();
			int dur = effect.getDuration();
			
			int ampBase = getAmplifier(id);
			int durBase = getDuration(id);
			
			// Only allow amping of amplifiable potions
			switch (id) {
				case 9: // Nasuea
				case 12: // Fire Resist
				case 13: // Water Breathing
				case 14: // Invis
				case 15: // Blindness
				case 16: // Night Vision
					break;
				default:
					if (amp < ((ampBase+1)*3)) mask |= 0x1;
			}
			
			mask |= 0x2; // Everything is corruptable
			
			// Don't allow extending Instant potions
			switch(id) {
				case 6:
				case 7:
					break;
				default:
					if (dur < (durBase * 3)) mask |= 0x4;
			}
			
			
			if (!ItemPotion.isSplash(stack.getItemDamage())) mask |= 0x10;
		}
		
		return mask;
	}
	
	public static AspectList toModify(int modifier, ItemStack stack) {
		AspectList al = new AspectList();
		
		if (modifier == 4) { // splashify
			al.add(Aspect.ENTROPY, 4);
			return al;
		}
		
		if (modifier == 1) { // Corrupt
			al.add(Aspect.TAINT,2);
			return al;
		}
		
		ItemPotion potion = (ItemPotion) stack.getItem();
		List<PotionEffect> effects = potion.getEffects(stack);
		PotionEffect effect = effects.get(0);
		
		int id = effect.getPotionID();
		
		// Amplify / Extend
		if (modifier == 0 || modifier == 2) {
			// figure out the number of times this potion has been enhanced
			int currentLevel = 0;
			
			// times amplified
			int ampLevel = effect.getAmplifier();
			
			if (ampLevel > getAmplifier(id)) ++currentLevel;
			if (ampLevel > (getAmplifier(id))+2) ++currentLevel;
			
			int durLevel = effect.getDuration();
			int baseDir = getDuration(id);
			if (durLevel > baseDir) ++currentLevel;
			if (durLevel > baseDir * 3) ++currentLevel;
			
			int[] costs = new int[] { 8, 16, 24, 32, 48, 64 };
			al.add(ReverseAssocations.get(id), costs[currentLevel]);
		}
		
		return al;
	}
	
	public static ItemStack applyModifer(int modifier, ItemStack stack) {
		ItemPotion potion = (ItemPotion) stack.getItem();
		List<PotionEffect> effects = potion.getEffects(stack);
		PotionEffect effect = effects.get(0);
		
		NBTTagCompound comp = new NBTTagCompound();
		NBTTagList customEffects = new NBTTagList();
		NBTTagCompound efTag = new NBTTagCompound();
		
		int id = effect.getPotionID();
		int effAmp = effect.getAmplifier();
		int effDur = effect.getDuration();
		boolean ambient = effect.getIsAmbient();
		
		// Amplify
		if (modifier == 0)
			effAmp += getAmplifier(id) + 1;
		
		if (modifier == 1) // Corrupt
			switch(id) { // Corruption table
				case 1: id = 2; break; // Swap Speed/Slow
				case 2: id = 1; break; 
				case 3: id = 4; break; // Swap Haste/Fatigue
				case 4: id = 3; break;
				case 5: id = 18; break; // Swap Strength/Weakness
				case 18: id = 5; break;
				case 6: id = 7; break; // heal to harm
				case 7: id = 6; break; // harm to heal
				case 8: effAmp = -effAmp; break; // Jump Boost becomes Jump Penalty
				case 9: id = 21; break; // Nasuea to health boost
				case 21: id = 9; break;
				case 12: id = 13; break; // Swap Fire Resist and Waterbreathing
				case 13: id = 12; break;
				case 19: id = 22; break; // Swap Poison / Absorb
				case 22: id = 19; break;
				case 16: id = 15; break; // Swap Blindness / Nightvision
				case 15: id = 19; break;
				case 11: effAmp = -effAmp-1; break; // Resist becomes negative resistance
				case 14: id = 20; break; // Swap Wither/Invis
				case 20: id = 14; break;
				case 17: id = 23; break; // Swap Hunger/Saturation
				case 23: id = 17; break; 
			}
		
		if (modifier == 2) // Extend
			effDur *= 2;
		
		PotionEffect modEffect = new PotionEffect(id, effDur, effAmp, ambient);
		modEffect.writeCustomPotionEffectToNBT(efTag);
		customEffects.appendTag(efTag);
		comp.setTag("CustomPotionEffects", customEffects);
		
		stack.setTagCompound(comp);
		
		if (modifier == 4) // Splash
			stack.setItemDamage(16400);
		
		return stack;
	}
	
	public static int getAmplifier(int id) {
		switch (id) {
			case 2: // Slow
			case 4: // Fatigue
			case 8: // Jump
			case 17: // Hunger
			case 18: // Weakness
			case 21: // Health boost
				return 1;
		}
		return 0;
	}
	
	public static int getDuration(int id) {
		// Tiers: 45s, 1.5m, 3m
		switch(id) {
			// Instant Health / Damage
			case 6:
			case 7:
				return 1;
			case 1: // Speed
			case 2: // Slow
			case 4: // Fatigue
			case 8: // Jump 
			case 9: // Nausea
			case 11: // Regen
			case 12: // Resistance
			case 13: // Fire Resist
			case 14: // Water breathing
			case 15: // Blindness
			case 16: // Night Vision 
			case 17: // Hunger 
			case 18: // Weakness
				return 3600; // 3m
			case 3: // Haste
			case 5: // Strength
			case 21: // Health Boost
				return 1800; // 1.5m
			case 10: // Regen
			case 19: // Poison
			case 20: // Wither
			case 22: // Absorb
				return 900;
			case 23: // Saturation
				return 10; // 5-Shanks
		}
		
		return 1800;
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
		Assocations.put(Aspect.FLESH, Potion.potionTypes[23]); // Saturation
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
		if (Effects == null) return 0xFFFFFF;
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
		
		String json = "{CustomPotionEffects:[";
		//json += "{Id:"+effectID+",Amplifier:"+amp+",Duration:"+dur+"}";
		json += effectToJSON(new PotionEffect(effectID, getDuration(effectID), getAmplifier(effectID)));
		
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
