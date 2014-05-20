package cori.EssentialAlchemy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITickedByTile {
	public void OnTileUpdate(World w, TileEntity te, int x, int y, int z);
}
