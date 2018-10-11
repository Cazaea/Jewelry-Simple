package com.hxd.jewelry.simple.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Cazaea on 2017/4/6.
 * 根据子item高度自动调节高度的ListView
 */

public class SelfAdaptionListView extends ListView {

    public SelfAdaptionListView(Context context) {
        super(context);
    }

    public SelfAdaptionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfAdaptionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}