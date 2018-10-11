package com.hxd.jewelry.simple.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Cazaea
 * @time 2017/5/9 8:45
 * @mail wistorm@sina.com
 */

public class PreferUtil {

    private Context context;

    private static SharedPreferences prefer;
    private static SharedPreferences.Editor editor;

    // SharedPreferences 文件名
    private static final String PREFER_FILE_NAME = "lease_data";
    // 最新版本信息
    private static final String LEAST_VERSION_CODE = "LEAST_VERSION_CODE";

    /**
     * PreferUtil构造器
     *
     * @param context
     */
    private PreferUtil(Context context) {
        this.context = context;
        prefer = context.getSharedPreferences(PREFER_FILE_NAME, Context.MODE_PRIVATE);
        editor = prefer.edit();
        editor.apply();
    }

    /**
     * 获取PreferUtil实例
     *
     * @param context
     * @return
     */
    public static PreferUtil getInstance(Context context) {
        return new PreferUtil(context);
    }

    /**
     * 向sharePreferences文件中放入String类型的数据
     *
     * @param key   数据的key值
     * @param value 数据
     */
    public static void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    /**
     * 获取String消息内容
     *
     * @return key所对应value值
     */
    public static String getString(String key) {
        return prefer.getString(key, null);
    }

    /**
     * 向sharePreferences文件中放入int类型的数据
     *
     * @param key   数据的key值
     * @param value 数据
     */
    private static void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    /**
     * 获取int类型消息内容
     *
     * @return key所对应value值
     */
    private static int getInt(String key) {
        return prefer.getInt(key, -1);
    }

    /**
     * 获取存入的versionCode
     *
     * @return 上次启动，存入的VersionCode
     */
    public int getSavedVersionCode() {
        return getInt(LEAST_VERSION_CODE);
    }

    /**
     * 存入最新的versionCode
     */
    public void putLeastVersionCode() {
        putInt(LEAST_VERSION_CODE, VersionUtil.getVersionCode(context));
    }

}
