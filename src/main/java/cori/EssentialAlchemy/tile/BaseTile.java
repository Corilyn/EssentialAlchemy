package cori.EssentialAlchemy.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class BaseTile extends TileEntity {
	@Override
	public void readFromNBT(NBTTagCompound nbtc) {
		super.readFromNBT(nbtc);
		ReadCustom(nbtc);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtc) {
		super.writeToNBT(nbtc);
		WriteCustom(nbtc);
	}
	
	public void WriteCustom(NBTTagCompound nbtc) {};
	public void ReadCustom(NBTTagCompound nbtc) {};
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtc = new NBTTagCompound();
		writeToNBT(nbtc);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,-999,nbtc);
	}
	
	/*public Packet GetDescriptionPacket() {
		NBTTagCompound nbtc = new NBTTagCompound();
		writeToNBT(nbtc);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,-999,nbtc);
	}*/
	
	
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		ReadCustom(pkt.func_148857_g()); // Boy is this unintuitive
	}
}
