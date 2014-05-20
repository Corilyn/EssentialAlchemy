package cori.EssentialAlchemy.block;

import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.client.ClientProxy;
import cori.EssentialAlchemy.tile.TileCrystalFocus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CrystalFocus extends BlockContainer {
	// Facing by metadata
	
	public CrystalFocus() {
		super(Material.iron);
		
		setCreativeTab(EssentialAlchemy.thaumTab);
		setBlockTextureName("thaumcraft:metalbase");
	}
	
	
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z,
			ForgeDirection side) {
		// Only the 'facing' side is solid
		return side == ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
	}
	
	/*@SideOnly(Side.CLIENT)
	private IIcon[] icons;*/
	public static IIcon MetalBase;
	public static IIcon Crucible4;
	public static IIcon SideIcon, BackIcon;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		MetalBase = ir.registerIcon("thaumcraft:metalbase");
		Crucible4 = ir.registerIcon("thaumcraft:crucible4");
		SideIcon = ir.registerIcon("essentialalchemy:CrystalFocuser_SIDE");
		BackIcon = ir.registerIcon("essentialalchemy:CrystalFocuser_BACK");
		/*icons = new IIcon[] {
			ir.registerIcon("thaumcraft:metalbase"), // Body
			ir.registerIcon("thaumcraft:crucible4") // Top
		};*/
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (side == meta)
			return Crucible4;
		if (ForgeDirection.OPPOSITES[side] == meta)
			return BackIcon;
		return SideIcon;
		//return side == meta ? Crucible4 : SideIcon;
	}
	
	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase eb, ItemStack is) {
		int meta = Lib.getDirInt(eb, x, y, z); // Uses Piston placement rules
		
		w.setBlockMetadataWithNotify(x, y, z, meta, 2);
	}
	
	@Override
	public int getRenderType() {
		return ClientProxy.crystalFocusRenderType;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileCrystalFocus();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

}
