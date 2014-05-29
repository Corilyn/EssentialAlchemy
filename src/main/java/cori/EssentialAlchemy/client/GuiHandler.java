package cori.EssentialAlchemy.client;

import cori.EssentialAlchemy.tile.ContainerModifier;
import cori.EssentialAlchemy.tile.TilePotionModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TilePotionModifier)
			return new ContainerModifier(player.inventory, (TilePotionModifier) te);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TilePotionModifier)
			return new GuiPotionModifier(player.inventory, (TilePotionModifier) te);
		
		return null;
	}

}
