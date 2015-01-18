package de.vion.eyetracking.testframework.tests.rectangle.item;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Represents a single rectangle on the screen
 * 
 * @author André Pomp
 * 
 */
public class Rectangle {

	private static final int CIRCLE_RADIUS = 15;

	// The row and the col of the current rectangle
	private int row;
	private int col;

	// The line for the rectangles
	private Paint activePaint = new Paint();
	private Paint inactivePaint = new Paint();

	// Only draw the active circle
	private Paint circlePaint = new Paint();

	private Point topLeft;
	private Point bottomRight;
	private Point center;

	public Rectangle(int row, int col, Point topLeft, Point bottomRight) {
		super();
		this.row = row;
		this.col = col;

		this.topLeft = topLeft;
		this.bottomRight = bottomRight;

		this.center = new Point();
		this.center.x = topLeft.x + (bottomRight.x - topLeft.x) / 2;
		this.center.y = topLeft.y + (bottomRight.y - topLeft.y) / 2;

		this.activePaint.setStyle(Paint.Style.FILL);
		this.activePaint.setColor(Color.GREEN);

		this.inactivePaint.setStyle(Paint.Style.FILL);
		this.inactivePaint.setColor(Color.DKGRAY);

		this.circlePaint.setStyle(Paint.Style.FILL);
		this.circlePaint.setColor(Color.RED);
	}

	public void drawRectangle(Canvas canvas, boolean active) {
		if (active) {
			canvas.drawRect(this.topLeft.x, this.topLeft.y, this.bottomRight.x,
					this.bottomRight.y, this.activePaint);
			canvas.drawCircle(this.center.x, this.center.y, CIRCLE_RADIUS,
					this.circlePaint);
		} else {
			canvas.drawRect(this.topLeft.x, this.topLeft.y, this.bottomRight.x,
					this.bottomRight.y, this.inactivePaint);
		}
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public Point getTopLeft() {
		return this.topLeft;
	}

	public Point getBottomRight() {
		return this.bottomRight;
	}
}