package com.hxd.jewelry.simple.http;

import android.content.Context;

import com.hxd.jewelry.simple.app.MainApp;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.utils.DeviceUtil;
import com.hxd.jewelry.simple.utils.LogcatUtil;
import com.hxd.jewelry.simple.utils.ParamUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xiaofei.library.datastorage.IDataStorage;

/**
 * Created by Cazaea on 2017/6/9.
 * 此类为http请求基础类
 */

public class HttpUtil {

    private static final CacheControl FORCE_NETWORK = new CacheControl.Builder().noCache().build();
    private static final CacheControl FORCE_CACHE = new CacheControl.Builder()
            .onlyIfCached()
            .maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS)
            .build();

    public static final String TYPE_FORCE_CACHE = "TYPE_FORCE_CACHE";
    public static final String TYPE_FORCE_NETWORK = "TYPE_FORCE_NETWORK";
    public static final String TYPE_CACHE_CONTROL = "TYPE_CACHE_CONTROL";

    // 刷新数据存储
    private static IDataStorage mDataStorage;

    /**
     * 此静态方法为一般post方法，因在AsyncTask中调用
     *
     * @param context       调用场景context
     * @param url           请求地址
     * @param params        请求参数
     * @param cache_type    缓存类型(TYPE_FORCE_CACHE,TYPE_FORCE_NETWORK,TYPE_CACHE_CONTROL),如果为TYPE_CACHE_CONTROL 需要填写缓存时间
     * @param cache_seconds 缓存时间，单位秒
     * @return 返回对象数组 obj[0]:bool,成功、失败；obj[1]:String,提示信息；obj[2]：JSONObject/null。
     * 注意：==> 如果状态不成功，obj[2]：返回状态码
     */
    public static Object[] postHttp(Context context, String url, HashMap<String, String> params, String cache_type, int cache_seconds) {

        // 参数中添加平台信息、版本号、设备编号、鉴权参数
        addInfo(context, params);

        try {
            // 缓存文件夹
            File cacheFile = new File(String.valueOf(context.getExternalCacheDir()), "cache");
            // 缓存大小为50M
            int cacheSize = 50 * 1024 * 1024;
            // 创建缓存对象
            final Cache cache = new Cache(cacheFile, cacheSize);

            // 创建网络连接
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(cache)
                    .build();

            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet())
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            RequestBody formBody = formBodyBuilder.build();

            CacheControl cacheControl = null;
            switch (cache_type) {
                case TYPE_CACHE_CONTROL:
                    cacheControl = new CacheControl.Builder()
                            .maxAge(cache_seconds, TimeUnit.SECONDS)
                            .build();
                    break;
                case TYPE_FORCE_CACHE:
                    cacheControl = FORCE_CACHE;
                    break;
                case TYPE_FORCE_NETWORK:
                    cacheControl = FORCE_NETWORK;
                    break;

            }

            assert cacheControl != null;
            Request request = new Request.Builder()
                    .cacheControl(cacheControl)
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();

            String result = Objects.requireNonNull(response.body()).string();
            JSONObject jo = new JSONObject(result);
            int status_code = jo.getInt("code");
            if (status_code == 1000) {
                return new Object[]{true, jo.getString("msg"), jo.isNull("data") ? new JSONObject() : jo.get("data")};
            } else {
                // 每次启动应用调用接口都判断一次登录状态是否过期
                if (status_code == 1102) {
                    // 重置用户数据
                    resetUser();
                    // 登录状态过期, 跳回首页
                    EventBus.getDefault().post(EventConfig.EVENT_LOGOUT);
                    return new Object[]{false, "用户不存在，请重新登录", status_code};
                }
                // 如果接口请求不成功，将obj[2]放入错误code返回
                return new Object[]{false, jo.getString("msg"), status_code};
            }
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[]{false, "数据解析失败", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
                return new Object[]{false, "无法访问服务器", null};
            } else {
                return new Object[]{false, "当前无法访问网络", null};
            }
        }
    }

    /**
     * @param context 调用场景context
     * @param url     请求地址
     * @param params  请求参数
     * @param image   要上传的png图片
     * @return 返回对象数组 obj[0]:bool,成功、失败；obj[1]:String,提示信息；obj[2]：JSONObject/null，返回数据
     */
    public static Object[] postHttpJPG(Context context, String
            url, HashMap<String, String> params, HashMap<String, File> image) {

        // 参数中添加平台信息、版本号、设备编号、鉴权参数
        addInfo(context, params);

        try {
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : params.entrySet())
                multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
            for (Map.Entry<String, File> entry : image.entrySet()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), entry.getValue());
                multipartBody.addFormDataPart(entry.getKey(), entry.getKey() + ".jpg", fileBody);
            }
            RequestBody requestBody = multipartBody.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String result = Objects.requireNonNull(response.body()).string();
            JSONObject jo = new JSONObject(result);
            // 状态码为1000时，接口跑通，否则接口请求异常
            boolean status = (jo.getInt("code") == 1000);
            return new Object[]{status, jo.getString("msg"), jo.get("data")};
        } catch (Exception e) {
            if (e instanceof JSONException) {
                return new Object[]{false, "JSON解析失败", null};
            } else if (NetWorkUtil.isNetworkConnected(context)) {
                return new Object[]{false, "无法访问服务器", null};
            } else {
                return new Object[]{false, "当前无法访问网络", null};
            }
        }
    }

    /**
     * 参数中添加平台信息、版本号、设备编号、鉴权参数
     *
     * @param context
     * @param params
     */
    private static void addInfo(Context context, HashMap<String, String> params) {
        // 接口统一添加User信息
        ParamUtil.paramAddUserInfo(params);
        // 添加平台信息
        params.put("platform", "Android");
        // 添加版本号
        params.put("version", ParamUtil.paramAddVersionName(context));
        // 添加设备编号
        params.put("device_uid", DeviceUtil.getUniqueId(context));
        // 添加鉴权参数
        try {
            params.put("sign", getSignature(params));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数字签名工具
     * 1qaz2wsx
     *
     * @param postPairs
     * @return
     * @throws IOException
     */
    private static String getSignature(HashMap<String, String> postPairs) throws IOException {

        StringBuilder baseString = new StringBuilder();
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(postPairs.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            // 升序排序
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }

        });

        for (Map.Entry<String, String> mapping : list) {
            baseString.append(mapping.getKey()).append("=").append(mapping.getValue());
            // 查看传入参数信息
            LogcatUtil.d(mapping.getKey() + ":-->" + mapping.getValue());
        }
        baseString.append("1qaz2wsx");

        byte[] bytes;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(baseString.toString().getBytes("UTF-8"));
        } catch (GeneralSecurityException ex) {
            throw new IOException(ex);
        }

        StringBuilder sign = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString();
    }

    /**
     * 给User传入 默认数据
     */
    private static void resetUser() {
        if (mDataStorage == null)
            mDataStorage = MainApp.getData();
        User user = mDataStorage.load(User.class, "User");
        // 如果没有存入数据，先初始化User数据为未登录状态
        if (user != null) {
            user = new User();
            user.userInfo = User.defaultInfo;
            user.hasLogin = false;
            user.fromAccount = true;
            mDataStorage.storeOrUpdate(user, "User");
        }
    }

}
