package struct;

import java.util.LinkedList;
import java.util.List;

/**
 * queue used for broadcasting the commands issued by clients
 * 
 * @author Vladimir
 *
 */
public class CommandQueue {

	LinkedList<String> commands;
	
	public CommandQueue() {
		commands = new LinkedList<String>();
	}
	
	/**
	 * get the first command from the queue and remove it
	 */
	public synchronized String getCommand() {
		if(commands.isEmpty()) {
			try {
				// wait until the queue has elements
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		return commands.removeFirst();
	}
	
	/**
	 * get all commands from the queue and remove them
	 */
	public synchronized List<String> getAllComands() {
		List<String> copyList = new LinkedList<String>(commands);
		commands.clear();
		
		return copyList;
	}
	
	/**
	 * put command in the queue
	 */
	public synchronized void putCommand(String command) {
		commands.addLast(command);
		notify(); // notify other threads that the queue has elements
	}
	
	@Override
	public String toString() {
		return commands.toString();
	}
}
