package com.hxd.jewelry.simple.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxd.jewelry.simple.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Cazaea
 * @time 2017/6/23 17:24
 * @mail wistorm@sina.com
 */

public class HomeFunctionGridAdapter extends BaseAdapter {

    // 传过来一个上下文对象
    private Context context;

    // 适配数据(图片)
    private int[] imageList;
    // 适配数据(标题)
    private String[] titleList;

    public HomeFunctionGridAdapter(Context context, String[] titleList, int[] imageList) {
        this.titleList = titleList;
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public int getCount() {
        int length = titleList.length;
        int num = length % 3;
        if (num != 0) {
            return (length + (3 - num));
        } else {
            return length;
        }
    }

    @Override
    public Object getItem(int i) {
        return titleList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_grid_home_function, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            // 图片
            holder.image.setImageDrawable(context.getResources().getDrawable(imageList[i]));
            // 标题
            holder.tvName.setText(titleList[i]);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
