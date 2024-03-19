package com.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.AbsSeekBar;

import com.android.canboxsetting.R;


public class VerticalSeekBar extends AbsSeekBar {
	public static final int VSEEKBAR_STATU_NONE = 0;
	public static final int VSEEKBAR_SART_TOUCH = 1;
	public static final int VSEEKBAR_STOP_TOUCH = 2;

	public int status = 0;

	private Drawable mThumb;
	private int height;
	private int width;
	private int mThumbWidth;

	public interface OnSeekBarChangeListener {
		void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress,
				boolean fromUser);

		void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar);

		void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar);
	}

	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	public VerticalSeekBar(Context context) {
		this(context, null);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.seekBarStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		Drawable thumb = mThumb;
		Rect rt;
		boolean b;
		if (thumb != null) {

			rt = thumb.getBounds();
			b = thumb.getPadding(rt);
		}

		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.VerticalSeekBar);

		height = a.getInt(R.styleable.VerticalSeekBar_height_, 0);
		width = a.getInt(R.styleable.VerticalSeekBar_width_, 0);

		mThumbWidth = a.getInt(R.styleable.VerticalSeekBar_thumbWidth_, 0);

	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		mOnSeekBarChangeListener = l;
	}

	void onStartTrackingTouch() {
		status = VSEEKBAR_SART_TOUCH;
		mPreProgress = getProgress();
		if (mOnSeekBarChangeListener != null) {
			mOnSeekBarChangeListener.onStartTrackingTouch(this);
		}
	}

	void onStopTrackingTouch() {
		status = VSEEKBAR_STOP_TOUCH;
		if (mOnSeekBarChangeListener != null) {
			mOnSeekBarChangeListener.onStopTrackingTouch(this);
		}
	}

	void onProgressRefresh(float scale, boolean fromUser) { // why formUser
															// incorrect?
		// super.onProgressRefresh(scale, fromUser);
		// Drawable thumb = mThumb;
		// if (thumb != null) {
		// setThumbPos(getHeight(), thumb, scale, Integer.MIN_VALUE);
		// invalidate();
		// }
		// if (status == VSEEKBAR_SART_TOUCH) {
		// fromUser = true;
		// }
		if (mOnSeekBarChangeListener != null) {
			mOnSeekBarChangeListener.onProgressChanged(this, getProgress(),
					fromUser);
		}
	}

	private int mPreProgress = -1;

	void onProgressRefresh() {
		if (mOnSeekBarChangeListener != null) {
			if (getProgress() != mPreProgress) {
				mPreProgress = getProgress();
				mOnSeekBarChangeListener.onProgressChanged(this, getProgress(),
						true);
			}
		}
	}

	private void setThumbPos(int w, Drawable thumb, float scale, int gap) {
		if (w > 0 && thumb != null) {
			int available = w + getPaddingLeft() - getPaddingRight();
			int thumbWidth = thumb.getIntrinsicWidth();

			int thumbHeight = thumb.getIntrinsicHeight();
			available -= thumbWidth;
			// The extra space for the thumb to move on the track
			//available += getThumbOffset() * 2;
			int thumbPos = (int) (scale * available);
			int topBound, bottomBound;
			if (gap == Integer.MIN_VALUE) {
				Rect oldBounds = thumb.getBounds();
				topBound = 0;// oldBounds.top;
				bottomBound = mThumbWidth;// oldBounds.bottom;
			} else {
				topBound = gap;
				bottomBound = gap + thumbHeight;
			}
			thumb.setBounds(thumbPos, topBound, thumbPos + thumbWidth,
					bottomBound);
		}
	}

	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-height, 0);
		super.onDraw(c);
	}

	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		// width = 32;
		// height = 290;
		// height = View.MeasureSpec.getSize(heightMeasureSpec);
		// width = View.MeasureSpec.getSize(widthMeasureSpec);
		this.setMeasuredDimension(width, height);
	}

	@Override
	public void setThumb(Drawable thumb) {
		mThumb = thumb;
		super.setThumb(thumb);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldw, oldh);

		if (mThumb != null) {
			int max = getMax();
			float scale = max > 0 ? (float) getProgress() / (float) max : 0;
			setThumbPos(h, mThumb, scale, Integer.MIN_VALUE);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setPressed(true);
			onStartTrackingTouch();
			trackTouchEvent(event);
			break;

		case MotionEvent.ACTION_MOVE:
			trackTouchEvent(event);
			attemptClaimDrag();
			onProgressRefresh();
			break;

		case MotionEvent.ACTION_UP:
			trackTouchEvent(event);
			onStopTrackingTouch();
			setPressed(false);
			break;

		case MotionEvent.ACTION_CANCEL:
			onStopTrackingTouch();
			setPressed(false);
			break;
		}
		return true;
	}

	private void trackTouchEvent(MotionEvent event) {
		final int Height = getHeight();
		final int available = Height - getPaddingBottom() - getPaddingTop();
		int Y = (int) event.getY();
		float scale;
		float progress = 0;
		if (Y > Height - getPaddingBottom()) {
			scale = 0.0f;
		} else if (Y < getPaddingTop()) {
			scale = 1.0f;
		} else {
			scale = (float) (Height - getPaddingBottom() - Y)
					/ (float) available;
		}

		final int max = getMax();
		progress = scale * max;

		setProgress((int) progress);
	}

	// �������setProgress��������ʱ���鲻�����bug
	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		onSizeChanged(getWidth(), getHeight(), 0, 0);

	}

	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			KeyEvent newEvent = null;
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DPAD_UP);
				break;
			default:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						event.getKeyCode());
				break;
			}
			return newEvent.dispatch(this);
		}
		return false;
	}
}
