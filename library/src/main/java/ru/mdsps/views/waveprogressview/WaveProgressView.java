package ru.mdsps.views.waveprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Demin.M on 01.11.2016
 * Licensed by Apache 2.0
 */

public class WaveProgressView extends View {

    public static final int VIEW_TYPE_RECTANGLE = 1;
    public static final int VIEW_TYPE_OVAL = 2;
    public static final int VIEW_TYPE_SQUARE = 3;
    public static final int VIEW_TYPE_CIRCLE = 4;

    private final static int FIRST_WAVE_ALPHA = 180;
    private final static int SECOND_WAVE_ALPHA = 90;
    private final static int THIRD_WAVE_ALPHA = 70;
    private final static int WAVE_HEIGHT_LARGE = 24;
    private final static int WAVE_HEIGHT_MIDDLE = 16;
    private final static int WAVE_HEIGHT_LITTLE = 8;

    private final static float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final static float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final static float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final static float X_SPACE = 20;
    private final static double PI2 = 2 * Math.PI;

    private float topLeftCornerRadius = 0f;
    private float topRightCornerRadius = 0f;
    private float bottomLeftCornerRadius = 0f;
    private float bottomRightCornerRadius = 0f;
    private float allCornerRadius = 0f;
    private float borderWidth = 0f;
    private float progress = 50f;
    private float mRealProgress = 100 - progress;

    private int mWaveMultipleMode;
    private int mWaveHeightMode;

    private int viewType = 0;
    private int backgroundColor = Color.TRANSPARENT;
    private int firstWaveColor = Color.BLUE;
    private int borderColor = Color.BLACK;

    private Path mFirstWave = new Path();
    private Path mSecondWave = new Path();
    private Path mThirdWave = new Path();
    private Path mBorder = new Path();

    private Paint mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFirstWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSecondWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mThirdWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF mBounds = new RectF();
    private RectF mBorderBounds = new RectF();

    private float[] mRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private float[] mBorderRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    private float mFirstWaveOffset = 0.0f;
    private float mSecondWaveOffset;
    private float mThirdWaveOffset;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public float getAllCornerRadius() {
        return px2dp(allCornerRadius);
    }

