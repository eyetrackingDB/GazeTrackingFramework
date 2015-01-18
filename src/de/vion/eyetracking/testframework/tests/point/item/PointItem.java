package de.vion.eyetracking.testframework.tests.point.item;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Represents a single point on the screen
 * 
 * @author André Pomp
 * 
 */
public class PointItem {

	private static final int CIRCLE_RADIUS = 40;

	private Paint activePaint = new Paint();
	private Paint inactivePaint = new Paint();

	private Point center;

	public PointItem(Point center) {
		super();
		this.center = center;
		this.activePaint.setStyle(Paint.Style.FILL);
		this.activePaint.setColor(Color.RED);
		this.inactivePaint.setStyle(Paint.Style.FILL);
		this.inactivePaint.setColor(Color.BLACK);
	}

	public void drawPoint(Canvas canvas, boolean active) {
		if (active) {
			canvas.drawCircle(this.center.x, this.center.y, CIRCLE_RADIUS,
					this.activePaint);
		} else {
			canvas.drawCircle(this.center.x, this.center.y, CIRCLE_RADIUS,
					this.inactivePaint);
		}
	}

	public Point getCenter() {
		return this.center;
	}
}