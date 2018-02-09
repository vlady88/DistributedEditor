package comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import editor.EditorCommI;

import network.NetworkNode;

/**
 * thread used to receive commands from the server
 * 
 * @author Vladimir
 *
 */
public class ReceiverThread extends NetworkNode {

	int port;
	EditorCommI editor;
	SocketChannel socketChannel;
	
	public ReceiverThread(int port, EditorCommI editor) {
		this.port = port;
		this.editor = editor;
	}
	
	/**
	 * handle a new message from the server
	 */
	private void processMessage(String message) {
		String[] tokens = message.split("#@");
		
		try {
			if(message.startsWith("insert") && tokens.length == 3) { 
				// insert char command, having the format insert#@[char]#@[position]
				char ch = tokens[1].charAt(0);
				int position = Integer.parseInt(tokens[2]);
				editor.receiveInsertChar(ch, position);
			} else if(message.startsWith("delete") && tokens.length == 2) { 
				// delete char command, having the format delete#@[position]
				int position = Integer.parseInt(tokens[1]);
				editor.receiveDeleteChar(position);
			} else if(message.startsWith("exit")) { 
				// command to exit the system
				stop = true;
			} else {
				System.out.println("Invalid message format: " + message);
			}
		} catch (Exception e) {
			System.out.println("Invalid message format: " + e.toString());
		}
	}

	@Override
	public void run() {
		ServerSocketChannel serverSocketChannel = null;
		
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
	
			// this socket is used for receiving messages from the server
			socketChannel = serverSocketChannel.accept();
			
			while(!stop) {
				String message = receive(socketChannel);
				
				if(message != null) { // message can be null if close() is called
					processMessage(message);
				}
			}
			
			serverSocketChannel.close();
			socketChannel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		stopNode();
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
