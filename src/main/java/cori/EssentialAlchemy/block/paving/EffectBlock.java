package cori.EssentialAlchemy.block.paving;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import cori.EssentialAlchemy.EssentialAlchemy;
import cori.EssentialAlchemy.KeyLib;
import cori.EssentialAlchemy.Lib;
import cori.EssentialAlchemy.client.ClientProxy;
import cori.EssentialAlchemy.tile.TileEffectStone;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EffectBlock extends BlockContainer {
	private float cRed=1, cBlue=1, cGreen=1;
	public void SetCharge(float r,float g,float b) {
		cRed = r; cGreen = g; cBlue = b;
	}
	
	//protected Potion effect;
	
	protected Aspect core;
	protected int Maximum = 16;
	
	public EffectBlock(Aspect desired, int maxHeld) {
		super(Material.rock);
		
		setHardness(1.5f);
		setStepSound(Block.soundTypeStone);
		
		setCreativeTab(EssentialAlchemy.thaumTab);
		core = desired;
		Maximum = maxHeld;
		
		centerColor = new Color(core.getColor());
		float[] colorArray = centerColor.getRGBColorComponents(null);
		SetCharge(colorArray[0], colorArray[1], colorArray[2]);
	}
	
	@Override
	public boolean onBlockActivated(
			World w,  // World
			int x, int y, int z, // Block X, Y, Z 
			EntityPlayer player, // Player
			int side,  // Block side
			float rx, float ry, float rz) { // Relative offset X, Y, Z
		ItemStack held = player.inventory.getCurrentItem();
		
		if (held == null) { // Empty Hand, pop the gold off!
			if (w.getBlockMetadata(x, y, z) != 0) {
				
				w.setBlockMetadataWithNotify(x, y, z, 0, 2);
				if (w.isRemote) return true; // Client job is done
				EntityItem ei = new EntityItem(w,x+0.5,y+1.1,z+0.5,new ItemStack(Items.gold_ingot,1));
				//ei.motionY = 0.5f; // Popping upwards
				ei.delayBeforeCanPickup = 0; // Instant
				// Pop squarely
				ei.motionX = 0;
				ei.motionZ = 0;
				w.spawnEntityInWorld(ei);
				return true;
			}
			return false;
		}
		
		if (held.getItem() == Items.gold_ingot) { // Gold in hand, adorn the block
			// If the player doesn't have the research node
			if (!Lib.isResearchComplete(player.getCommandSenderName(), KeyLib.AUGMENTED_PAVING)) return false;
			if (w.getBlockMetadata(x, y, z) != 0) return false;
			
			if (player.inventory.consumeInventoryItem(Items.gold_ingot)) {
				w.setBlockMetadataWithNotify(x, y, z, 1, 2); // Set the block XYZ to meta 1 with flag 2 (Update)
				return true;
			}
		}
		
		return false;
	}
	
	// Allow placing levers! (A pet peeve)
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z,
			ForgeDirection side) {
		return true;
	}
	
	
	// Rendering doodads
	public static boolean BorderPhase = false;
	public static IIcon Border, BorderAug, Inside;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		Border =	ir.registerIcon("essentialalchemy:PavingStoneBorder");
		BorderAug = ir.registerIcon("essentialalchemy:PavingStoneRimAugment");
		Inside = 	ir.registerIcon("essentialalchemy:PavingStoneCenter");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BorderPhase ? (meta == 0 || side != 1) ? Border : BorderAug : Inside;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess w, int x,
			int y, int z) {
		if (BorderPhase == true) return 0xFFFFFF;
		
		EffectBlock b = (EffectBlock)w.getBlock(x, y, z);
		return b.getBlendColor();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ClientProxy.pavingStoneRenderType;
	}
	
	// Come on compile time, I'm counting on you
	private static float BYTEINV = 1f/255f;
	
	// Blend as Multiply on 60% Opacity, based on my prodding in Gimp to get a pleasing blend (Multiply is too harsh!)
	public Color centerColor;
	public int getBlendColor() { // May need to overload for stuff like VOID and ENTROPY aspects
		return new Color(
				0.3f + (centerColor.getRed() * 0.6f * BYTEINV), 
				0.3f + (centerColor.getGreen() * 0.6f * BYTEINV), 
				0.3f + (centerColor.getBlue() * 0.6f * BYTEINV)
					).getRGB();
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		
		ArrayList<ItemStack> al = super.getDrops(world, x, y, z, metadata, fortune);
		
		if (metadata != 0) al.add(new ItemStack(Items.gold_ingot));
		
		return al;
	}
	
	
	
	/*/
	 * Affect Stub
	 */
	public void ApplyEffect(EntityLivingBase eb, TileEntity te) {
		
	}
	
	protected boolean doSunkenBox = true;
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z) {
		// Slightly sunken collision box
		if (doSunkenBox)
			return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x+1, (y+1) - 0.125, z+1);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@Override
	public void onEntityWalking(World w, int x, int y, int z, Entity e) {
		if (e instanceof EntityLivingBase) 
			ApplyEffect((EntityLivingBase) e,w.getTileEntity(x, y, z));
	}
	
	@Override
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e) {
		if (Math.floor(e.posY) < y) return; // Collided with non-top
		
		if (e instanceof EntityLivingBase) 
			ApplyEffect((EntityLivingBase) e,w.getTileEntity(x, y, z));
	}
	
	// For custom glow drawing, override
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	/*@Override
	public int getLightOpacity() {
		// TODO Auto-generated method stub
		return 255;
	}
	
	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		return 255;
	}*/

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		TileEffectStone tr = new TileEffectStone();
		tr.coreAspect = core;
		tr.setChargedColor(cRed, cGreen, cBlue);
		tr.maximum = Maximum;
		return tr;
	}
}
