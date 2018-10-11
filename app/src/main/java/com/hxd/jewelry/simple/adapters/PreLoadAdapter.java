package com.hxd.jewelry.simple.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.data.Null;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 预加载适配器
 * Created by Cazaea on 2017/6/28.
 */
public class PreLoadAdapter extends BaseAdapter {

    // 传递上下文对象
    private Context mContext;
    // 依据原楼主的代码，我们定义自己的数据类型，使用类来描述
    private LinkedList<Null> mLiData;

    public PreLoadAdapter(LinkedList<Null> mData, Context mContext) {
        this.mLiData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mLiData.size();
    }

    @Override
    public Object getItem(int position) {
        return mLiData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
//            convertView = View.inflate(mContext, R.layout.item_load_view, null);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_load_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivLoadingView.setImageResource(mLiData.get(position).getImgId());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_loading_view)
        ImageView ivLoadingView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //=======================================================================//
    /*
     * 以下是实现接口定义的方法
     *
     * 定义为Boolean 返回类型 是为了方便通知“前台”，成功了没有，
     * 注意 我们的position按照数据来，从0开始，当然你看着自己需求指定偏移量也可以
     *
     * (⊙﹏⊙)b 我打着插入。。。想想还是改成了添加。。。感觉自己病了
     */
    //=======================================================================//
    public boolean AddItem(Object data) {
        if (mLiData == null)
            mLiData = new LinkedList<Null>();
        mLiData.add((Null) data);
        notifyDataSetChanged();
        return true;
    }

    public boolean AddItem(int position, Object data) {
        if (mLiData == null)
            mLiData = new LinkedList<Null>();
        //注意超过了数据源的实际条目数时，需要的是添加到尾部，而不是直接添加，更不是在中间补充空值
        //当然，你也可以认为必须朝该位置插入，不能满足时通知前台要求不被许可，不执行。 我的例子中就采用这样处理
        if (position > getCount())
//			AddItem(data); //这里对应调整向朝尾部添加
            return false;
        else
            mLiData.add(position, (Null) data);
        notifyDataSetChanged();
        return true;
    }

    public boolean DeleteItem(int position) {
        if (mLiData == null)
            return false;
        if (position >= getCount())
            return false;
        mLiData.remove(position);
        notifyDataSetChanged();
        return true;
    }

    public void Clear() {
        if (mLiData == null)
            mLiData = new LinkedList<Null>();
        mLiData.clear();
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public void AddAll(Object data) {
        if (mLiData == null)
            mLiData = new LinkedList<Null>();
        mLiData.addAll((LinkedList<Null>) data);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public void ReplaceAll(Object data) {
        mLiData = new LinkedList<Null>();
        mLiData.addAll((LinkedList<Null>) data);
        notifyDataSetChanged();
    }

}