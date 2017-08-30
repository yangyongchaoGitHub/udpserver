package com.wenyun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wenyun.model.Client;
import com.wenyun.protocal.Packet;

public class Server {
	static DatagramChannel reveiveChannel;
	static Selector selector;
	private static DatagramSocket socket;

	/*
	 * private static DatagramPacket listenPacket; private static DatagramSocket
	 * serverSocket;
	 */
	private static int port = 10561;
	private static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();

	public static void main(String[] args) {
		// ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			reveiveChannel = DatagramChannel.open();
			reveiveChannel.configureBlocking(false);
			socket = reveiveChannel.socket();
			socket.bind(new InetSocketAddress(port));
			selector = Selector.open();
			reveiveChannel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		while (true) {

			int nKeys = 0;
			try {
				Thread.sleep(50);
				nKeys = selector.select();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (nKeys > 0) {
				Set<SelectionKey> keys = selector.selectedKeys();
				for (SelectionKey key : keys) {
					keys.remove(key);
					if (key.isReadable()) {
						// 有流可读取
						//System.out.println("test 2");
						ByteBuffer buffer = ByteBuffer.allocate(50);

						DatagramChannel sc = (DatagramChannel) key.channel();
						InetSocketAddress isa = null;
						Client client = null;
						try {
							isa = (InetSocketAddress) sc.receive(buffer);

							key.interestOps(SelectionKey.OP_READ);
							System.out.println("client ----> IP: " + isa.getAddress().getHostAddress() + ", port: "
									+ isa.getPort());
							//System.out.println("receiveBuffer.position() = " + buffer.position());
							client = clients.get(isa.getAddress().getHostAddress());
							if (client == null) {
								client = new Client();
								client.setChannel(sc);
								client.setAddress(isa);
								client.setTs(System.currentTimeMillis());
								clients.put(isa.getAddress().getHostAddress(), client);
							} else {
								client.setChannel(sc);
								client.setAddress(isa);
								client.setTs(System.currentTimeMillis());
							}
							
							buffer.flip();
							switch (buffer.array()[0]) {
							case 0x01:
								String result = "";
								System.out.println("clients size = " + clients.size());
								for (Map.Entry<String, Client> entry : clients.entrySet()) {
									if (!entry.getKey().equals(client.getAddress().toString())) {
										//System.out.println(client.getAddress().toString() + " " + entry.getValue().getAddress().getHostName());
										result += entry.getValue().getAddress().getHostName() + " ";
									}
								}
								byte[] data = result.getBytes();
								ByteBuffer buffer2 = ByteBuffer.allocate(data.length);
								buffer2.put(data);
								//System.arraycopy(data, 0, buffer2.array(), 0, data.length);
								buffer2.flip();
								//sc.send(buffer2, isa);
								response(buffer2, sc, isa);
								break;

							case 0x02:
								// ByteBuffer bb = ByteBuffer.allocate(30);
								// bb.put(listenPacket.getData());
								System.out.println(
										"data size = " + buffer.position() + " " + new String(buffer.array()));
								// byte[] msg = listenPacket.getData();
								
								byte[] data1 = "11".getBytes();
								ByteBuffer buffer21 = ByteBuffer.allocate(data1.length);
								buffer21.put(data1);
								//System.arraycopy(data1, 0, buffer21.array(), 0, data1.length);
								buffer21.flip();
								response(buffer21, sc, isa);
								// response("11".getBytes(), listenPacket);
								break;
							case 0x03:
								for (Map.Entry<String, Client> entry : clients.entrySet()) {
									if (!entry.getKey().equals(client.getAddress().getHostName().toString())) {
										System.out.println(entry.getValue().getChannel() + 
												" " + entry.getValue().getAddress() + 
												" " + client.getAddress().getHostName());
										sendOrder(entry.getValue().getChannel(), entry.getValue().getAddress());
									}
								}
								break;
								
							default:
								break;
							}

							//sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
				}
			}
		}
	}

	private static void sendOrder(DatagramChannel datagramChannel, InetSocketAddress inetSocketAddress) {
		byte[] d = new byte[]{0x01, 0x01, 0x01};
		ByteBuffer bb = ByteBuffer.allocate(3);
		bb.put(d);
		try {
			datagramChannel.send(bb, inetSocketAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void response(ByteBuffer data, DatagramChannel dc, SocketAddress isa) {
		try {
			//socket.send(new DatagramPacket(data.array(), data.array().length, isa));
			//reveiveChannel.send(data, target);
			//dc.write(data);
			/*System.out.println("response" + data.array().length + " " + isa);
			for(byte b : data.array()) {
				System.out.print(b);
			}*/
			dc.send(data, isa);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getHex(byte[] bytes) {
		String result = "";
		for (byte b : bytes) {
			String str = Integer.toHexString(b & 0xFF);
			if (str.length() == 1) {
				str = '0' + str;
			}
			result += str;
		}
		return result;
	}

}
