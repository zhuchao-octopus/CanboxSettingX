package com.common.view;

import com.canboxsetting.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CircularRingPercentageView extends View {
    private Paint paint;
    private Paint paintRound;
    private int circleWidth;
    private int roundBackgroundColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private float progress = 0;
    private int mValue = 0;
    private int[] colors = {0xffff4639, 0xffCDD513, 0xff3CDF5F};
    private int radius;
    private RectF oval;
    private RectF ovalRound;
    private Paint mPaintText;
    private Paint mPaintTextValue;
    private int maxColorNumber = 100;
    private float singlPoint = 9;
    private float lineWidth = 1f;
    private int circleCenter;
    private SweepGradient sweepGradient;
    private boolean isLine = false;


    /**
     * 分割的数量
     *
     * @param maxColorNumber 数量
     */
    public void setMaxColorNumber(int maxColorNumber) {
        this.maxColorNumber = maxColorNumber;
        singlPoint = (float) 360 / (float) maxColorNumber;
        invalidate();
    }

    /**
     * 是否是线条
     *
     * @param line true 是 false否
     */
    public void setLine(boolean line) {
        isLine = line;
        invalidate();
    }

    public int getCircleWidth() {
        return circleWidth;
    }

    public CircularRingPercentageView(Context context) {
        this(context, null);
    }

    public CircularRingPercentageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularRingPercentageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DashCircle);
        circleWidth = mTypedArray.getDimensionPixelOffset(R.styleable.DashCircle_circleRadiu, getDpValue(180));
        roundBackgroundColor = mTypedArray.getColor(R.styleable.DashCircle_firstColor, 0xffdddddd);
        textColor = 0xffffffff;//mTypedArray.getColor(R.styleable.DashCircle_circleTextColor, 0xff999999);
        roundWidth = mTypedArray.getInt(R.styleable.DashCircle_dashWidth, 40);
        textSize = 18;//mTypedArray.getDimension(R.styleable.DashCircle_circleTextSize, getDpValue(8));
        colors[0] = mTypedArray.getColor(R.styleable.DashCircle_outCircleColor, 0xffff4639);
        colors[1] = mTypedArray.getColor(R.styleable.DashCircle_firstColor, 0xffcdd513);
        colors[2] = mTypedArray.getColor(R.styleable.DashCircle_secondColor, 0xff3cdf5f);
        
		top_arc_degree_start = mTypedArray.getFloat(
				R.styleable.DashCircle_top_arc_degree_start, 0);
		top_arc_degree_end = mTypedArray.getFloat(
				R.styleable.DashCircle_top_arc_degree_end, 360);
		top_arc_value_max = mTypedArray.getInt(
				R.styleable.DashCircle_top_arc_value_max, 100);
		top_arc_value_min = mTypedArray.getInt(
				R.styleable.DashCircle_top_arc_value_min, 0);
		maxColorNumber = mTypedArray.getInt(
				R.styleable.DashCircle_top_arc_count, 40);
        
		
		top_arc_value_count = mTypedArray.getInt(
				R.styleable.DashCircle_top_arc_value_count, 7);
		
		top_arc_degree =  (float)((360.0 - top_arc_degree_start) + top_arc_degree_end);        
		singlPoint = (float) (top_arc_degree/maxColorNumber);	//(float) 360 / (float) maxColorNumber;		
		mStep = (float) (top_arc_degree/100);		
        
        
        initView();
        mTypedArray.recycle();
    }


    /**
     * 空白出颜色背景
     *
     * @param roundBackgroundColor
     */
    public void setRoundBackgroundColor(int roundBackgroundColor) {
        this.roundBackgroundColor = roundBackgroundColor;
        paint.setColor(roundBackgroundColor);
        invalidate();
    }

    /**
     * 刻度字体颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaintText.setColor(textColor);
        invalidate();
    }

    /**
     * 刻度字体大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mPaintText.setTextSize(textSize);
        invalidate();
    }

    /**
     * 渐变颜色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("colors length < 2");
        }
        this.colors = colors;
        sweepGradientInit();
        invalidate();
    }


    /**
     * 间隔角度大小
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }


    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 圆环宽度
     *
     * @param roundWidth 宽度
     */
    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
        if (roundWidth > circleCenter) {
            this.roundWidth = circleCenter;
        }
        radius = (int) (circleCenter - this.roundWidth / 2); // 圆环的半径
        oval.left = circleCenter - radius;
        oval.right = circleCenter + radius;
        oval.bottom = circleCenter + radius;
        oval.top = circleCenter - radius;
        paint.setStrokeWidth(this.roundWidth);
        invalidate();
    }

    /**
     * 圆环的直径
     *
     * @param circleWidth 直径
     */
    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        circleCenter = circleWidth / 2;

        if (roundWidth > circleCenter) {
            roundWidth = circleCenter;
        }
        setRoundWidth(roundWidth);
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    /**
     * 渐变初始化
     */
    public void sweepGradientInit() {
        //渐变颜色
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    public void initView() {

        int roundLineWidth = 2;
        circleCenter = circleWidth / 2;//半径
       // singlPoint = (float) 360 / (float) maxColorNumber;
        radius = (int) (circleCenter - roundWidth / 2); // 圆环的半径
        sweepGradientInit();
        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        mPaintTextValue = new Paint();
        mPaintTextValue.setColor(textColor);
        mPaintTextValue.setTextAlign(Paint.Align.CENTER);
        mPaintTextValue.setTextSize(60);
        mPaintTextValue.setAntiAlias(true);
        
		paint = new Paint();
		paint.setColor(roundBackgroundColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(roundWidth - roundLineWidth * 4);
		paint.setAntiAlias(true);

        paintRound = new Paint();
        paintRound.setColor(0xffffffff);
        paintRound.setStyle(Paint.Style.STROKE);
        paintRound.setStrokeWidth(roundLineWidth);
        paintRound.setAntiAlias(true);
        // 用于定义的圆弧的形状和大小的界限
		oval = new RectF(circleCenter - radius, circleCenter - radius,
				circleCenter + radius, circleCenter + radius);
		ovalRound = new RectF(roundLineWidth, roundLineWidth,
				circleWidth-roundLineWidth, circleWidth-roundLineWidth);

    }

    private float top_arc_degree_start;
    private float top_arc_degree_end;
    private int top_arc_value_max;
    private int top_arc_value_min;
    private float mStep = 360/100;;
    private float top_arc_degree;;
    private float top_arc_value_count;
	
	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


      canvas.drawArc(ovalRound, top_arc_degree_start, top_arc_degree, false, paintRound);
        
        paint.setColor(colors[2]);
        //背景渐变颜色
//        paint.setShader(sweepGradient);
        float sweepAngle = progress * mStep;
        canvas.drawArc(oval, top_arc_degree_start, sweepAngle, false, paint);
        paint.setShader(null);

        //绘制剩下的空白区域
        paint.setColor(colors[1]);
		canvas.drawArc(
				oval,
				(float) (top_arc_degree_start + sweepAngle),
				(float) (top_arc_degree - sweepAngle),
				false, paint);
		
        paint.setColor(0xff000000);
        
        //是否是线条模式
//        if (!isLine) {
            float start = top_arc_degree_start;
            for (int i = 0; i < maxColorNumber; i++) {
                canvas.drawArc(oval, start + singlPoint - lineWidth, lineWidth, false, paint); // 绘制间隔快
                start = (start + singlPoint);
            }
//        }
//   
		int num = 7;
		if (top_arc_value_max>100){ //....
			num = 5;
		}
            
		int step = (int) ((top_arc_value_max - top_arc_value_min) / (top_arc_value_count - 1));
		// 绘制文字刻度

		canvas.drawText(mValue + "", circleCenter,
				circleWidth/2 + 20, mPaintTextValue);
		
		for (int i = 0; i < top_arc_value_count; i++) {
			canvas.save();// 保存当前画布
			canvas.rotate(90 + top_arc_degree_start
					+ (top_arc_degree / (top_arc_value_count - 1)) * i, circleCenter,
					circleCenter);
			canvas.drawText(top_arc_value_min + (step * i) + "", circleCenter,
					circleCenter - radius + roundWidth / 2 + getDpValue(4)
							+ textSize, mPaintText);
			canvas.restore();//
		}
    }


    OnProgressScore onProgressScore;

    public interface OnProgressScore {
        void setProgressScore(float score);

    }

    public synchronized void setProgress(final float p) {
        progress = p;
        postInvalidate();
    }
    
    public void setValue(int v) {
    	
    	mValue = v/100;
    	int len = (top_arc_value_max - top_arc_value_min);
    	len = (v - top_arc_value_min*100)/len;   
    	
    	if (len < 0 || len > 100)
			return;
    	progress = len;
    	
        postInvalidate();
    }

    /**
     * @param p
     */
    public synchronized void setProgress(final float p, OnProgressScore onProgressScore) {
        this.onProgressScore = onProgressScore;
        progress = p;
        postInvalidate();
    }

}