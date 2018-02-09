package comm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import network.NetworkNode;
import editor.EditorCommI;

/**
 * communication module for the centralized topology
 * 
 * @author Vladimir
 *
 */
public class CentralServerCommunicator extends NetworkNode implements CommunicatorI {
	
	SocketChannel socketChannel; // socket used to send commands to the server
	EditorCommI editor; // interface to the editor object
	ReceiverThread receiver; // thread used to receive commands
	String serverIp;
	int serverPort;
	int port;
	
	public CentralServerCommunicator(int port, String serverIp, int serverPort) {
		this.port = port;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}
	
	public void setEditor(EditorCommI editor) {
		this.editor = editor;
	}

	/**
	 * connect to the server
	 */
	private void connect() {
		try {
			// we use a blocking socket to make sure that the messages arrive in the right order
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			socketChannel.connect(new InetSocketAddress(serverIp, serverPort));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendCommand(String command) {
		send(command, socketChannel);
	}
	
	/**
	 * send command for inserting a char at the specified position
	 */
	@Override
	public void insertChar(char ch, int position) {
		sendCommand("insert#@" + ch + "#@" + position);
	}

	/**
	 * send command for deleting the char at the specified position
	 */
	@Override
	public void deleteChar(int position) {
		sendCommand("delete#@" + position);
	}

	/**
	 * initialize communication
	 */
	@Override
	public void init() {
		// start the receiver thread
		receiver = new ReceiverThread(port, editor);
		receiver.start();
		
		try {
			// connect to the server
			connect();
			String ip = InetAddress.getLocalHost().getHostAddress();
			sendCommand("register#@" + ip + ":" + port); // register to the server
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method called when leaving the system
	 * frees the resources
	 */
	@Override
	public void close() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			sendCommand("unregister#@" + ip + ":" + port);
			socketChannel.close();
			receiver.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
