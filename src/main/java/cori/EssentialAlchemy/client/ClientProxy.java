package cori.EssentialAlchemy.client;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.world.World;
import cori.EssentialAlchemy.CommonProxy;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.render.CrystalFocusBlockRender;
import cori.EssentialAlchemy.render.PavingGlow;
import cori.EssentialAlchemy.render.PavingStoneRender;
import cori.EssentialAlchemy.tile.TileEffectStone;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void mainProcessing() { // I don't think I'll actually need this sucker after all?
		super.mainProcessing();
		
	}
	
	@Override
	public void BindFX(World w, float x, float y, float z) {
		EntityFX fx = new BindFX(w, x, y, z);
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
	}
	
	public static int crystalFocusRenderType;
	public static int pavingStoneRenderType;
	
	@Override
	public void RegisterBlocks() {
		super.RegisterBlocks();
		
		crystalFocusRenderType = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(crystalFocusRenderType, new CrystalFocusBlockRender());
		
		pavingStoneRenderType = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(pavingStoneRenderType, new PavingStoneRender());
		
		TileEntitySpecialRenderer glowInstance = new PavingGlow();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEffectStone.class, glowInstance);
		
		
		EssentialAlchemy.lg.warn("Bound special renderer");
	}
}
