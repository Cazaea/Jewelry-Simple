package com.hxd.jewelry.simple.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

/**
 * @author Cazaea
 * @time 2017/icon_home_11/14 14:41
 * @mail wistorm@sina.com
 */
public class PagerAdapter extends android.support.v4.view.PagerAdapter implements Serializable {
    private List<View> views;

    public PagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(views.get(position), 0);
        return views.get(position);
    }
}
