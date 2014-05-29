package cori.EssentialAlchemy.Net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import thaumcraft.common.lib.network.AbstractPacket;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.*;

@ChannelHandler.Sharable
public class PacketHandler extends
		MessageToMessageCodec<FMLProxyPacket, AbstractPacket> {
	
	private EnumMap<Side,FMLEmbeddedChannel> channels;
	private LinkedList<Class<? extends AbstractPacket>> packets = new LinkedList<Class<? extends AbstractPacket>>();
	private boolean isDoneInit = false;
	
	public boolean registerPacket(Class<? extends AbstractPacket> clss) {
		if (packets.size() > 256) return false; // Full
		if (packets.contains(clss)) return false; // Already contained
		if (isDoneInit) return false; // We are not longer accepting packets
		
		packets.add(clss);
		return true;
	}
	
	public void init() {
		channels = NetworkRegistry.INSTANCE.newChannel("EssAlch", this);
	}
	
	public void finalizeInit() {
		if (isDoneInit) return;
		isDoneInit = true;
		
		Collections.sort(packets, new Comparator<Class<? extends AbstractPacket>>() {

			@Override
			public int compare(Class<? extends AbstractPacket> o1,
					Class<? extends AbstractPacket> o2) {
				int com = String.CASE_INSENSITIVE_ORDER.compare(o1.getCanonicalName(), o2.getCanonicalName());
				if (com == 0) // If equilivant
					com = o1.getCanonicalName().compareTo(o2.getCanonicalName());
				return com;
			}
		});
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractPacket msg,
			List<Object> out) throws Exception {
		ByteBuf buffer = Unpooled.buffer();
		Class<? extends AbstractPacket> cls = msg.getClass();
		if (!packets.contains(cls))
			throw new Exception("Invalid Packet specified: " + cls.getSimpleName());
		
		byte descriptive = (byte)packets.indexOf(cls);
		buffer.writeByte(descriptive);
		msg.encodeInto(ctx, buffer);
		FMLProxyPacket prxy = new FMLProxyPacket(buffer.copy(),ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(prxy);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg,
			List<Object> out) throws Exception {
		
		ByteBuf incoming = msg.payload();
		byte descriptive = incoming.readByte();
		
		Class<? extends AbstractPacket> cls = packets.get(descriptive);
		if (cls == null) 
			throw new Exception("Invalid Packet in decoding stream " + descriptive);
		
		AbstractPacket pckt = cls.newInstance();
		pckt.decodeInto(ctx, incoming.slice());
		
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				pckt.handleClientSide(Minecraft.getMinecraft().thePlayer);
				break;
			case SERVER:
				INetHandler netHndlr = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				pckt.handleServerSide(((NetHandlerPlayServer)netHndlr).playerEntity);
				break;
		}
		
		out.add(pckt);
	}
	
	
	public void SendToServer(AbstractPacket pckt) {
		channels.get(Side.CLIENT)
			.attr(FMLOutboundHandler.FML_MESSAGETARGET)
			.set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeAndFlush(pckt);
	}
}
