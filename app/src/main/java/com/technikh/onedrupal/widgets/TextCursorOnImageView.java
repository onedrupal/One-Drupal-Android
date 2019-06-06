package com.technikh.onedrupal.widgets;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TextCursorOnImageView extends View {

	private Paint mRectPaint;

	private int mStartX = 0;
	private int mStartY = 0;
	private int mEndX = 0;
	private int mEndY = 0;
	private boolean mDrawRect = false;
	private TextPaint mTextPaint = null;

	private OnUpCallback mCallback = null;

	public interface OnUpCallback {
		void onRectFinished(Rect rect);
	}

	public TextCursorOnImageView(final Context context) {
		super(context);
		init();
	}

	public TextCursorOnImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextCursorOnImageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * Sets callback for up
	 *
	 * @param callback {@link OnUpCallback}
	 */
	public void setOnUpCallback(OnUpCallback callback) {
		mCallback = callback;
	}

	/**
	 * Inits internal data
	 */
	private void init() {
		mRectPaint = new Paint();
		mRectPaint.setColor(getContext().getResources().getColor(android.R.color.holo_green_light));
		mRectPaint.setStyle(Paint.Style.STROKE);
		mRectPaint.setStrokeWidth(5); // TODO: should take from resources

		mTextPaint = new TextPaint();
		mTextPaint.setColor(getContext().getResources().getColor(android.R.color.holo_green_light));
		mTextPaint.setTextSize(20);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {

		// TODO: be aware of multi-touches
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDrawRect = false;
				mStartX = (int) event.getX();
				mStartY = (int) event.getY();
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				if (!mDrawRect || Math.abs(x - mEndX) > 5 || Math.abs(y - mEndY) > 5) {
					mEndX = x;
					mEndY = y;
					invalidate();
				}

				mDrawRect = true;
				break;

			case MotionEvent.ACTION_UP:
				if (mCallback != null) {
					mCallback.onRectFinished(new Rect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
							Math.max(mEndX, mStartX), Math.max(mEndY, mStartX)));
				}
				invalidate();
				break;

			default:
				break;
		}

		return true;
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);

		if (mDrawRect) {
			canvas.drawRect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
					Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mRectPaint);
			canvas.drawText("  (" + Math.abs(mStartX - mEndX) + ", " + Math.abs(mStartY - mEndY) + ")",
					Math.max(mEndX, mStartX), Math.max(mEndY, mStartY), mTextPaint);
		}
	}
}
