package com.hxd.jewelry.simple.utils.reWeb;

import android.net.Uri;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

public class ReWebChromeClient extends com.tencent.smtt.sdk.WebChromeClient {

    private OpenFileChooserCallBack mOpenFileChooserCallBack;


    public ReWebChromeClient(OpenFileChooserCallBack openFileChooserCallBack) {
        mOpenFileChooserCallBack = openFileChooserCallBack;
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }


    // For Android > 5.0
    public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg,fileChooserParams);
        return true;
    }


    /**
     * 自定义接口
     */
    public interface OpenFileChooserCallBack {

        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void openFileChooserCallBack(ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams);
    }

}
