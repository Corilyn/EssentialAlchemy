package cori.EssentialAlchemy;

import java.util.Arrays;
import java.util.HashMap;

import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

// Used Vazkii's TT as an example

public class Research {
	public static final String CATEGORY = "ESSALC";
	
	public static HashMap recipes = new HashMap();
	
	static AspectList ofPrimals(int air, int earth, int fire, int water, int order, int entropy) {
		AspectList al = new AspectList();
		if (air > 0) 	al.add(Aspect.AIR, air);
		if (earth > 0) 	al.add(Aspect.EARTH, earth);
		if (fire > 0) 	al.add(Aspect.FIRE, fire);
		if (water > 0) 	al.add(Aspect.WATER, water);
		if (order > 0) 	al.add(Aspect.ORDER, order);
		if (entropy > 0)al.add(Aspect.ENTROPY, entropy);
		return al;
	}
	
	public static void doSetup() {
		registerRecipes();
		registerPages();
	}
	
	public static void registerRecipes() {
		ItemStack ArcaneBrick = ItemApi.getBlock("blockCosmeticSolid", 7);
		ItemStack SalisMundis = ItemApi.getItem("itemResource", 14);
		
		recipes.put(KeyLib.PAVING_RESIST,
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_RESIST, 
						new ItemStack(EssentialAlchemy.ProtectStone,4), 
						ofPrimals(0, 10, 0, 0, 10, 0), 
						"aba", "aca", 'a',ArcaneBrick,'b', SalisMundis, 'c', new ItemStack(Items.golden_chestplate)));
		
