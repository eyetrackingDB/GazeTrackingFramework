package de.vion.eyetracking.testframework.tests.word.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import de.vion.eyetracking.testframework.tests.word.logging.LogObjectWordCallback;

/**
 * The custom view for the word test
 * 
 * @author André Pomp
 * 
 */
public class WordTextView extends TextView {

	private Context context;
	private Rect rect = null;
	private Paint paint = new Paint();
	private String word;
	private int wordStartIndex = 0;
	private int wordStopIndex = 0;
	private boolean calculatePosition = false;
	private LogObjectWordCallback callback;

	public WordTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public WordTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public WordTextView(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setColor(Color.BLUE);
		this.paint.setStrokeWidth(3);

		if (this.calculatePosition) {
			calculateBoundingBox();
			this.calculatePosition = false;
			this.callback.onWordPositionCalculated(this.word, this.rect.left,
					this.rect.top, this.rect.right, this.rect.bottom,
					System.currentTimeMillis());

		}
		if (this.rect != null) {
			canvas.drawRect(this.rect, this.paint);
		}
	}

	private void calculateBoundingBox() {
		// For more information, see (found the solution there)
		// http://stackoverflow.com/questions/13385639/get-absolute-position-for-a-given-offset-on-textview-android
		Rect parentTextViewRect = new Rect();

		int startOffsetOfClickedText = this.wordStartIndex;
		int endOffsetOfClickedText = this.wordStopIndex;

		// Initialize values for the computing of clickedText position
		Layout textViewLayout = this.getLayout();

		// Initialize values for the computing of clickedText position
		double startXCoordinatesOfClickedText = textViewLayout
				.getPrimaryHorizontal(startOffsetOfClickedText);
		double endXCoordinatesOfClickedText = textViewLayout
				.getPrimaryHorizontal(endOffsetOfClickedText);

		// Get the rectangle of the clicked text
		int currentLineStartOffset = textViewLayout
				.getLineForOffset(startOffsetOfClickedText);

		int currentLineEndOffset = textViewLayout
				.getLineForOffset(endOffsetOfClickedText);
		boolean keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset;
		textViewLayout
				.getLineBounds(currentLineStartOffset, parentTextViewRect);

		// Update the rectangle position to his real position on screen
		int[] parentTextViewLocation = { 0, 0 };
		this.getLocationOnScreen(parentTextViewLocation);

		double parentTextViewTopAndBottomOffset = (
		// parentTextViewLocation[1] -
		this.getScrollY() + this.getCompoundPaddingTop());

		parentTextViewRect.top += parentTextViewTopAndBottomOffset;
		parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;

		// In the case of multi line text, we have to choose what rectangle
		if (keywordIsInMultiLine) {

			WindowManager wm = (WindowManager) this.context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();

			Point size = new Point();
			display.getSize(size);

			int screenHeight = size.y;
			int dyTop = parentTextViewRect.top;
			int dyBottom = screenHeight - parentTextViewRect.bottom;
			boolean onTop = dyTop > dyBottom;

			if (onTop) {
				endXCoordinatesOfClickedText = textViewLayout
						.getLineRight(currentLineStartOffset);
			} else {
				parentTextViewRect = new Rect();
				textViewLayout.getLineBounds(currentLineEndOffset,
						parentTextViewRect);
				parentTextViewRect.top += parentTextViewTopAndBottomOffset;
				parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;
				startXCoordinatesOfClickedText = textViewLayout
						.getLineLeft(currentLineEndOffset);
			}

		}

		parentTextViewRect.left += (parentTextViewLocation[0]
				+ startXCoordinatesOfClickedText
				+ this.getCompoundPaddingLeft() - this.getScrollX());
		parentTextViewRect.right = (int) (parentTextViewRect.left
				+ endXCoordinatesOfClickedText - startXCoordinatesOfClickedText);

		this.rect = parentTextViewRect;
	}

	public void setIndex(String word, int startIndex, int stopIndex) {
		this.wordStartIndex = startIndex;
		this.wordStopIndex = stopIndex;
		this.word = word;
		this.calculatePosition = true;
	}

	public void setCallback(LogObjectWordCallback callback) {
		this.callback = callback;
	}
}