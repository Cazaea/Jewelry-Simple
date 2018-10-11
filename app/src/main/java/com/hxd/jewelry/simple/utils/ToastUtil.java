package com.hxd.jewelry.simple.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * 全局Toast提示框
 * 动态关闭Toast打印
 * <p>
 * 作 者： Cazaea
 * 日 期： 2018/5/3
 * 邮 箱： wistorm@sina.com
 */
public class ToastUtil {

    // Toast实例
    private static Toast mToast;

    @SuppressLint("ShowToast")
    private static void getToast(Context context) {
        if (mToast == null) {
            mToast = new Toast(context);
        }
        View mView = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
        mToast.setView(mView);
    }

    public static void showShortToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
    }

    public static void showLongToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, CharSequence msg, int duration) {
        try {
            getToast(context);
            mToast.setText(msg);
            mToast.setDuration(duration);
            mToast.show();
        } catch (Exception e) {
            LogcatUtil.e(e.getMessage());
        }
    }

    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast(context);
            mToast.setText(resId);
            mToast.setDuration(duration);
            mToast.show();
        } catch (Exception e) {
            LogcatUtil.e(e.getMessage());
        }
    }

    /**
     * 判断Toast是否正在提示
     */
    public static boolean isShowing() {
        return mToast != null && mToast.getView() != null;
    }

    /**
     * 关闭正在显示的Toast
     */
    public static void cancelToast() {
        if (isShowing()) {
            mToast.cancel();
            mToast = null;
        }
    }

}