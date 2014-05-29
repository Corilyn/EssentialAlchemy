package cori.EssentialAlchemy;

import cori.EssentialAlchemy.tile.TilePotionModifier;
import ibxm.Player;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.common.lib.network.AbstractPacket;

public class ModifierCyclePacket extends AbstractPacket {

	TilePotionModifier tpm;
	
	public ModifierCyclePacket() {}
	
	public ModifierCyclePacket(TilePotionModifier entity, int buttonID) {
		tpm = entity;
		id = buttonID;
	}
	
	int x, y, z, dim, id;
	
	@Override
	public void decodeInto(ChannelHandlerContext chc, ByteBuf b) {
		x=b.readInt();
		y=b.readInt();
		z=b.readInt();
		dim=b.readInt();
		id=b.readInt();
	}

	@Override
	public void encodeInto(ChannelHandlerContext chc, ByteBuf b) {
		x=tpm.xCoord;
		y=tpm.yCoord;
		z=tpm.zCoord;
		dim=tpm.getWorldObj().provider.dimensionId;
		b.writeInt(tpm.xCoord);
		b.writeInt(tpm.yCoord);
		b.writeInt(tpm.zCoord);
		dim = tpm.getWorldObj().provider.dimensionId;
		b.writeInt(dim);
		b.writeInt(id);
	}

	private void handle(EntityPlayer ep) {
		//EssentialAlchemy.lg.warn("Handling ModifierCyclePacket ID: " + id);
		
		MinecraftServer server = MinecraftServer.getServer();
		
		if (server != null) {
			World w = server.worldServerForDimension(dim);
			
			if (w == null) {
				EssentialAlchemy.lg.warn("No dimension of ID:" + dim + " found");
				return;
			}
			
			TileEntity tile = w.getTileEntity(x, y, z);
			if (tile instanceof TilePotionModifier) {
				tpm = (TilePotionModifier)tile;
				tpm.handleButton(id);
			}
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer ep) {
		handle(ep);
	}

	@Override
	public void handleServerSide(EntityPlayer ep) {
		handle(ep);
	}

}
