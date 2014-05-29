package cori.EssentialAlchemy;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.Thaumcraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import cori.EssentialAlchemy.block.*;
import cori.EssentialAlchemy.block.paving.*;

import cori.EssentialAlchemy.potions.PotionModifier;
import cori.EssentialAlchemy.tile.TileCrystalFocus;
import cori.EssentialAlchemy.tile.TilePotionModifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public static Block RainbowDummy;
	
	public void mainProcessing() {
		
	}
	
	public void RegisterBlocks() {
		GameRegistry.registerTileEntity(TileEffectStone.class, "TileStoneBuff");
		GameRegistry.registerTileEntity(TileCrystalFocus.class,"TileCrystalFocus");
		GameRegistry.registerTileEntity(TilePotionModifier.class, "TilePotionModifier");
		
		// Crystal Focus
		Block crystalFocus = new CrystalFocus();
		GameRegistry.registerBlock(crystalFocus, "CrystalFocus");
		
		// Potion Modifier
		EssentialAlchemy.PotionModifier = new PotionModifier(Material.rock);
		GameRegistry.registerBlock(EssentialAlchemy.PotionModifier, "PotionModifier");
		
		// Paving stones
		
		EssentialAlchemy.ProtectStone = new ResistStone();
		GameRegistry.registerBlock(EssentialAlchemy.ProtectStone,"ResistStone");
		
		EssentialAlchemy.RegenStone = new RegenStone();
		GameRegistry.registerBlock(EssentialAlchemy.RegenStone,"RegenStone");
		
		EssentialAlchemy.TransitStone = new TransitStone();
		GameRegistry.registerBlock(EssentialAlchemy.TransitStone, "TransitStone");
		
		EssentialAlchemy.ColdStone = new ColdStone();
		GameRegistry.registerBlock(EssentialAlchemy.ColdStone, "ColdStone");
		
		EssentialAlchemy.SuckStone = new SuctionStone();
		GameRegistry.registerBlock(EssentialAlchemy.SuckStone, "SuckStone");
		
		EssentialAlchemy.VenomStone = new VenomStone();
		GameRegistry.registerBlock(EssentialAlchemy.VenomStone, "VenomStone");
		
		EssentialAlchemy.BindStone = new BindStone();
		GameRegistry.registerBlock(EssentialAlchemy.BindStone, "BindStone");
		
		EssentialAlchemy.BiteStone = new BiteStone();
		GameRegistry.registerBlock(EssentialAlchemy.BiteStone, "BiteStone");
		
		EssentialAlchemy.HealthStone = new HealthStone();
		GameRegistry.registerBlock(EssentialAlchemy.HealthStone, "HealthStone");
		
		RainbowDummy = new RainbowDummy();
		GameRegistry.registerBlock(RainbowDummy, "RainbowDummy"); // Do I actually need to register this? Can't hurt.

		
		// Register Aspects
		registerAspects();
	}
	
	// ... So far everything is autoregistered by the recipes
	private void registerAspects() {
		//ThaumcraftApi.register
	}

	public void BindFX(World w, float x, float y, float z) {
		
	}
}
