package com.hxd.jewelry.simple.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.utils.DensityUtil;

import java.util.List;

/**
 * 垂直滚动广告栏
 * 待优化：
 * 1.刷新数据，阴影重叠
 * 2.单一数据滚动不流畅
 * <p>
 * 作 者： Cazaea
 * 日 期： 2018/6/5
 * 邮 箱： wistorm@sina.com
 */
public class MarqueeView extends ViewFlipper {

    private Context mContext;

    // 是否设置动画时间
    private boolean isSetAnimDuration;
    private MarqueeViewItemClickListener mMarqueeViewItemClickListener;

    private int textSize = 14;
    private int textColor = 0xffffffff;
    private int interval = 2000;
    private int animDuration = 500;

    private List<String> noticeList;

    private int gravity = Gravity.CENTER_VERTICAL;
    private static final int TEXT_GRAVITY_LEFT = 0, TEXT_GRAVITY_CENTER = 1, TEXT_GRAVITY_RIGHT = 2;

    public MarqueeView(Context context) {
        super(context);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.mContext = context;
        TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeViewStyle, defStyle, 0);
        interval = typeArray.getInteger(R.styleable.MarqueeViewStyle_mvInterval, interval);
        textColor = typeArray.getColor(R.styleable.MarqueeViewStyle_mvTextColor, textColor);
        if (typeArray.hasValue(R.styleable.MarqueeViewStyle_mvTextSize)) {
            textSize = typeArray.getDimensionPixelSize(R.styleable.MarqueeViewStyle_mvTextSize, textSize);
            textSize = (int) DensityUtil.px2sp(context, textSize);
        }
        animDuration = typeArray.getInteger(R.styleable.MarqueeViewStyle_mvAnimDuration, animDuration);

        int gravityType = typeArray.getInteger(R.styleable.MarqueeViewStyle_mvGravity, gravity);
        switch (gravityType) {
            case TEXT_GRAVITY_LEFT:
                gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                break;
            case TEXT_GRAVITY_CENTER:
                gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER;
                break;
            case TEXT_GRAVITY_RIGHT:
                gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER;
                break;
        }
        typeArray.recycle();
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_in);
        if (isSetAnimDuration) animIn.setDuration(animDuration);
        setInAnimation(animIn);

        Animation animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_out);
        if (isSetAnimDuration) animOut.setDuration(animDuration);
        setOutAnimation(animOut);


    }

    /**
     * 开启跑马灯
     *
     * @param notices
     */
    public void startMarqueeWithList(final List<String> notices) {
        if (notices == null || notices.size() == 0)
            return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                checkNotices(notices);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    /**
     * 校验数据源
     *
     * @param notices
     */
    private void checkNotices(List<String> notices) {
        /*// 初始化
        if (noticeList == null)
            noticeList = new ArrayList<String>();
        else {
            // 停止滚动
            if (isFlipping())
                stopFlipping();
            // 清除所有子View
            removeAllViews();
            // 重新添加数据
            noticeList.clear();
        }

        // 校验获取数据源
        for (String notice : notices) {
            // 垂直滚动控件宽度
            int dpW = DensityUtil.px2dip(mContext, getWidth());
            // 控件可显示字符个数
            int limit = dpW / textSize;
            if (dpW == 0) {
                throw new RuntimeException("Please set MarqueeView width !");
            }
            // 单条信息字符数
            int noticeLength = notice.length();
            // 是否能显示完全
            if (noticeLength <= limit) {
                noticeList.add(notice);
            } else {
                // 截取的段数(内容过长，截段显示)
                int index = noticeLength / limit + (noticeLength % limit == 0 ? 0 : 1);
                for (int i = 0; i < index; i++) {
                    int startIndex = i * limit;
                    int endIndex = (i + 1) * limit > noticeLength ? noticeLength : (i + 1) * limit;
                    noticeList.add(notice.substring(startIndex, endIndex));
                }
            }
        }
        // 开始添加数据
        startAddViews(noticeList);*/

        // 停止滚动
        if (isFlipping())
            stopFlipping();
        // 清除所有子View
        removeAllViews();

        // 开始添加数据
        startAddViews(notices);
    }

    /**
     * 开始添加数据
     */
    private void startAddViews(List<String> pNoticeList) {
        // 适配数据
        for (int i = 0; i < pNoticeList.size(); i++) {
            TextView textView = createTextView(pNoticeList.get(i), i);
            addView(textView);
        }
        // 开始滚动
        if (!isFlipping())
            startFlipping();
    }

    /**
     * 创建子View以及事件绑定
     *
     * @param noticeStr
     * @param position
     * @return
     */
    private TextView createTextView(String noticeStr, final int position) {
        TextView tv = new TextView(mContext);
        tv.setText(noticeStr);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        tv.setGravity(gravity);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTag(position);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarqueeViewItemClickListener.marqueeViewItemClick(position);
            }
        });
        return tv;
    }

    public interface MarqueeViewItemClickListener {
        void marqueeViewItemClick(int position);
    }

    public void setMarqueeViewItemClickListener(MarqueeViewItemClickListener marqueeViewItemClickListener) {
        mMarqueeViewItemClickListener = marqueeViewItemClickListener;
    }

}