package com.wenyun.model;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class Client {
	private InetSocketAddress address;
	private long ts;
	private DatagramChannel channel;
	
	public DatagramChannel getChannel() {
		return channel;
	}
	public void setChannel(DatagramChannel channel) {
		this.channel = channel;
	}
	public InetSocketAddress getAddress() {
		return address;
	}
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
