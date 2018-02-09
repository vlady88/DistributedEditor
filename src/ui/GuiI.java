package ui;

import editor.EditorGuiI;

public interface GuiI {

	public void init();
	
	public void setEditor(EditorGuiI editor);
	
	public void insertChar(char ch, int position);
	
	public void deleteChar(int position);
}
