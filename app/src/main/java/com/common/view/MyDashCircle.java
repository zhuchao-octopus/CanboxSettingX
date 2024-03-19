//package com.common.view;
//
//import com.canboxsetting.R;
//
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.os.Handler;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.animation.AccelerateInterpolator;
//
//
///**
// * Created by Yizhui on 2016/3/2.
// */
//public class MyDashCircle extends View {
//
//	private float circleRadiu;// 内圆半径长度
//	private float dashWidth; // 刻度长度
//	private int firstColor; // 当前进度颜色
//	private int secondColor; // 刻度颜色
//	private int outCircleColor; // 外圆圈颜色
//	private int outCircleWidth; // 外圆圈宽度
//	private int outCircleSpace; // 外圆圈与刻度之间间隔
//
//
//	private int mTopCount; // 外圆圈与刻度之间间隔
//	
//	private ValueAnimator mValueAnimator;
//
//	private int totalDuration = 60;
//	private int currentDuration = 20;
//	private int dashDegree = 2;
//
//	private Handler mHandler;
//
//	private Paint mPaint;
//
//	public MyDashCircle(Context context) {
//		this(context, null);
//	}
//
//	public MyDashCircle(Context context, AttributeSet attrs) {
//		this(context, attrs, 0);
//	}
//
//	private int mCurrentIndex = 120;
//
//	public void setProgress(int index) {
//		mCurrentIndex = index;
//	}
//	public MyDashCircle(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//
//		// 获取自定义属性
//		TypedArray arr = context.getTheme().obtainStyledAttributes(attrs,
//				R.styleable.DashCircle, defStyle, 0);
//		int n = arr.getIndexCount();
//		for (int i = 0; i < n; i++) {
//			int attr = arr.getIndex(i);
//			switch (attr) {
//			case R.styleable.DashCircle_circleRadiu:
//				circleRadiu = arr.getDimensionPixelSize(attr, (int) TypedValue
//						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
//								context.getResources().getDisplayMetrics()));
//				break;
//			case R.styleable.DashCircle_dashWidth:
//				dashWidth = arr.getDimensionPixelSize(attr, (int) TypedValue
//						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
//								context.getResources().getDisplayMetrics()));
//				break;
//			case R.styleable.DashCircle_firstColor:
//				firstColor = arr.getColor(attr, Color.WHITE);
//				break;
//			case R.styleable.DashCircle_secondColor:
//				secondColor = arr.getColor(attr, Color.BLUE);
//				break;
//			case R.styleable.DashCircle_outCircleColor:
//				outCircleColor = arr.getColor(attr, Color.YELLOW);
//				break;
//			case R.styleable.DashCircle_top_arc_count:
//				mTopCount = arr.getInt(attr, 10);
//				dashDegree = 360/mTopCount;
//				break;
//			}
//		}
//		arr.recycle(); // TypedArray 使用后需要释放
//
//		outCircleWidth = (int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources()
//						.getDisplayMetrics());
//		outCircleSpace = (int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources()
//						.getDisplayMetrics());
//
//		initPaint();
////		initAnimator();
////		animateValue();
//		// mHandler=new Handler(){
//		// @Override
//		// public void handleMessage(Message msg) {
//		// currentDuration++;
//		// invalidate();
//		// if(currentDuration<totalDuration){
//		// mHandler.sendEmptyMessageDelayed(0x11,1000);
//		// }
//		// }
//		// };
//		//
//		// mHandler.sendEmptyMessageDelayed(0x11,1000);
//	}
//
//	private void initPaint() {
//		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeCap(Paint.Cap.ROUND);
//		mPaint.setStrokeJoin(Paint.Join.ROUND);
//	}
//
//	private void initAnimator() {
//		mValueAnimator = new ValueAnimator();
//		mValueAnimator.setInterpolator(new AccelerateInterpolator());
//		mValueAnimator
//				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//					@Override
//					public void onAnimationUpdate(ValueAnimator animation) {
//						currentDuration = (Integer) animation.getAnimatedValue();
//						invalidate();
//					}
//				});
//	}
//
//	private void animateValue() {
//		if (mValueAnimator != null) {
//			mValueAnimator.setDuration(60 * 1000);
//			mValueAnimator.setIntValues(0, totalDuration);
//			mValueAnimator.start();
//		}
//	}
//
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//		int width;
//		int height;
//		if (widthMode == MeasureSpec.EXACTLY) {
//			width = widthSize;
//			int maxCircleRadiu = (int) (width - getPaddingLeft()
//					- getPaddingRight() - 2 * (outCircleWidth + outCircleSpace + dashWidth)) / 2;
//			if (circleRadiu > maxCircleRadiu) {
//				circleRadiu = maxCircleRadiu;
//			}
//		} else {
//			width = (int) (getPaddingLeft()
//					+ getPaddingRight()
//					+ 2
//					* (circleRadiu + dashWidth + outCircleSpace + outCircleWidth) + 0.5f);
//		}
//
//		if (heightMode == MeasureSpec.EXACTLY) {
//			height = heightSize;
//			int maxCircleRadiu = (int) (height - getPaddingTop()
//					- getPaddingBottom() - 2 * (outCircleWidth + outCircleSpace + dashWidth)) / 2;
//			if (circleRadiu > maxCircleRadiu) {
//				circleRadiu = maxCircleRadiu;
//			}
//		} else {
//			height = (int) (getPaddingTop()
//					+ getPaddingBottom()
//					+ 2
//					* (circleRadiu + dashWidth + outCircleSpace + outCircleWidth) + 0.5f);
//		}
//
//		setMeasuredDimension(width, height);
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
////		canvas.drawColor(Color.GRAY);
//		canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
//		mPaint.setColor(outCircleColor);
//		mPaint.setStrokeWidth(outCircleWidth);
////
////		// 外圆
////		canvas.drawCircle(0, 0, circleRadiu + dashWidth + outCircleSpace,
////				mPaint);
//
//
//		RectF oval = new RectF( -getWidth()/2, -getHeight()/2,
//
//		getWidth()/2 , getHeight()/2 );
//		mPaint.setStrokeWidth(1);
//		mPaint.setStyle(Paint.Style.FILL);
//		canvas.drawArc(oval, 0,270, true, mPaint);
////
////		// 当前进度刻度
//		mPaint.setStrokeWidth(3);
//		mPaint.setColor(secondColor);
////		int currentIndex = (int) (currentDuration * 360
////				/ (totalDuration * dashDegree) + 0.5);
////		for (int i = 1; i <= mCurrentIndex; i++) {
////			canvas.drawLine(0, -circleRadiu, 0, -(circleRadiu + dashWidth),
////					mPaint);
////			canvas.rotate(dashDegree);
////		}
//
//		// 余下进度刻度
////		mPaint.setColor(firstColor);
////		for (int i = mCurrentIndex + 1; i <= 360 / dashDegree; i++) {
////			canvas.drawLine(0, -circleRadiu, 0, -(circleRadiu + dashWidth),
////					mPaint);
////			canvas.rotate(dashDegree);
////		}
//	}
//}
