package com.wenyun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wenyun.model.Client;
import com.wenyun.protocal.Packet;

public class Server {
	private static DatagramPacket listenPacket;
    private static DatagramSocket serverSocket;
    private static int port = 10561;
    private static ConcurrentHashMap<InetAddress, Client> clients = new ConcurrentHashMap<InetAddress, Client>();

	public static void main(String[] args) {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			serverSocket = null;
			e.printStackTrace();
		}
		
		if (serverSocket == null) {
			System.out.println("create socket error");
			System.exit(0);
		}

		byte[] recever = new byte[10];
		listenPacket = new DatagramPacket(recever, recever.length);
		while (true) {
			try {
				serverSocket.receive(listenPacket);
				//String date = new String(packet.getData(), 0, packet.getLength());
				
				System.out.println("listen udp packet " + listenPacket.getAddress() + " " + 
						listenPacket.getPort());
				Client client = clients.get(listenPacket.getAddress());
				if (client == null) {
					client = new Client();
					client.setIp(listenPacket.getAddress());
					client.setPort(listenPacket.getPort());
					client.setTs(System.currentTimeMillis());
					clients.put(listenPacket.getAddress(), client);
				}
				Packet packet = new Packet();
					packet.decoder(listenPacket.getData());
					
					switch (packet.getTarget()) {
					case 0x01:
						for (Map.Entry<InetAddress, Client> entry : clients.entrySet()) {
							String result = "";
							if (!entry.equals(client)) {
								result += entry.getValue().getIp().getHostAddress();
							}
						}
						byte[] lis = listenPacket.getData();
						for(byte l : lis ) {
							System.out.println(l & 0xff);
						}
						//System.out.println(new String(packet.getData().array(), 0, packet.getData().array().length));
						break;
					default:
						break;
					}
				//packet.getLength();
				response(listenPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void response(DatagramPacket datagramPacket) {
		try {
			serverSocket.send(
					new DatagramPacket("654/r/n".getBytes(), 3, datagramPacket.getAddress(), datagramPacket.getPort()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("return message " + datagramPacket.getAddress() + datagramPacket.getPort());
	}
	
	private static String getHex(byte[] bytes) {
		String result = "";
		for(byte b: bytes) {
			String str = Integer.toHexString(b & 0xFF);
	        if (str.length() == 1) {
	        	str = '0' + str;
	        }
	        result += str;
		}
		return result;
	}

}
