package com.wenyun.protocal;

import java.nio.ByteBuffer;

public class Packet {
	public static int login = 1;
	public static int logout = 2;
	public static int heart = 3;
	public static int bind = 4; //high ==> bind ==> low
	//public static int 
	
	private byte[] start;
	private byte end;
	private byte target;
	private ByteBuffer data;
	private long sn;
	
	public byte getTarget() {
		return target;
	}
	public void setTarget(byte target) {
		this.target = target;
	}
	public ByteBuffer getData() {
		return data;
	}
	public void setData(ByteBuffer data) {
		this.data = data;
	}
	
	public int decoder(byte[] rawData) {
		if (rawData.length < 1) {
			return 1;
		}
		
		if (rawData.length == 1) {
			target = rawData[0];
			return 2;
		}
		
		target = rawData[0];
		data = ByteBuffer.allocate(rawData.length-1);
		System.arraycopy(rawData, 1, data.array(), 0, data.capacity());
		return 0;
	}
	
	public byte[] encode() {
		byte []result = new byte[1+data.capacity()];
		result[0] = target;
		System.arraycopy(data.array(), 1, result, 1, result.length - 1);
		return result;
	}
}
