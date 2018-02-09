package editor;

/**
 * this is what the communication module sees
 * @author Vladimir
 *
 */
public interface EditorCommI {

	public void receiveInsertChar(char ch, int position);
	
	public void receiveDeleteChar(int position);
}
