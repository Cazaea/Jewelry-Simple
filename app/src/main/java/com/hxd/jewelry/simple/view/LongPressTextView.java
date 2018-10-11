package com.hxd.jewelry.simple.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Cazaea on 2017/7/25.
 * 带有超长点击事件的 TextView
 */
public class LongPressTextView extends AppCompatTextView {

    // 上下文对象
    private Context mContext;
    // 最后位置
    private int mLastMotionX, mLastMotionY;
    // 是否移动了
    private boolean isMoved;
    // 是否释放了
    private boolean isReleased;
    // 计数器，防止多次点击导致最后一次形成longPress的时间变短
    private int mCounter;
    // 移动的阈值
    private static final int TOUCH_SLOP = 20;

    private Runnable mLongPressRunnable;
    private LongPressListener myLongPressListener;
    private Runnable mSingleClickRunnable;
    private SingleClickListener mySingleClickListener;

    public LongPressTextView(Context context) {
        this(context, null);
    }

    public LongPressTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongPressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        // 判断长按事件
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                mCounter--;
                // 计数器大于 0，说明当前执行的 Runnable 不是最后一次 down 产生的
                if (mCounter > 0 || isReleased || isMoved) return;
                if (myLongPressListener != null) {
                    myLongPressListener.onLongPress();
                }
            }
        };
        // 单击事件
        mSingleClickRunnable = new Runnable() {
            @Override
            public void run() {
                if (mySingleClickListener != null) {
                    mySingleClickListener.onSingleClick();
                }
            }
        };

    }

    /**
     * 设置自定义的长按监听
     */
    public void setLongPressListener(LongPressListener myLongPressListener) {
        this.myLongPressListener = myLongPressListener;
    }

    /**
     * 设置自定义的单击监听
     */
    public void setSingleClickListener(SingleClickListener mySingleClickListener) {
        this.mySingleClickListener = mySingleClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mCounter++;
                isReleased = false;
                isMoved = false;
                // 单击事件
                post(mSingleClickRunnable);
                // 长按时间
                postDelayed(mLongPressRunnable, 5000);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isMoved) break;
                if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                        || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                    // 移动超过阈值，则表示移动了
                    isMoved = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                // 释放了
                isReleased = true;
                break;
        }
        return true;
    }

    /**
     * 自定义长按接口
     */
    public interface LongPressListener {
        void onLongPress();
    }

    /**
     * 自定义单击接口
     */
    public interface SingleClickListener {
        void onSingleClick();
    }

}
