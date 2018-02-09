package sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import editor.EditorGuiI;

/**
 * class that simulates the GUI
 * it reads commands from a file and sends them to the communication module
 * @author Vladimir
 *
 */
public class Simulator extends Thread {

	EditorGuiI editor;
	String filename;
	
	public Simulator(EditorGuiI editor, String filename) {
		this.editor = editor;
		this.filename = filename;
	}
	
	public void init() {
		this.start();
	}
	
	@Override
	public void run() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(filename));
			sc.useDelimiter("\\s*,\\s+");
			
			while(sc.hasNext()) {
				try {
					// commands have the form insert#@[char]#@[position] or delete#@[position]
					String command = sc.next();
					String[] tokens = command.split("#@");
					if(command.startsWith("insert") && tokens.length == 3) {
						editor.sendInsertChar(tokens[1].charAt(0), Integer.parseInt(tokens[2]));
					} else if(command.startsWith("delete") && tokens.length == 2) {
						editor.sendDeleteChar(Integer.parseInt(tokens[1]));
					} else {
						System.out.println("invalid command");
					}
				} catch (NumberFormatException e) {
					System.out.println("invalid command");
				}

				// add a delay between commands
				Random rand = new Random();
				sleep(100 + rand.nextInt(400));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			if(sc != null) {
				sc.close();
				editor.close();
			}
		}
	}
}
