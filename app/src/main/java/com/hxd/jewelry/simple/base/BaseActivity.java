package com.hxd.jewelry.simple.base;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.utils.status.StatusBarUtil;
import com.thejoyrun.router.Router;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 公用界面
 * <p>
 * 作 者： Cazaea
 * 日 期： 2018/4/27
 * 邮 箱： wistorm@sina.com
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 处理多次点击事件
     */
    private long firstClickTime = 0;// 第一次点击时间
    private long lastClickTime;// 最后一次点击时间

    // 控件绑定
    private Unbinder mUnbinder;
    // 数据传递：Main页面初始化
    public Bundle savedInstanceState;
    // 处理输入框
    private InputMethodManager inputMethodManager;

    /**
     * 任务线程，刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {

        // 开发者模式下，重启应用，关闭所有页面
        if (message.equals(EventConfig.EVENT_CLOSE_ALL_OPENED_ACTIVITY)) {
            this.finish();
        }

        // 双击退出程序
        if (message.equals(EventConfig.EVENT_PRESS_BACK)) {
            if (System.currentTimeMillis() - firstClickTime > 2000) {
                ToastUtil.showShortToast(BaseActivity.this, "再按一次退出程序");
                firstClickTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 传递数据
        this.savedInstanceState = savedInstanceState;
        // 绑定页面
        setContentView(setLayoutId());
        // 绑定控件
        mUnbinder = ButterKnife.bind(this);
        // 绑定路由
        Router.inject(this);
        // 注册事件总线
        EventBus.getDefault().register(this);

        // View与数据绑定
        initView();
        // 初始化数据渲染
        initData();
        // 设置监听事件
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除控件绑定
        mUnbinder.unbind();
        // 软键盘对象置空
        this.inputMethodManager = null;
        // 解绑EventBus
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置布局文件
     *
     * @return
     */
    protected abstract int setLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * View与数据绑定
     */
    protected void initView() {
    }

    /**
     * 设置监听
     */
    protected void setListener() {
    }

    /**
     * 权限申请
     */
    protected void requestPermission() {
        // 申请权限
        this.sharePermissionRequest();
    }

    /**
     * 消耗多次点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick())
                return isFastDoubleClick();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 规定两次点击间隔在500ms内为快速双击
     *
     * @return 是否快速点击
     */
    public boolean isFastDoubleClick() {
        // 当前点击时间
        long time = System.currentTimeMillis();
        // 两次点击时间差
        long timeD = time - lastClickTime;
        // 保存当前时间
        lastClickTime = time;
        // 是否两次点击间隔小于0.5秒
        return timeD <= 500;
    }

    /**
     * 监听返回按钮
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置标题栏颜色
     *
     * @param mDarkMode 状态栏显示颜色显示模式：
     *                  <p> true:深色状态栏(浅色字体)
     *                  <p> false:浅色状态栏(深色字体)
     * @param mToolbar  设置颜色的Toolbar
     * @param mColor    Toolbar以及StatusBar显示颜色
     */
    public void setStatusBarMode(boolean mDarkMode, Toolbar mToolbar, int mColor) {
        // Toolbar以及StatusBar显示颜色
        int color = getResources().getColor(mColor);
        // 设置状态栏颜色
        StatusBarUtil.setColor(BaseActivity.this, color, 0);
        // Toolbar处理
        if (mToolbar != null) {
            // 初始化Toolbar
            mToolbar.setTitle("");
            setSupportActionBar(mToolbar);
            // 设置Toolbar颜色
            mToolbar.setBackgroundColor(color);
        }
        // 设置显示模式
        if (mDarkMode) {
            // 设置白字
            StatusBarUtil.setDarkMode(BaseActivity.this);
        } else {
            // 设置白底黑字
            StatusBarUtil.setLightMode(BaseActivity.this);
        }
    }

    /**
     * 关闭页面
     */
    public void finish() {
        super.finish();
        // 关闭Toast弹框
        ToastUtil.cancelToast();
        // 关闭输入框
        hideSoftKeyBoard();
        closeKeyboard();
    }

    /**
     * 如果软键盘开启，关闭软键盘
     */
    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.inputMethodManager == null) {
            this.inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.inputMethodManager != null)) {
            this.inputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    /**
     * 关闭软键盘
     */
    private void closeKeyboard() {
        View localView = getWindow().peekDecorView();
        if (this.inputMethodManager == null) {
            this.inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.inputMethodManager != null)) {
            this.inputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
        }
    }

    /**
     * 分享功能权限申请
     */
    private void sharePermissionRequest() {
        if (Build.VERSION.SDK_INT >= 23) {
            // 所需要的权限组
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS
            };
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
    }

}
