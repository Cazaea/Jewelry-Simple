package com.hxd.jewelry.simple.view;

import android.widget.GridView;

/**
 * @author Cazaea
 * @time 2017/3/9 15:37
 * @mail wistorm@sina.com
 *
 * 解决ListView嵌套GridView,GridView高度显示不完全问题
 *
 */

public class SelfGridView extends GridView {

    public SelfGridView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