    public void setAllCornerRadius(float allCornerRadius) {
        this.allCornerRadius = dp2px(allCornerRadius);
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public float getBottomLeftCornerRadius() {
        return px2dp(bottomLeftCornerRadius);
    }

    public void setBottomLeftCornerRadius(float bottomLeftCornerRadius) {
        this.bottomLeftCornerRadius = dp2px(bottomLeftCornerRadius);
        invalidate();
    }

    public float getBottomRightCornerRadius() {
        return px2dp(bottomRightCornerRadius);
    }

    public void setBottomRightCornerRadius(float bottomRightCornerRadius) {
        this.bottomRightCornerRadius = dp2px(bottomRightCornerRadius);
        invalidate();
    }

    public int getWaveColor() {
        return firstWaveColor;
    }

    public void setWaveColor(int firstWaveColor) {
        this.firstWaveColor = firstWaveColor;
        invalidate();
    }

    public int getWaveHeightMode() {
        return mWaveHeightMode;
    }

    public void setWaveHeightMode(int mWaveHeightMode) {
        this.mWaveHeightMode = mWaveHeightMode;
        invalidate();
    }

    public int getWaveMultipleMode() {
        return mWaveMultipleMode;
    }

    public void setWaveMultipleMode(int mWaveMultipleMode) {
        this.mWaveMultipleMode = mWaveMultipleMode;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getTopLeftCornerRadius() {
        return px2dp(topLeftCornerRadius);
    }

    public void setTopLeftCornerRadius(float topLeftCornerRadius) {
        this.topLeftCornerRadius = dp2px(topLeftCornerRadius);
        invalidate();
    }

    public float getTopRightCornerRadius() {
        return px2dp(topRightCornerRadius);
    }

    public void setTopRightCornerRadius(float topRightCornerRadius) {
        this.topRightCornerRadius = dp2px(topRightCornerRadius);
        invalidate();
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        setVisibility(View.GONE);
        this.viewType = viewType;
        invalidate();
        setVisibility(View.VISIBLE);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.WaveProgressView, defStyleAttr, 0);
        if(a != null){
            viewType = a.getInt(R.styleable.WaveProgressView_wpv_type, VIEW_TYPE_RECTANGLE);
            topLeftCornerRadius = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_top_left_corner_radius, 0);
            topRightCornerRadius = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_top_right_corner_radius, 0);
            bottomLeftCornerRadius = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_bottom_left_corner_radius, 0);
            bottomRightCornerRadius = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_bottom_right_corner_radius, 0);
            allCornerRadius = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_corner_radius, 0);
            firstWaveColor = a.getColor(R.styleable.WaveProgressView_wpv_wave_color, Color.BLUE);
            backgroundColor = a.getColor(R.styleable.WaveProgressView_wpv_background_color, Color.TRANSPARENT);
            borderColor = a.getColor(R.styleable.WaveProgressView_wpv_border_color, Color.BLACK);
            borderWidth = a.getDimensionPixelSize(
                    R.styleable.WaveProgressView_wpv_border_width, 0);
            progress = a.getFloat(R.styleable.WaveProgressView_wpv_progress, 50f);
            mWaveMultipleMode = a.getInt(R.styleable.WaveProgressView_wpv_wave_length, 1);
            mWaveHeightMode = a.getInt(R.styleable.WaveProgressView_wpv_wave_height, 1);

            a.recycle();
        }

        if(allCornerRadius > 0){
            topLeftCornerRadius = allCornerRadius;
            topRightCornerRadius = allCornerRadius;
            bottomLeftCornerRadius = allCornerRadius;
            bottomRightCornerRadius = allCornerRadius;
        }

        mBorderRadii = new float[] {
            topLeftCornerRadius, topLeftCornerRadius,
            topRightCornerRadius, topRightCornerRadius,
            bottomRightCornerRadius, bottomRightCornerRadius,
            bottomLeftCornerRadius, bottomLeftCornerRadius
        };

        mRealProgress = 100 - progress;
        mSecondWaveOffset = getWaveHeight() * 0.4f;
        mThirdWaveOffset = getWaveHeight() * 0.6f;

        calcRadii();

        invalidate();
    }

    private void calcRadii(){
        float ltCr, rtCr, rbCr, lbCr;
        ltCr = topLeftCornerRadius - borderWidth;
        rtCr = topRightCornerRadius - borderWidth;
        rbCr = bottomRightCornerRadius - borderWidth;
        lbCr = bottomLeftCornerRadius - borderWidth;
        mRadii = new float[]{
                ltCr, ltCr,
                rtCr, rtCr,
                rbCr, rbCr,
                lbCr, lbCr
        };

    }

    private void calculatePath(){

        mBorderRadii = new float[] {
                topLeftCornerRadius, topLeftCornerRadius,
                topRightCornerRadius, topRightCornerRadius,
                bottomRightCornerRadius, bottomRightCornerRadius,
                bottomLeftCornerRadius, bottomLeftCornerRadius
        };

        calcRadii();

        float left, top, right, bottom;

        if (borderWidth > 0) {
            left = (borderWidth / 2) + getPaddingLeft();
            top = (borderWidth / 2) + getPaddingTop();
            right = getMeasuredWidth() - ((borderWidth / 2) + getPaddingRight());
            bottom = getMeasuredHeight() - ((borderWidth / 2) + getPaddingBottom());
            mBorderBounds.set(left, top, right, bottom);
            left = (int) (left + (borderWidth / 2));
            top = (int) (top + (borderWidth / 2));
            right = (int) (right - (borderWidth / 2));
            bottom = (int) (bottom - (borderWidth / 2));
            mBounds.set(left, top, right, bottom);
        } else {
            left = 0;
            top = 0;
            right = getMeasuredWidth();
            bottom = getMeasuredHeight();
            mBounds.set(left, top, right, bottom);
        }
    }

    private float getWaveMultiple() {
        switch (mWaveMultipleMode) {
            case 1:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case 2:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case 3:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight() {
        switch (mWaveHeightMode) {
            case 1:
                return WAVE_HEIGHT_LARGE;
            case 2:
                return WAVE_HEIGHT_MIDDLE;
            case 3:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private void getWaveOffset() {
        float mWaveHz = 0.13f;
        if (mFirstWaveOffset > Float.MAX_VALUE - 100) {
            mFirstWaveOffset = 0;
        } else {
            mFirstWaveOffset += mWaveHz;
        }

        if (mSecondWaveOffset > Float.MAX_VALUE - 100) {
            mSecondWaveOffset = 0;
        } else {
            mSecondWaveOffset += mWaveHz;
        }

        if (mThirdWaveOffset > Float.MAX_VALUE - 100) {
            mThirdWaveOffset = 0;
        } else {
            mThirdWaveOffset += mWaveHz;
        }
    }

    private Bitmap generateWaveBitmap(){
        Bitmap bitmap = Bitmap.createBitmap((int) mBounds.width(), (int) mBounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        mFirstWave.reset();
        mSecondWave.reset();
        mThirdWave.reset();

        float width = mBounds.width();
        float height = mBounds.height();
        float realHeight = height / 100 * mRealProgress;
        float y;
        float x = 0;
        float mMaxRight = getRight() + X_SPACE;

        float mWaveLength = width * getWaveMultiple();
        double omega = PI2 / mWaveLength;

        getWaveOffset();

        // Основная волна
        mFirstWave.moveTo(0, height);
        mFirstWave.lineTo(0, realHeight);
        for (float xf = 0; xf <= mMaxRight; xf += X_SPACE) {
            y = (float) (getWaveHeight() * Math.sin(omega * xf + mFirstWaveOffset) + getWaveHeight());
            mFirstWave.lineTo(xf, realHeight + y);
            x = xf;
        }
        mFirstWave.lineTo(x, height);
        mFirstWave.lineTo(0, height);

        // Вторая волна
        mSecondWave.moveTo(0, height);
        mSecondWave.lineTo(0, realHeight);
        for (float xf = 0; xf <= mMaxRight; xf += X_SPACE) {
            y = (float) (getWaveHeight() * Math.sin(omega * xf + mSecondWaveOffset) + getWaveHeight());
            mSecondWave.lineTo(xf, realHeight + y);
            x = xf;
        }
        mSecondWave.lineTo(x, height);
        mSecondWave.lineTo(0, height);

        // Третья волна
        mThirdWave.moveTo(0, height);
        mThirdWave.lineTo(0, realHeight);
        for (float xf = 0; xf <= mMaxRight; xf += X_SPACE) {
            y = (float) (getWaveHeight() * Math.sin(omega * xf + mThirdWaveOffset) + getWaveHeight());
            mThirdWave.lineTo(xf, realHeight + y);
            x = xf;
        }
        mThirdWave.lineTo(x, height);
        mThirdWave.lineTo(0, height);

        // Отрисовка третьей волны
        mThirdWavePaint.setColor(firstWaveColor);
        mThirdWavePaint.setAlpha(THIRD_WAVE_ALPHA);
        canvas.drawPath(mThirdWave, mThirdWavePaint);

        // Отрисовка второй волны
        mSecondWavePaint.setColor(firstWaveColor);
        mSecondWavePaint.setAlpha(SECOND_WAVE_ALPHA);
        canvas.drawPath(mSecondWave, mSecondWavePaint);

        // Отрисовка основной волны
        mFirstWavePaint.setColor(firstWaveColor);
        mFirstWavePaint.setAlpha(FIRST_WAVE_ALPHA);
        canvas.drawPath(mFirstWave, mFirstWavePaint);

        return bitmap;
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (WaveProgressView.this) {
                long start = System.currentTimeMillis();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

    private int px2dp(float px) {
        final float scale = getResources().getDisplayMetrics().density;
        return  (int)(px / scale);
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculatePath();

        // Wave
        mBorder.reset();
        Bitmap wave = generateWaveBitmap();
        BitmapShader mShader = new BitmapShader(wave, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setShader(mShader);
        if(viewType == VIEW_TYPE_RECTANGLE || viewType == VIEW_TYPE_SQUARE) {
            mBorder.addRoundRect(mBounds, mRadii, Path.Direction.CW);
        } else {
            mBorder.addOval(mBounds, Path.Direction.CW);
        }
        canvas.drawPath(mBorder, mWavePaint);

        // Border
        if(borderWidth > 0) {
            mBorder.reset();
            if(viewType == VIEW_TYPE_RECTANGLE || viewType == VIEW_TYPE_SQUARE) {
                mBorder.addRoundRect(mBorderBounds, mBorderRadii, Path.Direction.CW);
            } else {
                mBorder.addOval(mBorderBounds, Path.Direction.CW);
            }
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderWidth);
            canvas.drawPath(mBorder, mBorderPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(viewType == VIEW_TYPE_CIRCLE || viewType == VIEW_TYPE_SQUARE) {
            int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(size, size);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }
}
