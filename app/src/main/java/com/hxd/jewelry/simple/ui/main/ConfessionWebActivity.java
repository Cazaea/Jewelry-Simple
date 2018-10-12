package com.hxd.jewelry.simple.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.adapters.PreLoadAdapter;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.Null;
import com.hxd.jewelry.simple.utils.LogcatUtil;
import com.hxd.jewelry.simple.utils.LoginUtil;
import com.hxd.jewelry.simple.utils.ParamUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.utils.UrlAnalysis;
import com.hxd.jewelry.simple.utils.reWeb.ReWebChromeClient;
import com.hxd.jewelry.simple.utils.reWeb.ReWebViewClient;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.CacheManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.thejoyrun.router.RouterField;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import butterknife.BindView;

@RouterActivity("confession")
public class ConfessionWebActivity extends BaseActivity implements ReWebChromeClient.OpenFileChooserCallBack {

    @BindView(R.id.web_toolbar)
    Toolbar webToolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wv_main)
    WebView wvMain;
    @BindView(R.id.lv_main)
    ListView lvMain;

    // 第一次路由传入的链接
    @RouterField("url")
    String url;

    // 首页加载Web等待框
    PreLoadAdapter adapter;

    // 标记位 登录失败后是否需要关闭本页面
    private String should_close_after_login_fail;

    // 用于 WebView 中选择图片
    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private ValueCallback<Uri> mUploadMsg;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private OptBridge optBridge;

    /**
     * 视频全屏参数
     */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback customViewCallback;

    /**
     * 用于接收EventBus传递的事件总线，登录成功后reload所有web重置用户数据
     * 用法：在需要刷新web的地方调用  EventBus.getDefault().post(EventConfig.EVENT_REFRESH_WEB_INFO);
     *
     * @param message EventConfig中的选项
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reloadWeb(String message) {
        if (message.equals(EventConfig.EVENT_EXIT_WITHOUT_LOGIN)) {
            if (should_close_after_login_fail.equals("1")) {
                ConfessionWebActivity.this.finish();
            }
        }
        if (message.equals(EventConfig.EVENT_REFRESH_WEB_INFO)) {
            String currentUrl = wvMain.getUrl();
            String reloadUrl = ParamUtil.urlAddUserInfo(currentUrl);
            wvMain.loadUrl(reloadUrl);
        }
    }

    @Override
    protected int setLayoutId() {
        // 权限申请
        EventBus.getDefault().post(EventConfig.EVENT_REQUEST_PERMISSION);
        return R.layout.activity_main_web;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置浅色字体，深色状态栏
        setStatusBarMode(false, webToolbar, R.color.colorWhite);
        //（这个对宿主没什么影响，建议声明）
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 初始化WebView
        initWebView();
        // 支持WebView使用比地浏览器下载或第三方应用下载
        supportDownload(wvMain);

    }

    @Override
    protected void initData() {
        super.initData();

        // 预加载占位图
        lvMain.setVisibility(View.VISIBLE);
        wvMain.setVisibility(View.GONE);

        // 设置显示数据
        GetData(8);
        adapter = new PreLoadAdapter(mData, this);
        adapter.AddAll(mData);
        lvMain.setAdapter(adapter);

        should_close_after_login_fail = "0";

        // 首次加载页面
        loadUrl(ParamUtil.urlAddThreeInfo(ApiConfig.ConfessionApi));
    }

    /**
     * 占位预加载图片处理
     */
    private Null testData;
    private LinkedList<Null> mData = null;

    private void GetData(int count) {
        mData = new LinkedList<Null>();
        for (int i = 0; i < count; i++) {
            testData = new Null(R.drawable.image_home_loading, i);
            mData.add(testData);
        }
    }

    /**
     * 初始化web内核设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = wvMain.getSettings();

        // 设置WebView可与Js代码进行交互
        settings.setJavaScriptEnabled(true);
        // 设置JS可打开WebView新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setAllowFileAccess(true);
        // settings.setAllowFileAccessFromFileURLs (true);
        // settings.setAllowUniversalAccessFromFileURLs (true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        // 设置页面是否支持缩放
        settings.setSupportZoom(true);
        // 显示WebView提供的缩放控件
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);

        // 被这个tag声明的宽度将被使用，页面没有tag或者没有提供一个宽度，那么一个宽型viewport将会被使用。
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(true);
        // settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        // settings.setDatabaseEnabled(true);
        // settings.setDatabasePath("...");

        // 打开WebView的storage功能，使JS的localStorage,sessionStorage对象可用
        settings.setDomStorageEnabled(true);
        // 打开WebView的LBS功能，使JS的geolocation对象可用
        settings.setGeolocationEnabled(true);
        // settings.setGeolocationDatabasePath("...");

        // 设置WebView的字体，默认为"sans-serif"
        // settings.setStandardFontFamily("");
        // 设置WebView字体的大小，默认为16
        // settings.setDefaultFontSize(20);
        // 设置WebView支持的最小字体大小，默认为8
        // settings.setMinimumFontSize(12);

        // 设置是否打开 WebView 表单数据的保存功能
        // settings.setSaveFormData(true);

        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        // settings.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NORMAL);

        // Https加载不安全的Http资源,设置不被阻止
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        optBridge = new OptBridge();
        wvMain.addJavascriptInterface(optBridge, "OptBridge");

        wvMain.setWebViewClient(new ReWebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.trim().equals("")) {
                    return true;
                }
                // 截取Url中的scheme部分 如"http","https","lease"
                String schemePart = url.substring(0, url.indexOf(":"));
                // Url中的链接部分 如"http://www.lease.com"
                String routerPart = UrlAnalysis.UrlPage(url);
                // Url中的参数部分
                Map<String, String> valuePart = UrlAnalysis.URLRequest(url);

                if (schemePart.equals(AppConfig.ROUTER_HEAD)) {
                    // 登录 type1 转到登录页 登录完成后更新所有web用户信息
                    if (routerPart.contains(AppConfig.ROUTER_TOTAL_HEAD + "login")) {
                        should_close_after_login_fail = valuePart.get("close");
                        Router.startActivity(ConfessionWebActivity.this, url);
                    }
                    // 如果是未登录状态，且需要登录
                    if (!LoginUtil.isLogin() && valuePart.get("login").equals("1")) {
                        Router.startActivity(ConfessionWebActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "login");
                    } else {
                        Router.startActivity(ConfessionWebActivity.this, url);
                    }
                    return true;
                }

                WebView.HitTestResult hit = wvMain.getHitTestResult();
                int hitType = hit.getType();
                if (hitType != WebView.HitTestResult.UNKNOWN_TYPE) {
                    // 这里执行自定义的操作
                    url = url.replace("&", "%26");
                    Router.startActivity(ConfessionWebActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + url);
                    return true;
                } else {
                    // 重定向时hitType为0 ,执行默认的操作
                    return false;
                }
            }

            @Override
            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
                ToastUtil.showShortToast(ConfessionWebActivity.this, "页面加载失败");
                // 关闭加载页面
                lvMain.setVisibility(View.GONE);
                wvMain.setVisibility(View.VISIBLE);
                LogcatUtil.e("网页加载失败!");
            }
        });

        /*
         * 设置Web页面标题，以及监听页面加载进度
         */
        wvMain.setWebChromeClient(new ReWebChromeClient(this) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tvTitle.setText("AR情书");
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 关闭加载页面
                    lvMain.setVisibility(View.GONE);
                    wvMain.setVisibility(View.VISIBLE);
                    LogcatUtil.d("页面加载完成!");
                }
            }

            /**
             * 视频播放相关的方法
             * @return
             */
            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(ConfessionWebActivity.this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }

        });

    }

    /**
     * 视频播放全屏
     */
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        ConfessionWebActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(ConfessionWebActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        wvMain.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public final class OptBridge {
        @JavascriptInterface
        public void OptClose() {
            Message msg = new Message();
            msg.what = 3000;
            mHandler.sendMessage(msg);
        }

        @JavascriptInterface
        public void OptSendNotification(String msg) {
            if (msg.equals("webReload")) {
                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_WEB_INFO);
                return;
            }
            if (msg.equals("userInfoRefresh")) {
                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);
                return;
            }

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 关闭本页面
            if (msg.what == 3000) {
                if (customView != null) {
                    hideCustomView();
                } else if (wvMain != null && wvMain.canGoBack()) {
                    wvMain.goBack();
                } else {
                    ConfessionWebActivity.this.finish();
                }
            }
        }
    };

    /**
     * 加载链接
     *
     * @param url
     */
    private void loadUrl(String url) {
        // 关闭加载页面
        lvMain.setVisibility(View.VISIBLE);
        wvMain.setVisibility(View.GONE);
        wvMain.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        // clear
        File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
        // WebView关闭
        if (wvMain != null) {
            wvMain.clearCache(true);
            wvMain.clearFormData();
            wvMain.clearHistory();
            wvMain.clearMatches();
            wvMain.clearSslPreferences();
            wvMain.clearFocus();
            wvMain.clearDisappearingChildren();
            wvMain.clearAnimation();
            wvMain.removeAllViews();
            wvMain.destroy();
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (customView != null) {
                hideCustomView();
            } else if (wvMain != null && wvMain.canGoBack()) {
                wvMain.goBack();
            } else {
                EventBus.getDefault().post(EventConfig.EVENT_PRESS_BACK);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity 结果的回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
                mUploadMsg = null;
            }
            if (mUploadMessageForAndroid5 != null) {
                mUploadMessageForAndroid5.onReceiveValue(null);
                mUploadMessageForAndroid5 = null;
            }
            return;
        }

        File temp = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        if (mUploadMessageForAndroid5 != null) {

            switch (requestCode) {
                case REQUEST_CODE_IMAGE_CAPTURE:
                    if (temp.exists()) {
                        mUploadMessageForAndroid5.onReceiveValue(new Uri[]{Uri.fromFile(temp)});
                    } else {
                        mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                    }
                case REQUEST_CODE_PICK_IMAGE:
                    try {
                        final Uri result = (data == null) ? null : data.getData();
                        if (null != mUploadMessageForAndroid5) {
                            if (result != null) {
                                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
                            } else {
                                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }


        if (mUploadMsg != null) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE_CAPTURE:
                    if (temp.exists()) {
                        mUploadMsg.onReceiveValue(Uri.fromFile(temp));
                    } else {
                        mUploadMsg.onReceiveValue(null);
                    }
                    Uri uri = Uri.fromFile(temp);
                    mUploadMsg.onReceiveValue(uri);
                case REQUEST_CODE_PICK_IMAGE:

                    final Uri result = (data == null) ? null : data.getData();
                    try {
                        mUploadMsg.onReceiveValue(result);
                        // PicAsyncTask(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 权限申请的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 300:
                /*
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
                startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
                */
                imageCapture();
                break;
        }
    }

    /**
     * 判断系统及拍照
     */
    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(this,
                    AppConfig.PROVIDER_FILE_NAME, pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
    }


    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMsg = uploadMsg;
        showOptions();
    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadMessageForAndroid5 = uploadMsg;
        showOptions();
    }

    /**
     * 显示 "拍照/相册" 选项
     */
    public void showOptions() {

        CharSequence[] sequences = {"相册", "拍照"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setTitle("选择图片");
        alertDialog.setItems(sequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent showImgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(showImgIntent, REQUEST_CODE_PICK_IMAGE);
                } else {
                    // 权限申请
                    if (ContextCompat.checkSelfPermission(ConfessionWebActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(ConfessionWebActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 权限还没有授予，需要在这里写申请权限的代码
                        ActivityCompat.requestPermissions(ConfessionWebActivity.this,
                                new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                    } else {
                        // 权限已经申请，直接拍照
                        imageCapture();
                    }
                }
            }
        });
        alertDialog.show();
    }


    /**
     * 对话框取消后的回调
     */
    private class ReOnCancelListener implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
                mUploadMsg = null;
            }
            if (mUploadMessageForAndroid5 != null) {
                mUploadMessageForAndroid5.onReceiveValue(null);
                mUploadMessageForAndroid5 = null;
            }
        }
    }

    /**
     * 支持WebView使用比地浏览器下载或第三方应用下载
     */
    private void supportDownload(WebView view) {
        view.setDownloadListener(new MyWebViewDownLoadListener());
    }

    /**
     * 支持下载功能类实现
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mime_type, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
}
