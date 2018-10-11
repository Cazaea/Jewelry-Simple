package com.hxd.jewelry.simple.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * 作 者： Cazaea
 * 日 期： 2018/6/6
 * 邮 箱： wistorm@sina.com
 */
public class SelfRefreshView extends SmartRefreshLayout {

    private int mLastXIntercept;
    private int mLastYIntercept;

    public SelfRefreshView(Context context) {
        super(context);
    }

    public SelfRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public SelfRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean intercepted = false;
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                intercepted = false;
//                // 调用ViewPager的onInterceptTouchEvent方法初始化mActivePointerId
//                super.onInterceptTouchEvent(ev);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                // 横坐标位移增量
//                int deltaX = x - mLastXIntercept;
//                // 纵坐标位移增量
//                int deltaY = y - mLastYIntercept;
//                intercepted = Math.abs(deltaX) > Math.abs(deltaY);
//                break;
//            case MotionEvent.ACTION_UP:
//                intercepted = false;
//                break;
//            default:
//                break;
//        }
//        mLastXIntercept = x;
//        mLastYIntercept = y;
//        return intercepted;
        return false;
    }
}
