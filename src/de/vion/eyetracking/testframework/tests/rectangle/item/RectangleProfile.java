package de.vion.eyetracking.testframework.tests.rectangle.item;

/**
 * Represents the profile for a sequence of rectangles
 * 
 * @author André Pomp
 * 
 */
public class RectangleProfile {

	private int numberOfRows = 0;
	private int numberOfColumns = 0;
	private int numberOfRectangles = 0;

	public RectangleProfile(int numberOfRows, int numberOfColumns) {
		super();
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		this.numberOfRectangles = numberOfRows * numberOfColumns;
	}

	public int getNumberOfRows() {
		return this.numberOfRows;
	}

	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	public int getNumberOfRectangles() {
		return this.numberOfRectangles;
	}
}