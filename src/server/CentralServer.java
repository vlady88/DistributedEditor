package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import network.NetworkNode;
import struct.CommandQueue;

/**
 * central server used to dispatch commands to client editors
 * it makes use of a queue to order the commands
 * 
 * @author Vladimir
 *
 */
public class CentralServer extends NetworkNode {
	
	int port;
	CommandQueue commandQ; // queue that stores messages to be delivered
	BroadcastThread broadcastThread;
	ServerSocketChannel serverSocketChannel;
	
	public CentralServer(int port) {
		this.port = port;
		commandQ = new CommandQueue();
	}
	
	/**
	 * process message from client
	 */
	public void processMessage(String message, SocketChannel socketChannel) {
		String[] tokens = message.split("#@");
		
		try {
			if(message.startsWith("register") && tokens.length == 2) {
				// new client
				// the message is of the form "register [address:port]"
				// starts a connection thread for that client
				ConnectionThread ct = new ConnectionThread(broadcastThread, commandQ, socketChannel);
				ct.start();
				// register the client to the broadcast thread, so it will receive commands from others
				broadcastThread.addClient(tokens[1]);
			} else {
				System.out.println("Invalid message format: " + message);
			}
		} catch(Exception e) {
			System.out.println("Invalid message format: " + e.toString());
		}
	}
	
	@Override
	public void run() {
		try {
			// initialize broadcast thread
			broadcastThread = new BroadcastThread(commandQ);
			broadcastThread.start();
			
			// initialize server socketChannel for accepting new connections
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
	
			// wait for new connections
			while(true) {
			    SocketChannel socketChannel = serverSocketChannel.accept();
			    String message = receive(socketChannel);
			    processMessage(message, socketChannel);
			}
		} catch (Exception e) {
			// if server was stopped by stopServer(), then no need to worry 
			if(!stop) {
				e.printStackTrace();
			}
			broadcastThread.stopNode();
			commandQ.putCommand("Exit");
		}
	}
	
	public void stopServer() {
		stopNode();
		try {
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// arguments are "-p [port]"
		if(args.length < 2) {
			System.err.println("Invalid number of parameters");
			System.exit(1);
		}
		
		// parse the arguments
		int port = -1;
		String option = null;
		for(String arg : args) {
			if(arg.startsWith("-")) {
				option = arg;
			} else {
				if(option.equals("-p")) {
					port = Integer.parseInt(arg);
				}
			}
		}
		
		// start the server thread
		CentralServer server = new CentralServer(port);
		server.start();
	}
	
	public BroadcastThread getBroadcastThread() {
		return broadcastThread;
	}
}
