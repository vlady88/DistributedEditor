package ui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import editor.EditorGuiI;

/**
 * class that implements a simple window with a text area
 * 
 * @author Vladimir
 *
 */
public class Gui extends JFrame implements ActionListener, WindowListener, KeyListener, GuiI {
	
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 663;
	public static final int HEIGHT = 553;
	
	EditorGuiI editor;
	
	JPanel mainPanel;
	TextArea text;
	Font font;

	/**
	 *  initialize the visual components
	 */
	public Gui(String str) {
		super(str);

		mainPanel = new JPanel();
		mainPanel = (JPanel) getContentPane();
		mainPanel.setLayout(new FlowLayout());

		addWindowListener(this);

		font = new Font("Arial", Font.PLAIN, 14);

		text = new TextArea(30, 80);
		text.setFont(font);
		text.addKeyListener(this);

		mainPanel.add(text);
	}
	
	/**
	 * display the editor window
	 */
	public void init() {
		setSize(Gui.WIDTH, Gui.HEIGHT);
		setResizable(false);
		setLocationByPlatform(true);
		setVisible(true);
	}
	
	public void setEditor(EditorGuiI editor) {
		this.editor = editor;
	}
	
	public void insertChar(char ch, int position) {
		text.insert("" + ch, position);
	}
	
	public void deleteChar(int position) {
		text.replaceRange("", position, position + 1);
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	/**
	 * this is called when the "X" button is pressed to provide a smooth exit
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		editor.close();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	/**
	 * catch the keyboard events and send them to the other clients before displaying them
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		if(font.canDisplay(event.getKeyChar())) {
			// normal character pressed
			editor.sendInsertChar(event.getKeyChar(), text.getCaretPosition());
			event.consume();
		} else if(event.getKeyCode() == KeyEvent.VK_DELETE) {
			// "Delete" key pressed
			editor.sendDeleteChar(text.getCaretPosition());
			event.consume();
		} else if(event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			// "Backspace" key pressed
			editor.sendDeleteChar(text.getCaretPosition() - 1);
			event.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent event) {}

	@Override
	public void actionPerformed(ActionEvent arg0) {}

}
