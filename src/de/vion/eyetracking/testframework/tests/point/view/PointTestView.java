package de.vion.eyetracking.testframework.tests.point.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import de.vion.eyetracking.testframework.tests.point.item.PointItem;

/**
 * The custom view for the point test
 * 
 * @author André Pomp
 * 
 */
public class PointTestView extends View {

	private boolean doOnce = false;

	private List<PointItem> points = new ArrayList<PointItem>();

	private int currentPos = -1;
	private int numberOfRows = 0;
	private int numberOfColums = 0;
	private PointTestViewCreatedCallback callback;

	public PointTestView(Context context) {
		super(context);
	}

	public PointTestView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PointTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPoints(int numberOfRows, int numberOfColums) {
		this.numberOfRows = numberOfRows;
		this.numberOfColums = numberOfColums;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!this.doOnce) {
			// Calculate the positions for the points
			createPoints(canvas);
			this.callback.pointsCreated();
			this.doOnce = true;
		}

		for (int i = 0; i < this.points.size(); i++) {
			if (i == this.currentPos) {
				this.points.get(i).drawPoint(canvas, true);
			} else {
				this.points.get(i).drawPoint(canvas, false);
			}
		}
	}

	private void createPoints(Canvas canvas) {
		int heightBetweenPoints = canvas.getHeight() / (2 * this.numberOfRows);
		int widthBetweenPoints = canvas.getWidth() / (2 * this.numberOfColums);

		int currentHeightBetweenPoints = heightBetweenPoints;
		int currentWidthBetweenPoints = widthBetweenPoints;
		for (int i = 1; i <= this.numberOfRows; i++) {
			currentWidthBetweenPoints = widthBetweenPoints;
			if (i > 1) {
				currentHeightBetweenPoints += (2 * heightBetweenPoints);
			}

			for (int j = 1; j <= this.numberOfColums; j++) {
				Point center = new Point();

				if (j == 1) {
					center.x = currentWidthBetweenPoints;
				} else {
					currentWidthBetweenPoints += (2 * widthBetweenPoints);
					center.x = currentWidthBetweenPoints;
				}

				center.y = currentHeightBetweenPoints;

				PointItem point = new PointItem(center);
				this.points.add(point);
			}
		}
		Collections.shuffle(this.points);
	}

	public PointItem getPoint(int pos) {
		return this.points.get(pos);
	}

	public void setCallback(PointTestViewCreatedCallback callback) {
		this.callback = callback;
	}

	public void updatePointPos(int currentPos) {
		this.currentPos = currentPos;
		postInvalidate();
	}
}