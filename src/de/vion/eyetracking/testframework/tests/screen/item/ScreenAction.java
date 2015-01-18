package de.vion.eyetracking.testframework.tests.screen.item;

/**
 * Represents a single action (left,right,above,onscreen)
 * 
 * @author André Pomp
 * 
 */
public class ScreenAction {

	private String text;
	private String unicode;

	public ScreenAction(String text, String unicode) {
		super();
		this.text = text;
		this.unicode = unicode;
	}

	public String getText() {
		return this.text;
	}

	public String getUnicode() {
		return this.unicode;
	}

}
