package cori.EssentialAlchemy.potions;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.client.ClientProxy;
import cori.EssentialAlchemy.tile.TilePotionModifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PotionModifier extends BlockContainer {

	public PotionModifier(Material m) {
		super(m);
		
		setBlockName("PotionModifier");
		setCreativeTab(EssentialAlchemy.thaumTab);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World w, int x,
			int y, int z, EntityPlayer player,
			int side, float dx, float dy,
			float dz) {
		
		TilePotionModifier tpm = (TilePotionModifier)w.getTileEntity(x, y, z);
		tpm.refreshMask();
		
		player.openGui(EssentialAlchemy.instance, 0, w, x, y, z);
		
		return true;
	}
	
	public static IIcon BlockBase, BlockSide, BlockTop;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		BlockBase = ir.registerIcon("essentialalchemy:PotionModifierBase");
		BlockSide = ir.registerIcon("essentialalchemy:PotionModifierSide");
		BlockTop  = ir.registerIcon("essentialalchemy:PotionModifierTop");
	}
	
	public static boolean sideSwitch = false;
	
	@Override
	public int getRenderType() {
		return ClientProxy.potionModifierRenderType;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		ForgeDirection fDir = ForgeDirection.getOrientation(side);
		
		switch(fDir) {
			case EAST:
			case NORTH:
			case SOUTH:
			case WEST:
				return BlockSide;
			
			case UP:
			case DOWN:
				return sideSwitch ? BlockBase : BlockTop;
				
			default:
				return BlockBase;
		}
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	
	@Override
	public boolean isSideSolid(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		switch (side) {
			case DOWN:
			case UP:
				return true;
			default:
				return false;
		}
	}


	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TilePotionModifier();
	}
}
