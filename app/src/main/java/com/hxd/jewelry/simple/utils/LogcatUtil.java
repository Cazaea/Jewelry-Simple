package com.hxd.jewelry.simple.utils;

import com.hxd.jewelry.simple.config.AppConfig;
import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * 清晰明了的Log日志打印
 * Created by Cazaea on 2016/12/13.
 */

public class LogcatUtil {

    public static void d(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.d(msg);
    }

    public static void e(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.e(msg);
    }

    public static void w(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.w(msg);
    }

    public static void v(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.v(msg);
    }

    public static void i(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.i(msg);
    }

    // 红色突出显示
    public static void wtf(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.wtf(msg);
    }

    // 打印json数据
    public static void json(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.json(msg);
    }

    // 打印xml文件
    public static void xml(String msg) {
        if (AppConfig.DEBUG_MODE)
            Logger.xml(msg);
    }

    /*public static void saveLog() {
        try {
            java.lang.Process p = Runtime.getRuntime().exec("logcat");
            final InputStream is = p.getInputStream();
            new Thread() {
                @Override
                public void run() {
                    FileOutputStream os = null;
                    try {
                        String fillPath = isExistDir(Environment.getExternalStorageDirectory() + "/Lease/file/log.txt");
                        os = new FileOutputStream(fillPath);
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while (-1 != (len = is.read(buf))) {
                            os.write(buf, 0, len);
                            os.flush();
                        }
                    } catch (Exception e) {
                        LogcatUtil.d("read logcat process failed. message: " + e.getMessage());
                    } finally {
                        if (null != os) {
                            try {
                                os.close();
                                os = null;
                            } catch (IOException e) {
                                // Do nothing
                            }
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            LogcatUtil.d("open logcat process failed. message: " + e.getMessage());
        }
    }

    *//**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     *//*
    public static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory().getPath(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }*/

}
