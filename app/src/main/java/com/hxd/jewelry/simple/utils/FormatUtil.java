package com.hxd.jewelry.simple.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Cazaea
 * @time 2017/icon_home_11/28 icon_home_10:42
 * @mail wistorm@sina.com
 */
public class FormatUtil {

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

}
