package com.hxd.jewelry.simple.utils.reWeb;

import android.graphics.Bitmap;

import com.hxd.jewelry.simple.utils.LogcatUtil;

public class ReWebViewClient extends com.tencent.smtt.sdk.WebViewClient {

    @Override
    public void onPageStarted(com.tencent.smtt.sdk.WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
        LogcatUtil.d("网页Url==>" + url);
        super.onPageFinished(view, url);
    }

}
