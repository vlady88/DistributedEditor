package editor;

import comm.CentralServerCommunicator;
import comm.CommunicatorI;
import sim.Simulator;
import ui.Gui;
import ui.GuiI;

/**
 * this class acts as a mediator between the communication module and the GUI
 * 
 * @author Vladimir
 *
 */
public class DistributedEditor extends Thread implements EditorGuiI, EditorCommI {
	
	GuiI gui;
	CommunicatorI communicator;
	Simulator sim;
	
	public DistributedEditor(GuiI gui, CommunicatorI communicator) {
		this.gui = gui;
		this.communicator = communicator;
	}
	
	public void setSimulator(Simulator sim) {
		this.sim = sim;
	}
	
	public GuiI getGui() {
		return gui;
	}
	
	@Override
	public void run() {
		communicator.init();
		if(sim != null) {
			sim.init();
		} else {
			gui.init();
		}
	}
	
	/**
	 * send "insert char" command to other clients
	 */
	@Override
	public void sendInsertChar(char ch, int position) {
		communicator.insertChar(ch, position);
	}
	
	/**
	 * send "delete char" command to other clients
	 */
	@Override
	public void sendDeleteChar(int position) {
		communicator.deleteChar(position);
	}
	
	/**
	 * receive "insert char" command from other clients and propagate it to the GUI
	 */
	@Override
	public void receiveInsertChar(char ch, int position) {
		gui.insertChar(ch, position);
	}

	/**
	 * receive "delete char" command from other clients and propagate it to the GUI
	 */
	@Override
	public void receiveDeleteChar(int position) {
		gui.deleteChar(position);
	}
	
	@Override
	public void close() {
		communicator.close();
	}
	
	public static void main(String args[]) {
		int port = -1;
		String serverAddr = null;
		String option = "";
		String filename = null;
		boolean simulate = false;
	
		// parse parameters, which have the format "-[param_name] [param_value]"
		for(String arg : args) {
			if(arg.startsWith("-")) {
				option = arg;
				if(option.equals("-sim")) {
					simulate = true;
				}
			} else {
				if(option.equals("-p")) {
					// parse the port number
					port = Integer.parseInt(arg);
				} else if(option.equals("-s")) {
					// parse the server address
					serverAddr = arg;
				} else if(option.equals("-f")) {
					// parse commands file (for simulator mode)
					filename = arg;
				}
			}
		}
		
		// according to parameters, choose a communication model
		CommunicatorI communicator = null;
		if(serverAddr != null) {
			String[] tokens = serverAddr.split(":");
			String serverIp = tokens[0];
			int serverPort = Integer.parseInt(tokens[1]);
			
			communicator = new CentralServerCommunicator(port, serverIp, serverPort);
		}
		
		// initializations
		Gui gui = new Gui("DistrEditor");
		DistributedEditor de = new DistributedEditor(gui, communicator);
		if(simulate) {
			de.setSimulator(new Simulator(de, filename));
		}
		
		gui.setEditor(de);
		communicator.setEditor(de);
		de.start();
	}

}