		recipes.put(KeyLib.PAVING_REGEN, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_REGEN, 
						new ItemStack(EssentialAlchemy.RegenStone,4), 
						ofPrimals(0, 10, 0, 10, 0, 0),
						"aba", "aca", 'a',ArcaneBrick,'b', SalisMundis, 'c', new ItemStack(Items.golden_apple)));
		
		recipes.put(KeyLib.PAVING_TRANSIT, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_TRANSIT, 
						new ItemStack(EssentialAlchemy.TransitStone,4), 
						ofPrimals(10, 0, 0, 0, 0, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Items.ender_pearl)));
		
		recipes.put(KeyLib.PAVING_COLD, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_COLD, 
						new ItemStack(EssentialAlchemy.ColdStone,4), 
						ofPrimals(0, 0, 0, 10, 0, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Blocks.ice)));
		
		recipes.put(KeyLib.PAVING_SUCK, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_SUCK, 
						new ItemStack(EssentialAlchemy.SuckStone,4), 
						ofPrimals(10, 0, 0, 0, 0, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Blocks.chest)));
		
		recipes.put(KeyLib.PAVING_VENOM, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_VENOM, 
						new ItemStack(EssentialAlchemy.VenomStone,4), 
						ofPrimals(0, 0, 0, 10, 0, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Items.spider_eye)));
		
		recipes.put(KeyLib.PAVING_BIND, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_BIND, 
						new ItemStack(EssentialAlchemy.BindStone,4), 
						ofPrimals(0, 0, 0, 0, 10, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Blocks.web)));
		
		recipes.put(KeyLib.PAVING_BITE, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_BITE, 
						new ItemStack(EssentialAlchemy.BiteStone,4), 
						ofPrimals(0, 10, 0, 0, 0, 10), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', ItemApi.getItem("itemSwordThaumium", 0)));
		
		recipes.put(KeyLib.PAVING_LIFE, 
				ThaumcraftApi.addArcaneCraftingRecipe(
						KeyLib.PAVING_LIFE, 
						new ItemStack(EssentialAlchemy.HealthStone,4), 
						ofPrimals(0, 10, 0, 10, 0, 0), 
						"aba", "aca", 'a',ArcaneBrick, 'b',SalisMundis, 'c', new ItemStack(Items.egg)));
	}
	
	public static void registerPages() {
		ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
		ResourceLocation icon = new ResourceLocation("essentialalchemy","textures/gui/tabIcon.png");
		
		ResearchCategories.registerCategory(CATEGORY, icon, background);
		
		ResearchItem r;
		
		// Advanced Paving
		r = new EssentialResearchItem(
				KeyLib.ADVANCED_PAVING, 
				CATEGORY, 
				new AspectList().add(Aspect.MAGIC, 2).add(Aspect.ORDER,4),
				-1, // Col
				0, // Row
				1, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.proxy.RainbowDummy))
			.setParents("DISTILESSENTIA","ARCANESTONE")
			//.setRound()
			.setSecondary().setPages( // Show the advanced paving, and all recipes thus far unlocked
					new ResearchPage("ES.page.ADVPAVING.1")/*,
					new ResearchPage(
						new IArcaneRecipe[] {
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_BIND),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_BITE),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_COLD),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_REGEN),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_RESIST),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_SUCK),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_TRANSIT),
							(IArcaneRecipe) recipes.get(KeyLib.PAVING_VENOM)
						})*/)
			.registerResearchItem();
		
		// Augmented Paving
		r = new EssentialResearchItem(
				KeyLib.AUGMENTED_PAVING, 
				CATEGORY, 
				new AspectList().add(Aspect.GREED, 4).add(Aspect.TRAVEL, 4).add(Aspect.VOID, 6), 
				-1, 
				-2, 
				2, 
				new ItemStack(Items.gold_ingot))
			.setParents(KeyLib.ADVANCED_PAVING, "JARVOID")
			.setRound()
			.setPages(
					new ResearchPage("ES.page.AUGPAVING.1"),
					new ResearchPage(Arrays.asList(new Object[] {
							new AspectList(),
							1, 2, 1,
							Arrays.asList (new ItemStack[] {
								new ItemStack(Items.gold_ingot),
								new ItemStack(EssentialAlchemy.proxy.RainbowDummy)})
						}))
					)
			.registerResearchItem(); 
		
		// Protection Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_RESIST, 
				CATEGORY, 
				new AspectList().add(Aspect.ARMOR, 8).add(Aspect.ORDER,2),
				1, // Col
				0, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.ProtectStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.RESISTSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_RESIST)))
			.registerResearchItem();
		
		// Regen Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_REGEN, 
				CATEGORY, 
				new AspectList().add(Aspect.HEAL, 8).add(Aspect.ORDER,2),
				3, // Col
				3, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.RegenStone))
			.setParents(KeyLib.PAVING_LIFE)
			.setConcealed()
			/*.setSecondary()*/.setPages(
					new ResearchPage("ES.page.REGENSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_REGEN)))
			.registerResearchItem();
		
		// Transit Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_TRANSIT, 
				CATEGORY, 
				new AspectList().add(Aspect.ELDRITCH, 8).add(Aspect.ENTROPY,2),
				1, // Col
				4, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.TransitStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.TRANSITSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_TRANSIT)))
			.registerResearchItem();
		
		// Cold Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_COLD, 
				CATEGORY, 
				new AspectList().add(Aspect.COLD,8).add(Aspect.ORDER, 2),
				1, // Col
				6, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.ColdStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.COLDSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_COLD)))
			.registerResearchItem();
		
		// Suck Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_SUCK, 
				CATEGORY, 
				new AspectList().add(Aspect.VOID,8).add(Aspect.AIR, 2),
				-3, // Col
				0, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.SuckStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.SUCKSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_SUCK)))
			.registerResearchItem();
		
		// Venom Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_VENOM, 
				CATEGORY, 
				new AspectList().add(Aspect.POISON, 8).add(Aspect.TRAP, 2),
				-3, // Col
				2, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.VenomStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.VENOMSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_VENOM)))
			.registerResearchItem();
		
		// Bind Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_BIND, 
				CATEGORY, 
				new AspectList().add(Aspect.TRAP, 8).add(Aspect.MOTION, 2),
				-5, // Col
				0, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.BindStone))
			.setParents(KeyLib.PAVING_SUCK)
			.setConcealed()
			/*.setSecondary()*/.setPages(
					new ResearchPage("ES.page.BINDSTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_BIND)))
			.registerResearchItem();
		
		// Bite Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_BITE, 
				CATEGORY, 
				new AspectList().add(Aspect.WEAPON, 8).add(Aspect.TRAP, 2),
				-5, // Col
				3, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.BiteStone))
			.setParents(KeyLib.PAVING_VENOM)
			.setConcealed()
			/*.setSecondary()*/.setPages(
					new ResearchPage("ES.page.BITESTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_BITE)))
			.registerResearchItem();
		
		// Health Stone
		r = new EssentialResearchItem(
				KeyLib.PAVING_LIFE, 
				CATEGORY, 
				new AspectList().add(Aspect.LIFE, 8).add(Aspect.HEAL, 2),
				1, // Col
				2, // Row
				2, // Complexity (1-3)
				new ItemStack(EssentialAlchemy.HealthStone))
			.setParents(KeyLib.ADVANCED_PAVING)
			.setConcealed()
			.setSecondary().setPages(
					new ResearchPage("ES.page.LIFESTONE.1"),
					new ResearchPage((IArcaneRecipe)recipes.get(KeyLib.PAVING_LIFE)))
			.registerResearchItem();
	}
}
