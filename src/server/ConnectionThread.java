package server;

import java.nio.channels.SocketChannel;

import network.NetworkNode;
import struct.CommandQueue;

/**
 * connection thread used by the server to communicate with a client
 * 
 * @author Vladimir
 *
 */
public class ConnectionThread extends NetworkNode {

	BroadcastThread bcastThread;
	CommandQueue commandQ;
	SocketChannel socketChannel;
	
	public ConnectionThread(BroadcastThread bcastThread, CommandQueue commandQ, SocketChannel socketChannel) {
		this.bcastThread = bcastThread;
		this.commandQ = commandQ;
		this.socketChannel = socketChannel;
	}
	
	/**
	 * process message from client
	 */
	public void processMessage(String message) {
		String[] tokens = message.split("#@");
		
		try {
			if(message.startsWith("unregister") && tokens.length == 2) {
				// unregister client
				// the message has the format "unregister#@address:port"
				bcastThread.removeClient(tokens[1]);
				stop = true;
			} else if (message.startsWith("insert") && tokens.length == 3) { 
				// insert char
				// the message has the format insert#@[char]#@[position]
				commandQ.putCommand(message);
			} else if (message.startsWith("delete") && tokens.length == 2) { 
				// delete char
				// the message has the format delete#@[position]
				commandQ.putCommand(message);
			} else {
				System.out.println("Invalid message format: " + message);
			}
		} catch(Exception e) {
			System.out.println("Invalid message format: " + e.toString());
		}
	}
	
	@Override
	public void run() {
		while(!stop) {
			String message = receive(socketChannel);
			processMessage(message);
		}
	}
}
