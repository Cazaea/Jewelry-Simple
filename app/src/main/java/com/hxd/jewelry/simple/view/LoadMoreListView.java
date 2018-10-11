package com.hxd.jewelry.simple.view;//package com.hxd.jewelry.com.hxd.jewelry.demo.view;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.ListView;
//
//import com.hxd.jewelry.demo.R;
//
///**
// * @author Cazaea
// * @time 2018/3/22 9:48
// * @mail wistorm@sina.com
// */
//public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
//
//    private View mLoadMoreView;
//    private View mLoadCompleteView;
//    private OnLoadMoreListener mOnLoadMoreListener;
//
//    // 是否加载中或已加载所有数据
//    private boolean mIsLoadingOrComplete = false;
//    // 是否所有条目都可见
//    private boolean mIsAllVisible;
//
//    public LoadMoreListView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public LoadMoreListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    // 初始化
//    private void init(Context context) {
//        // 加载更多Footer
//        mLoadMoreView = LayoutInflater.from(context).inflate(R.layout.load_more, null);
//        // 没有更多数据Footer
//        mLoadCompleteView = LayoutInflater.from(context).inflate(R.layout.load_complete, null);
//        // 设置拉动监听
//        this.setOnScrollListener(this);
//    }
//
//    // 加载更多回调接口
//    public interface OnLoadMoreListener {
//        void loadMore();
//    }
//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        // (最后一条可见item==最后一条item)&&(停止滑动)&&(!加载数据中)&&(!所有条目都可见)
//        if (getAdapter().getCount() - 1 == view.getLastVisiblePosition() && scrollState == SCROLL_STATE_IDLE && !mIsLoadingOrComplete && !mIsAllVisible) {
//            if (null != mOnLoadMoreListener) {
//                // 加载更多(延时1.5秒,防止加载速度过快导致加载更多布局闪现)
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mOnLoadMoreListener.loadMore();
//                    }
//                }, 1500);
//            }
//        }
//        if (getFooterViewsCount() == 0 && !mIsAllVisible) addFooterView(mLoadMoreView);
//
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        // 判断总Item个数是否等于已显示Item个数
////        mIsAllVisible = totalItemCount == visibleItemCount;
//        mIsAllVisible = false;
//    }
//
//    /**
//     * 加载更多回调
//     *
//     * @param onLoadMoreListener 加载更多回调接口
//     */
//    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
//        mOnLoadMoreListener = onLoadMoreListener;
//    }
//
//    /**
//     * 通知此次加载完成,remove footerView
//     *
//     * @param allComplete 是否已加载全部数据
//     */
//    public void setLoadCompleted(boolean allComplete) {
//
//        if (allComplete && getFooterViewsCount() != 0) {
//            mIsLoadingOrComplete = true;
//            removeFooterView(mLoadMoreView);
//            removeFooterView(mLoadCompleteView);
//            addFooterView(mLoadCompleteView);
//        } else {
//            mIsLoadingOrComplete = false;
//            removeFooterView(mLoadMoreView);
//            removeFooterView(mLoadCompleteView);
//        }
//    }
//
//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }
//
//}
