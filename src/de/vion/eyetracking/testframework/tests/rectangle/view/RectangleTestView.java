package de.vion.eyetracking.testframework.tests.rectangle.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import de.vion.eyetracking.testframework.tests.rectangle.item.Rectangle;

/**
 * The custom view for the rectangle test
 * 
 * @author André Pomp
 * 
 */
public class RectangleTestView extends View {

	private boolean doOnce = false;

	private List<Rectangle> rectangles = new ArrayList<Rectangle>();

	private int currentPos = -1;
	private int numberOfRows = 0;
	private int numberOfColums = 0;
	private boolean pause = false;

	private RectangleTestViewCreatedCallback callback;

	public RectangleTestView(Context context) {
		super(context);
	}

	public RectangleTestView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public RectangleTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setRectangles(int numberOfRows, int numberOfColums) {
		this.numberOfRows = numberOfRows;
		this.numberOfColums = numberOfColums;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!this.doOnce) {
			// Calculate the positions for the points
			createRectangles(canvas);
			this.callback.rectanglesCreated();
			this.doOnce = true;
		}

		if (!this.pause) {
			for (int i = 0; i < this.rectangles.size(); i++) {
				if (i == this.currentPos) {
					this.rectangles.get(i).drawRectangle(canvas, true);
				} else {
					this.rectangles.get(i).drawRectangle(canvas, false);
				}
			}
		} else {
			canvas.drawColor(Color.DKGRAY);
		}
	}

	private void createRectangles(Canvas canvas) {
		int rectHeight = canvas.getHeight() / this.numberOfRows;
		int rectWidth = canvas.getWidth() / this.numberOfColums;

		for (int i = 0; i < this.numberOfRows; i++) {
			for (int j = 0; j < this.numberOfColums; j++) {
				Point topLeft = new Point();
				topLeft.x = rectWidth * j;
				topLeft.y = rectHeight * i;

				Point bottomRight = new Point();
				bottomRight.x = topLeft.x + rectWidth;
				bottomRight.y = topLeft.y + rectHeight;

				Rectangle rectangle = new Rectangle(i, j, topLeft, bottomRight);
				this.rectangles.add(rectangle);
			}
		}
		shuffleRectangles();
	}

	public Rectangle getRectangle(int pos) {
		return this.rectangles.get(pos);
	}

	public void setCallback(RectangleTestViewCreatedCallback callback) {
		this.callback = callback;
	}

	public void shuffleRectangles() {
		Collections.shuffle(this.rectangles, new Random(System.nanoTime()));
	}

	public void updateRectanglePos(int currentPos, boolean pause) {
		this.currentPos = currentPos;
		this.pause = pause;

		postInvalidate();
	}
}