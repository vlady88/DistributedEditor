package network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * abstract class for sending and receiving text messages over the network
 * 
 * @author Vladimir
 *
 */
public abstract class NetworkNode extends Thread {
	
	private static int BUFFER_SIZE = 512;
	
	ByteBuffer buffer;
	protected boolean stop = false;
	
	public NetworkNode() {
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

	/**
	 * send a string through the socketChannel
	 */
	public int send(String message, SocketChannel socketChannel) {
		try {
			// write the string to the socketChannel
			buffer.clear();
			buffer.put(message.getBytes());
			buffer.flip();
			while(buffer.hasRemaining()) {
			    socketChannel.write(buffer);
			}
			
			return message.length();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * receive a string from the socketChannel
	 */
	public String receive(SocketChannel socketChannel) {
		try {
			// transfer data from socketChannel to buffer
			buffer.clear();
			int bytesRead = socketChannel.read(buffer);
			if(bytesRead == -1) {
				System.out.println("Error receiving message");
				return null;
			}
			
			// transfer data from buffer to StringBuilder
			StringBuilder message = new StringBuilder();
			buffer.flip();
			while(buffer.hasRemaining()){
			      message.append((char)buffer.get());
			}
			
			return message.toString();
		} catch (Exception e) {
			if(!stop) {
				System.out.println("Error receiving message");
			}
		}
		
		return null;
	}

	public void stopNode() {
		stop = true;
	}
}
