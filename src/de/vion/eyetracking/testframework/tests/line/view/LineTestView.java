package de.vion.eyetracking.testframework.tests.line.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import de.vion.eyetracking.testframework.tests.line.logging.LogObjectLineCallback;

/**
 * The custom view for the line test
 * 
 * @author André Pomp
 * 
 */
public class LineTestView extends View {

	private static final int marginTopBottom = 100; // px margin at the top
	private static final int marginLeftRight = 100; // px marging to left and
													// right

	private static final int CIRCLE_RADIUS = 30;

	private Paint paint = new Paint();
	private Point center;

	private int numberOfLines;
	private int currentNumberOfLines = 1;
	private int velocityX;
	private int velocityY;
	private boolean doOnce = false;

	private LogObjectLineCallback callback;

	public LineTestView(Context context) {
		super(context);
	}

	public LineTestView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public LineTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setValues(LogObjectLineCallback callback, int numberOfLines,
			int velocity) {
		this.numberOfLines = numberOfLines;
		this.velocityX = velocity;
		this.callback = callback;
		this.paint.setColor(Color.RED);
		this.paint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!this.doOnce) {
			this.velocityY = (canvas.getHeight() - (2 * marginTopBottom))
					/ this.numberOfLines;
			this.doOnce = true;
		}

		// Draw the preprinted lines
		for (int i = 0; i < this.numberOfLines; i++) {
			canvas.drawLine(marginLeftRight, marginTopBottom
					+ (i * this.velocityY),
					canvas.getWidth() - marginLeftRight, marginTopBottom
							+ (i * this.velocityY), this.paint);
		}

		// Draw the point
		if (this.center == null) {
			this.center = new Point(marginLeftRight, marginTopBottom);
			this.callback.onPositionUpdated(System.currentTimeMillis(),
					this.center.y, this.currentNumberOfLines);
		} else {
			this.center.x += this.velocityX;

			if (this.center.x > (canvas.getWidth() - (marginLeftRight))) {
				this.center.x = marginLeftRight;
				this.center.y += this.velocityY;
				this.currentNumberOfLines++;

				if (this.currentNumberOfLines <= this.numberOfLines) {
					this.callback.onPositionUpdated(System.currentTimeMillis(),
							this.center.y, this.currentNumberOfLines);
				}
			}
		}

		if (this.currentNumberOfLines <= this.numberOfLines) {
			canvas.drawCircle(this.center.x, this.center.y, CIRCLE_RADIUS,
					this.paint);
		}
	}

	public void setCallback(LogObjectLineCallback callback) {
		this.callback = callback;
	}

	public void resetBallPosition() {
		this.center = null;
		this.currentNumberOfLines = 1;
		postInvalidate();
	}

	public void updateBallPosition() {
		postInvalidate();
	}

	public int getCurrentNumberOfLines() {
		return this.currentNumberOfLines;
	}
}