package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import struct.CommandQueue;

import network.NetworkNode;

/**
 * thread that broadcasts commands to clients
 * 
 * @author Vladimir
 *
 */
public class BroadcastThread extends NetworkNode {

	CommandQueue commandQ;
	HashMap<String, SocketChannel> clients; // clients are stored in this map
	
	public BroadcastThread(CommandQueue commandQ) {
		this.commandQ = commandQ;
		clients = new HashMap<String, SocketChannel>();
	}
	
	/**
	 * register a new client
	 */
	public void addClient(String client) {
		try {
			// client string has the form [IP]:[port]
			String[] tokens = client.split(":");
			String ip = tokens[0];
			int port = Integer.parseInt(tokens[1]);
			
			// open a connection with the client
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			socketChannel.connect(new InetSocketAddress(ip, port));
			
			// store the client in the clients map
			clients.put(client, socketChannel);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * unregister client
	 */
	public void removeClient(String client) {
		SocketChannel socketChannel = clients.get(client);
		send("exit", socketChannel);
		clients.remove(client);
		
		// close the connection with the client
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * broadcast a command to all clients
	 */
	private void broadcastCommand(String command) {
		for(SocketChannel clientSocket : clients.values()) {
			try {
				send(command, clientSocket);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		while(!stop) {
			// this will block until the queue has elements
			String command = commandQ.getCommand();
			broadcastCommand(command);
		}
	}

	
	public HashMap<String, SocketChannel> getClients() {
		return clients;
	}
}
