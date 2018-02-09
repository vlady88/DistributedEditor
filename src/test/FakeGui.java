package test;

import editor.EditorGuiI;
import ui.GuiI;

/**
 * 
 * Mock GUI used for unit testing
 * uses a string buffer instead of the visual editor
 * 
 * @author Vladimir
 *
 */
public class FakeGui implements GuiI{

	EditorGuiI editor;
	StringBuilder text;
	
	public FakeGui() {
		text = new StringBuilder();
	}
	
	@Override
	public void setEditor(EditorGuiI editor) {
		this.editor = editor;
	}

	@Override
	public void insertChar(char ch, int position) {
		text.insert(position, ch);
	}

	@Override
	public void deleteChar(int position) {
		text.deleteCharAt(position);
	}

	@Override
	public void init() {}
	
	public String getText() {
		return text.toString();
	}

}
