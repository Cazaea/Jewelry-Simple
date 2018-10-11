package com.hxd.jewelry.simple.ui.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.ui.login.LoginActivity;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.ShareInfo;
import com.hxd.jewelry.simple.data.WebButton;
import com.hxd.jewelry.simple.utils.LogcatUtil;
import com.hxd.jewelry.simple.utils.LoginUtil;
import com.hxd.jewelry.simple.utils.ParamUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.utils.UrlAnalysis;
import com.hxd.jewelry.simple.utils.reWeb.ReWebChromeClient;
import com.hxd.jewelry.simple.utils.reWeb.ReWebViewClient;
import com.hxd.jewelry.simple.view.CustomDialog;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CacheManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.thejoyrun.router.RouterField;
//import com.umeng.socialize.ShareAction;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.UMShareListener;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@RouterActivity("web")
public class WebsiteActivity extends BaseActivity implements ReWebChromeClient.OpenFileChooserCallBack {

    @BindView(R.id.web_tv_ctrl)
    TextView webTvCtrl;
    @BindView(R.id.web_iv_share)
    ImageView webIvShare;
    @BindView(R.id.web_toolbar)
    Toolbar webToolbar;
    @BindView(R.id.web_tv_title)
    TextView tvTitle; // 标题
    @BindView(R.id.web_wv)
    WebView webView; // webview
//    @BindView(R.id.smart_refresh)
//    SelfRefreshView smartRefresh;

    // 第一次路由传入的链接
    @RouterField("url")
    String url;
    @RouterField("title")
    String title;

    private OptBridge optBridge;

    // 标记位 登录失败后是否需要关闭本页面
    private String should_close_after_login_fail;
    // 加载loading，可取消
    private CustomDialog customDialog;

    // 用于 WebView 中选择图片
    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private ValueCallback<Uri> mUploadMsg;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    // 分享数据信息
    private ShareInfo mShareInfo;
//    // 友盟社会化分享
//    private UMShareListener mUMShareListener;

    // 按钮
    private WebButton mWebButton;

