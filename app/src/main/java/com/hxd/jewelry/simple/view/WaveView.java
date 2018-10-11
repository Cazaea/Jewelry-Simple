package com.hxd.jewelry.simple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.hxd.jewelry.simple.R;

/**
 * 水流波动控件
 * <p>
 * 作 者： Cazaea
 * 日 期： 2018/5/24
 * 邮 箱： wistorm@sina.com
 */
public class WaveView extends View {

//    private int mViewWidth;
//    private int mViewHeight;
//
//    /**
//     * 水位线
//     */
//    private float mLevelLine;
//
//    /**
//     * 波浪起伏幅度
//     */
//    private float mWaveHeight = 30;
//    /**
//     * 波长
//     */
//    private float mWaveWidth = 150;
//    /**
//     * 被隐藏的最左边的波形
//     */
//    private float mLeftSide;
//
//    private float mMoveLen;
//    /**
//     * 水波平移速度
//     */
//    public static final float SPEED = 1.7f;
//
//    private List<Point> mPointsList;
//    private Paint mPaint;
//    private Paint mTextPaint;
//    private Path mWavePath;
//    private boolean isMeasured = false;
//
//    private Timer timer;
//    private MyTimerTask mTask;
//
//    @SuppressLint("HandlerLeak")
//    Handler updateHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            // 记录平移总位移
//            mMoveLen += SPEED;
//            // 水位上升
//            mLevelLine -= 0.1f;
//            if (mLevelLine < 0)
//                mLevelLine = 0;
//            mLeftSide += SPEED;
//            // 波形平移
//            for (int i = 0; i < mPointsList.size(); i++) {
//                mPointsList.get(i).setX(mPointsList.get(i).getX() + SPEED);
//                switch (i % 4) {
//                    case 0:
//                    case 2:
//                        mPointsList.get(i).setY(mLevelLine);
//                        break;
//                    case 1:
//                        mPointsList.get(i).setY(mLevelLine + mWaveHeight);
//                        break;
//                    case 3:
//                        mPointsList.get(i).setY(mLevelLine - mWaveHeight);
//                        break;
//                }
//            }
//            if (mMoveLen >= mWaveWidth) {
//                // 波形平移超过一个完整波形后复位
//                mMoveLen = 0;
//                resetPoints();
//            }
//            invalidate();
//        }
//
//    };
//
//    /**
//     * 所有点的x坐标都还原到初始状态，也就是一个周期前的状态
//     */
//    private void resetPoints() {
//        mLeftSide = -mWaveWidth;
//        for (int i = 0; i < mPointsList.size(); i++) {
//            mPointsList.get(i).setX(i * mWaveWidth / 4 - mWaveWidth);
//        }
//    }
//
//    public WaveView(Context context) {
//        super(context);
//        init();
//    }
//
//    public WaveView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public WaveView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//
//    private void init() {
//        mPointsList = new ArrayList<Point>();
//        timer = new Timer();
//
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Style.FILL);
//        mPaint.setColor(getResources().getColor(R.color.colorHalfTransparent));
//
//        mTextPaint = new Paint();
//        mTextPaint.setColor(Color.WHITE);
//        mTextPaint.setTextAlign(Align.CENTER);
//        mTextPaint.setTextSize(30);
//
//        mWavePath = new Path();
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        super.onWindowFocusChanged(hasWindowFocus);
//        // 开始波动
//        start();
//    }
//
//    private void start() {
//        if (mTask != null) {
//            mTask.cancel();
//            mTask = null;
//        }
//        mTask = new MyTimerTask(updateHandler);
//        timer.schedule(mTask, 0, icon_home_10);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (!isMeasured) {
//            isMeasured = true;
//            mViewHeight = getMeasuredHeight();
//            mViewWidth = getMeasuredWidth();
//            // 水位线从最底下开始上升
//            mLevelLine = mViewHeight;
//            // 根据View宽度计算波形峰值
//            mWaveHeight = mViewWidth / 2.5f;
//            // 波长等于四倍View宽度也就是View中只能看到四分之一个波形，这样可以使起伏更明显
//            mWaveWidth = mViewWidth * 4;
//            // 左边隐藏的距离预留一个波形
//            mLeftSide = -mWaveWidth;
//            // 这里计算在可见的View宽度中能容纳几个波形，注意n上取整
//            int n = (int) Math.round(mViewWidth / mWaveWidth + 0.5);
//            // n个波形需要4n+1个点，但是我们要预留一个波形在左边隐藏区域，所以需要4n+5个点
//            for (int i = 0; i < (4 * n + 5); i++) {
//                // 从P0开始初始化到P4n+4，总共4n+5个点
//                float x = i * mWaveWidth / 4 - mWaveWidth;
//                float y = 0;
//                switch (i % 4) {
//                    case 0:
//                    case 2:
//                        // 零点位于水位线上
//                        y = mLevelLine;
//                        break;
//                    case 1:
//                        // 往下波动的控制点
//                        y = mLevelLine + mWaveHeight;
//                        break;
//                    case 3:
//                        // 往上波动的控制点
//                        y = mLevelLine - mWaveHeight;
//                        break;
//                }
//                mPointsList.add(new Point(x, y));
//            }
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        mWavePath.reset();
//        int i = 0;
//        mWavePath.moveTo(mPointsList.get(0).getX(), mPointsList.get(0).getY());
//        for (; i < mPointsList.size() - 2; i = i + 2) {
//            mWavePath.quadTo(mPointsList.get(i + 1).getX(),
//                    mPointsList.get(i + 1).getY(), mPointsList.get(i + 2)
//                            .getX(), mPointsList.get(i + 2).getY());
//        }
//        mWavePath.lineTo(mPointsList.get(i).getX(), mViewHeight);
//        mWavePath.lineTo(mLeftSide, mViewHeight);
//        mWavePath.close();
//
//        // mPaint的Style是FILL，会填充整个Path区域
//        canvas.drawPath(mWavePath, mPaint);
//        // 绘制百分比
//        canvas.drawText("" + ((int) ((1 - mLevelLine / mViewHeight) * 100))
//                + "%", mViewWidth / 2, mLevelLine + mWaveHeight
//                + (mViewHeight - mLevelLine - mWaveHeight) / 2, mTextPaint);
//    }
//
//    class MyTimerTask extends TimerTask {
//        Handler handler;
//
//        public MyTimerTask(Handler handler) {
//            this.handler = handler;
//        }
//
//        @Override
//        public void run() {
//            handler.sendMessage(handler.obtainMessage());
//        }
//
//    }
//
//    class Point {
//        private float x;
//        private float y;
//
//        public float getX() {
//            return x;
//        }
//
//        public void setX(float x) {
//            this.x = x;
//        }
//
//        public float getY() {
//            return y;
//        }
//
//        public void setY(float y) {
//            this.y = y;
//        }
//
//        public Point(float x, float y) {
//            this.x = x;
//            this.y = y;
//        }
//
//    }

    // 默认属性值
    private static final int DEFAULT_AMPLITUDE = 200;
    private static final int DEFAULT_PERIOD = 16;
    private static final float DEFAULT_SPEED = .1F;
    private static final float DEFAULT_QUADRANT = .33F;
    private static final float DEFAULT_FREQUENCY = 1F / 360F;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#64B5F6");
    private static final int DEFAULT_WAVE_BK_COLOR = Color.parseColor("#EEEEEE");

    @SuppressWarnings("FieldCanBeLocal")
    @ColorInt
    private int mWaveColor;
    @ColorInt
    private int mWaveBKColor;
    // 振幅
    private int mAmplitude;
    // 波浪位于View的位置
    private float mQuadrant;
    // 波浪的频率,这个值越大,波浪越密集
    private float mFrequency;

    // 速度
    private float mSpeed;
    private float mShift;

    private final Paint mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mWavePath = new Path();

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet set) {
        final TypedArray array = context.obtainStyledAttributes(set, R.styleable.WaveAnim);
        mSpeed = array.getFloat(R.styleable.WaveAnim_speed, DEFAULT_SPEED);
        mWaveColor = array.getColor(R.styleable.WaveAnim_waveColor, DEFAULT_WAVE_COLOR);
        mWaveBKColor = array.getColor(R.styleable.WaveAnim_waveBackgroundColor, DEFAULT_WAVE_BK_COLOR);
        mAmplitude = array.getInt(R.styleable.WaveAnim_amplitude, DEFAULT_AMPLITUDE);
        mQuadrant = array.getFloat(R.styleable.WaveAnim_quadrant, DEFAULT_QUADRANT);
        mFrequency = array.getFloat(R.styleable.WaveAnim_frequency, DEFAULT_FREQUENCY);
        array.recycle();

        mWavePaint.setStrokeWidth(2);
        mWavePaint.setColor(mWaveColor);

        // 开始波浪动画
        postDelayed(new WaveAnimation(), DEFAULT_PERIOD);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();

        final int waveHeight = (int) (getHeight() * mQuadrant);
        // 绘制背景
        canvas.drawColor(mWaveBKColor);
        mWavePath.moveTo(0, height);
        mWavePath.lineTo(0, waveHeight);
        for (int i = 1; i <= width; i++) {
            // 绘制正弦曲线 y = A Sin(ωt+ ρ) = A sin(2πft + ρ)
            final float y = (float) (waveHeight + mAmplitude * Math.sin(2 * Math.PI * i * mFrequency + mShift));
            mWavePath.lineTo(i, y);
        }
        // 将曲线闭合
        mWavePath.lineTo(width, height);
        canvas.drawPath(mWavePath, mWavePaint);
    }


    final class WaveAnimation implements Runnable {

        @Override
        public void run() {
            mWavePath.reset();
            mShift += mSpeed;
            invalidate();
            WaveView.this.postDelayed(this, DEFAULT_PERIOD);
        }
    }

}
