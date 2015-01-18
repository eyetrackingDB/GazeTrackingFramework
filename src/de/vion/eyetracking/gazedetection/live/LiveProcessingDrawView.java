package de.vion.eyetracking.gazedetection.live;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The view for the live camera gaze tracking on which we draw the gaze points
 * 
 * @author André Pomp
 * 
 */
public class LiveProcessingDrawView extends SurfaceView implements SurfaceHolder.Callback {

	private Point point = new Point();
	private Paint paint = new Paint();

	public LiveProcessingDrawView(Context context) {
		super(context);
		setBackgroundColor(Color.TRANSPARENT);
		setClickable(false);
		setEnabled(false);
		this.paint.setColor(Color.YELLOW);
		this.paint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		if (this.point != null) {
			canvas.drawCircle(this.point.x, this.point.y, 20, this.paint);
		} else {
			canvas.drawCircle(0, 0, 20, this.paint);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// do nothing
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// do nothing
	}

	public void changePoint(int x, int y) {
		if (this.point == null) {
			this.point = new Point();
		}
		this.point.x = x;
		this.point.y = y;
	}
}