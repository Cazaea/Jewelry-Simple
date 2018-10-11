package com.hxd.jewelry.simple.utils.filter;

import android.annotation.SuppressLint;

/**
 * @author Cazaea
 * @time 2017/icon_home_11/7 16:14
 * @mail wistorm@sina.com
 */

public class AmountTransUtil {

    public static String formatString(String info) {
        @SuppressLint("DefaultLocale") String result = String.format("%.2f", Float.parseFloat(info));
        return result;
    }

    public static String formatFloat(float info) {
        @SuppressLint("DefaultLocale") String result = String.format("%.2f", info);
        return result;
    }

    public static String formatInt(int info) {
        @SuppressLint("DefaultLocale") String result = String.format("%.2f", Float.parseFloat(String.valueOf(info)));
        return result;
    }

}
