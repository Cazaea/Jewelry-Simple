package com.hxd.jewelry.simple.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hxd.jewelry.simple.app.MainApp;
import com.hxd.jewelry.simple.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import xiaofei.library.datastorage.IDataStorage;

/**
 * 参数相关处理
 * Created by Cazaea on 2017/7/28.
 */

public class ParamUtil {

    /**
     * 为参数部分添加user_id和token
     *
     * @param params 需要添加参数的Map参数集合
     * @return
     */
    public static void paramAddUserInfo(HashMap<String, String> params) {
        // 用户数据获取，排除首次获取异常
        IDataStorage dataStorage = MainApp.getData();
        User user = dataStorage.load(User.class, "User");
        try {
            JSONObject jo = new JSONObject(user.userInfo);
            params.put("user_id", jo.getString("id"));
            params.put("token", jo.getString("token"));
        } catch (JSONException e) {
            params.put("user_id", "-1");
            params.put("token", "-1");
        }
    }

    /**
     * 为链接添加user_id和token
     *
     * @param currentUrl
     * @return
     */
    public static String urlAddUserInfo(String currentUrl) {
        // url中的链接部分
        String routerPart = UrlAnalysis.UrlPage(currentUrl);
        // url中的参数部分
        LinkedHashMap<String, String> valuePart = UrlAnalysis.URLRequest(currentUrl);
        // 参数部分添加用户数据
        IDataStorage dataStorage = MainApp.getData();
        User user = dataStorage.load(User.class, "User");
        try {
            JSONObject jo = new JSONObject(user.userInfo);
            valuePart.put("user_id", jo.getString("id"));
            valuePart.put("token", jo.getString("token"));
        } catch (JSONException e) {
            valuePart.put("user_id", "-1");
            valuePart.put("token", "-1");
        }
        return UrlAnalysis.UrlMosaic(routerPart, valuePart);
    }

    /**
     * 为链接添加user_id,token和另一参数
     *
     * @param currentUrl
     * @return
     */
    public static String urlAddThreeInfo(String currentUrl) {
        // url中的链接部分
        String routerPart = UrlAnalysis.UrlPage(currentUrl);
        // url中的参数部分
        LinkedHashMap<String, String> valuePart = UrlAnalysis.URLRequest(currentUrl);
        // 用户数据获取
        User user = MainApp.getData().load(User.class, "User");
        try {
            JSONObject jo = new JSONObject(user.userInfo);
            valuePart.put("user_id", jo.getString("id"));
            valuePart.put("token", jo.getString("token"));
            valuePart.put("column_id", "-1");
        } catch (JSONException e) {
            valuePart.put("user_id", "-1");
            valuePart.put("token", "-1");
            valuePart.put("column_id", "-1");
        }
        return UrlAnalysis.UrlMosaic(routerPart, valuePart);
    }

    /**
     * 获取应用当前版本号（作为参数传递）
     *
     * @param context
     * @return
     */
    public static String paramAddVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取应用当前版本名（作为参数传递）
     *
     * @param context
     * @return
     */
    public static String paramAddVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
