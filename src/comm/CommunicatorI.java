package comm;

import editor.EditorCommI;

/**
 * interface for the communication module
 * 
 * @author Vladimir
 *
 */
public interface CommunicatorI {

	public void setEditor(EditorCommI editor);
	
	public void insertChar(char ch, int position);
	
	public void deleteChar(int position);
	
	public void init();
	
	public void close();
}
