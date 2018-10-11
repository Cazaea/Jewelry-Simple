package com.hxd.jewelry.simple.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 应用相关信息获取
 * Created by Cazaea 2016/12/6.
 */

public class VersionUtil {

    /**
     * 获取APP包名
     *
     * @param context
     * @return 包名
     */
    public static String getAppId(Context context) {
        String appId = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            appId = info.packageName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appId;
    }

    /**
     * 获取APP版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "0";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取APP版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

}
