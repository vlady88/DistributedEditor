package editor;

/**
 * interface exposed by the editor to the communicator
 * 
 * @author Vladimir
 *
 */
public interface EditorCommI {

	public void receiveInsertChar(char ch, int position);
	
	public void receiveDeleteChar(int position);
}
