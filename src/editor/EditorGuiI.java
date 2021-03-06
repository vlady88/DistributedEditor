package editor;

/**
 * interface exposed by the editor to the GUI
 *
 * @author Vladimir
 *
 */
public interface EditorGuiI {

	public void sendInsertChar(char ch, int position);
	
	public void sendDeleteChar(int position);
	
	public void close();
}
