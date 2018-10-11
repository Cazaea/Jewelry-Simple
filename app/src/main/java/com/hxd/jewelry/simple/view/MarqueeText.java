package com.hxd.jewelry.simple.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxd.jewelry.simple.config.AppConfig;
import com.thejoyrun.router.Router;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lister on 2017-icon_home_06-17.
 * 滚动横幅工具类
 */

public class MarqueeText {

    private Context mContext;

    private LinearLayout mLinearLayout;
    private HorizontalScrollView mScrollView;
    private ArrayList<String> mData = new ArrayList<>();
    private ArrayList<String> mUrl = new ArrayList<>();
    private MyHandler rollHandler;
    /**
     * 使用 Timer 调度 TimerTask
     */
    private Timer mTimer;
    private TextTimerTask mTextTimerTask;
    private static final int SPEED = 30;
    private int moveSum = 0;
    private int lineWidth = 0;
    private int lineHeight = 0;
    private int moveEnd = 0;

    @SuppressLint("ClickableViewAccessibility")
    public MarqueeText(Context context, LinearLayout linearLayout, HorizontalScrollView scrollView) {
        this.mContext = context;
        this.mLinearLayout = linearLayout;
        this.mScrollView = scrollView;
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * 开始滚动
     */
    public void showMarqueeText(JSONArray marqueeArray) {
        // 初始化数据列表
        mData.clear();
        mUrl.clear();
        for (int i = 0; i < marqueeArray.length(); i++) {
            try {
                JSONObject jsonObject = marqueeArray.getJSONObject(i);
                mData.add(jsonObject.isNull("title") ? "濠寓" : jsonObject.getString("title"));
                mUrl.add(jsonObject.isNull("link_url") ? "" : jsonObject.getString("link_url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 加载到 TextView 中
        setView(mLinearLayout);
        // 开始滚动
        if (rollHandler == null) {
            rollHandler = new MyHandler();
        }
        mLinearLayout.measure(mLinearLayout.getMeasuredWidth(), mLinearLayout.getMeasuredHeight());
        lineWidth = mLinearLayout.getMeasuredWidth();
        lineHeight = mLinearLayout.getMeasuredHeight();
        moveEnd = -(lineWidth / 2);
        stopTimer();
        runRoll();
    }

    /**
     * 将数据列表中的字符串添加到 LinearLayout 中
     */
    private void setView(LinearLayout line) {
        line.removeAllViews();
        int listSize = mData.size();
        for (int i = 0; i < listSize * 4; i++) {
            TextView textView = new TextView(mContext);
            // 添加多次字符串，保证效果
            final int poi = i % listSize;
            if (poi >= listSize) {
                return;
            }
            final String title = mData.get(poi);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(80, 0, 0, 0);
            textView.setId(i);
            textView.setTextSize(14);
            textView.setTextColor(Color.GRAY);
            textView.setText(title);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(mUrl.get(poi)))
                        Router.startActivity(mContext, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + mUrl.get(poi).replace("&", "%26"));
                }
            });
            line.addView(textView, i, params);
        }
    }

    /**
     * 通过 Timer 来调度横幅滚动
     */
    private void runRoll() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTextTimerTask == null) {
            mTextTimerTask = new TextTimerTask();
            if (rollHandler != null) {
                // 每隔 SPEED 执行一次
                mTimer.schedule(mTextTimerTask, 0, SPEED);
            }
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTextTimerTask != null) {
            mTextTimerTask.cancel();
            mTextTimerTask = null;
        }
    }

    private class TextTimerTask extends TimerTask {

        @Override
        public void run() {
            int moveSpeed = 4;
            moveSum -= moveSpeed;
            if (moveSum < moveEnd) {
                moveSum = 0;
            } else {
                rollHandler.sendEmptyMessage(1);
            }
        }
    }

    /**
     * 因为 UI 操作只能在主线程
     * 所以使用 Handler 操作 LinearLayout
     */
    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            mLinearLayout.layout(moveSum, 0, moveSum + lineWidth, lineHeight);
        }
    }
}