    // 提示声音
    private MediaPlayer mediaPlayer;

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
                WebsiteActivity.this.finish();
            }
        }
        // TODO 认证成功，关闭认证页面
        if (message.equals(EventConfig.EVENT_VERIFIED_SUCCESS_CLOSE_ACTIVITY)) {
            WebsiteActivity.this.finish();
        }
        if (message.equals(EventConfig.EVENT_REFRESH_WEB_INFO)) {
            String currentUrl = webView.getUrl();
            String reloadUrl = ParamUtil.urlAddUserInfo(currentUrl);
            // TODO 重新加载数据(赎回/充值,刷新Web)
            webView.loadUrl(reloadUrl);
        }
        if (message.equals(EventConfig.EVENT_WEB_BUTTON_SHARE)) {
            webIvShare.setVisibility(View.VISIBLE);
        }
        if (message.equals(EventConfig.EVENT_WEB_BUTTON_SIGN)) {
            webTvCtrl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int setLayoutId() {
        // 权限申请
        EventBus.getDefault().post(EventConfig.EVENT_REQUEST_PERMISSION);
        return R.layout.activity_website;
    }

    @Override
    protected void requestPermission() {
        super.requestPermission();
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置浅色字体，深色状态栏
        setStatusBarMode(false, webToolbar, R.color.colorWhite);
        // 控件初始化与属性设置
        initAndSetDefaultValue();
        // 初始化下拉刷新
//        initRefresh();
        // 初始化WebView
        initWebViewSettings();
        // 支持WebView使用比地浏览器下载或第三方应用下载
        supportDownload(webView);
    }

    @Override
    protected void initData() {
        super.initData();
        // 标题设置
        tvTitle.setText(title);
        // 首次加载页面
        if (!TextUtils.isEmpty(url))
            loadUrl(ParamUtil.urlAddUserInfo(url));
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
        if (webView != null) {
            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
            webView.clearMatches();
            webView.clearSslPreferences();
            webView.clearFocus();
            webView.clearDisappearingChildren();
            webView.clearAnimation();
            webView.removeAllViews();
            webView.destroy();
        }

        // 释放媒体
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    /**
     * 控件初始化与属性设置
     */
    private void initAndSetDefaultValue() {

        getWindow().setFormat(PixelFormat.TRANSLUCENT);//（这个对宿主没什么影响，建议声明）
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // 默认不显示
        webTvCtrl.setVisibility(View.GONE);
        webIvShare.setVisibility(View.GONE);

        should_close_after_login_fail = "0";

        mediaPlayer = MediaPlayer.create(WebsiteActivity.this, R.raw.verify_success_bdc);

//        mUMShareListener = new UMShareListener() {
//            @Override
//            public void onStart(SHARE_MEDIA share_media) {
//            }
//
//            @Override
//            public void onResult(SHARE_MEDIA share_media) {
//            }
//
//            @Override
//            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//            }
//
//            @Override
//            public void onCancel(SHARE_MEDIA share_media) {
//            }
//        };
    }

    /**
     * 初始化下来加载
     */
    /*private void initRefresh() {
        // 下拉刷新
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                // TODO 刷新Web页面
                String currentUrl = webView.getUrl();
                String reloadUrl = ParamUtil.urlAddUserInfo(currentUrl);
                webView.loadUrl(reloadUrl);
                // 延时一秒，刷新成功
                smartRefresh.finishRefresh(1000, true);
            }
        });

    }*/

    /**
     * 初始化web内核设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings settings = webView.getSettings();

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
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Https加载不安全的Http资源,设置不被阻止
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        optBridge = new OptBridge();
        webView.addJavascriptInterface(optBridge, "OptBridge");

        // 取消滚动条
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // 触摸焦点起作用
        webView.requestFocus();
        // 点击链接继续在当前browser中响应
        webView.setWebViewClient(new ReWebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String currentUrl) {
                if (currentUrl.trim().equals("")) {
                    return true;
                }
                // 截取Url中的scheme部分 如"http","https","lease"
                String schemePart = currentUrl.substring(0, currentUrl.indexOf(":"));
                // Url中的链接部分 如"http://www.lease.com"
                String routerPart = UrlAnalysis.UrlPage(currentUrl);
                // Url中的参数部分
                Map<String, String> valuePart = UrlAnalysis.URLRequest(currentUrl);

                WebView.HitTestResult hit = webView.getHitTestResult();
                int hitType = hit.getType();

                // 跳转原生界面
                if (schemePart.equals(AppConfig.ROUTER_HEAD)) {
                    // 登录 type1 转到登录页 登录完成后更新所有web用户信息
                    if (routerPart.contains(AppConfig.ROUTER_TOTAL_HEAD + "login")) {
                        should_close_after_login_fail = valuePart.get("close");
                        Router.startActivity(WebsiteActivity.this, currentUrl);
                    }
                    // 如果是未登录状态，且需要登录
                    if (!LoginUtil.isLogin() && valuePart.get("login").equals("1")) {
//                        Router.startActivity(WebsiteActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "login?url=" + currentUrl);
                        Intent intent = new Intent(WebsiteActivity.this, LoginActivity.class);
                        intent.putExtra("url", currentUrl);
                        intent.putExtra("type", "web");
                        startActivityForResult(intent, 100);
                    } else {
                        Router.startActivity(WebsiteActivity.this, currentUrl);
                    }
                    return true;
                } else if (schemePart.equals("tel")) {
                    // 不跳原生，是打电话，那就打电话
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(currentUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                } else if (hitType != WebView.HitTestResult.UNKNOWN_TYPE) {
                    // 这里执行自定义的操作
                    currentUrl = currentUrl.replace("&", "%26");
                    Router.startActivity(WebsiteActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + currentUrl);
                    return true;
                } else {
                    // 重定向时hitType为0 ,执行默认的操作
                    return false;
                }
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(Message msg); // 进行其他处理
            }

            @Override
            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
                // 关闭加载
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.dismiss();
                }
                ToastUtil.showShortToast(WebsiteActivity.this, "页面加载失败");
                LogcatUtil.e("网页加载失败!========>" + var1.getUrl());
            }
        });

        /*
         * 设置Web页面标题，以及监听页面加载进度
         */
        webView.setWebChromeClient(new ReWebChromeClient(this) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                tvTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    LogcatUtil.d("页面加载完成!");
                    // 关闭加载
                    if (customDialog != null && customDialog.isShowing()) {
                        customDialog.dismiss();
                    }
                    // 开始播放提示音
                    if (title != null && title.equals("验证结果")) {
                        mediaPlayer.start();
                    }

                }
            }

            /**
             * 视频播放相关的方法
             * @return
             */
            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(WebsiteActivity.this);
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
     * 分享功能
     */
    /*private void shareWebInfo() {

        if (mShareInfo == null) {
            return;
        }

        // 链接
        UMWeb web = new UMWeb(mShareInfo.mLinkUrl);
        // 标题
        web.setTitle(mShareInfo.mTitle);
        // 缩略图
        UMImage thumb = new UMImage(WebsiteActivity.this, mShareInfo.mImage);
        web.setThumb(thumb);
        // 描述
        web.setDescription(mShareInfo.mDescription);

        new ShareAction(WebsiteActivity.this).withMedia(web).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(mUMShareListener).open();
    }*/

    /**
     * 视频播放全屏
     */
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        WebsiteActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(WebsiteActivity.this);
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
        webView.setVisibility(View.VISIBLE);
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
            msg.what = 1000;
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
            if (msg.equals("Share")) {
                EventBus.getDefault().post(EventConfig.EVENT_WEB_BUTTON_SHARE);
                return;
            }
            if (msg.equals("Sign")) {
                EventBus.getDefault().post(EventConfig.EVENT_WEB_BUTTON_SIGN);
                return;
            }
        }

        /**
         * WebView中按钮点击打开分享
         */
        @JavascriptInterface
        public void OptShare(String title, String description, String link, String picLink) {
            if (mShareInfo == null)
                mShareInfo = new ShareInfo();
            mShareInfo.mTitle = title;
            mShareInfo.mDescription = description;
            mShareInfo.mLinkUrl = link;
            mShareInfo.mImage = picLink;
//            shareWebInfo();
        }

        /**
         * WebView控制打开原生右上角的分享按钮
         */
        @JavascriptInterface
        public void OptEnableShareBtn(String title, String description, String link, String picLink) {
            if (mShareInfo == null)
                mShareInfo = new ShareInfo();
            mShareInfo.mTitle = title;
            mShareInfo.mDescription = description;
            mShareInfo.mLinkUrl = link;
            mShareInfo.mImage = picLink;

            Message msg = new Message();
            msg.what = 2000;
            mHandler.sendMessage(msg);
        }

        /**
         * WebView控制打开原生右上角的文字按钮
         */
        @JavascriptInterface
        public void OptEnableWebBtn(String title, String url) {
            if (mWebButton == null)
                mWebButton = new WebButton();
            mWebButton.mTitle = title;
            mWebButton.mLinkUrl = url;

            Message msg = new Message();
            msg.what = 3000;
            mHandler.sendMessage(msg);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 关闭本页面
            if (msg.what == 1000) {
                if (customView != null) {
                    hideCustomView();
                } else if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    WebsiteActivity.this.finish();
                }
            }
            // 显示分享按钮
            if (msg.what == 2000) {
                webIvShare.setVisibility(View.VISIBLE);
            }
            // 显示文字按钮
            if (msg.what == 3000) {
                webTvCtrl.setText(mWebButton.mTitle);
                webTvCtrl.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 加载链接
     *
     * @param url
     */
    private void loadUrl(String url) {

        // 可取消加载框容错判空
        if (customDialog == null)
            customDialog = new CustomDialog(this, R.style.LoadingDialogStyle);

        // 开始加载页面
        customDialog.show();

        // Url容错处理
        if (!url.contains(":")) {
            return;
        }

        // 截取Url中的scheme部分 如"http","https","lease"
        String schemePart = url.substring(0, url.indexOf(":"));
        // Url中的链接部分 如"http://www.lease.com"
        String routerPart = UrlAnalysis.UrlPage(url);
        // Url中的参数部分
        Map<String, String> valuePart = UrlAnalysis.URLRequest(url);

        WebView.HitTestResult hit = webView.getHitTestResult();
        int hitType = hit.getType();

        // 跳转原生界面
        if (schemePart.equals(AppConfig.ROUTER_HEAD)) {
            // 登录 type1 转到登录页 登录完成后更新所有web用户信息
            if (routerPart.contains(AppConfig.ROUTER_TOTAL_HEAD + "login")) {
                should_close_after_login_fail = valuePart.get("close");
                Router.startActivity(WebsiteActivity.this, url);
            }
            // 如果是未登录状态，且需要登录
            if (!LoginUtil.isLogin() && valuePart.get("login").equals("1")) {
                Intent intent = new Intent(WebsiteActivity.this, LoginActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("type", "web");
                startActivityForResult(intent, 100);
            } else {
                Router.startActivity(WebsiteActivity.this, url);
            }
        } else if (schemePart.equals("tel")) {
            // 不跳原生，是打电话，那就打电话
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (hitType != WebView.HitTestResult.UNKNOWN_TYPE) {
            // 这里执行自定义的操作
            url = url.replace("&", "%26");
            Router.startActivity(WebsiteActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + url);
        } else {
            // 重定向时hitType为0 ,执行默认的操作
            webView.loadUrl(url);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

//    /**
//     * 点击右上角的分享
//     */
//    @OnClick(R.id.web_iv_share)
//    public void onShare() {
//        shareWebInfo();
//    }

    /**
     * 点击右上角的文字
     */
    @OnClick(R.id.web_tv_ctrl)
    public void onText() {
        // TODO 这里执行自定义的操作
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (customView != null) {
                hideCustomView();
            } else if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity 结果的回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        // 友盟分享
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

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

        // 重新加载页面，跳转已登录页面
        if (requestCode == 100) {
            url = data.getStringExtra("url");
            loadUrl(ParamUtil.urlAddUserInfo(url));
        }

    }

    /**
     * 权限申请的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 300:
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
                    if (ContextCompat.checkSelfPermission(WebsiteActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(WebsiteActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 权限还没有授予，需要在这里写申请权限的代码
                        ActivityCompat.requestPermissions(WebsiteActivity.this,
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

    /**
     * 图片长按保存
     */
    private void longClickAction(WebView view) {
        // 操作图片（完整代码）
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;

                // 这里可以拦截很多类型，我们只处理图片类型就可以了
                switch (type) {
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 单纯的图片类型
                        // 获取图片的路径
                        String saveImgUrl = result.getExtra();
                        // 跳转到图片详情页，显示图片
//                        Intent i = new Intent(WebsiteActivity.this, ImageActivity.class);
//                        i.putExtra("imgUrl", saveImgUrl);
//                        startActivity(i);
                        break;
                    default:
                        break;
                }
                return true;
            }

        });

    }

}
