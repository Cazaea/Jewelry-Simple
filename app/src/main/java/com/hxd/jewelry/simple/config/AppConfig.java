package com.hxd.jewelry.simple.config;

/**
 * 用于配置App的常量及开关
 *
 * @author Cazaea
 * @time 2017/icon_home_11/17 14:48
 * @mail wistorm@sina.com
 */

public class AppConfig {

    /**
     * Open or Close
     */
    public static boolean DEBUG_MODE = true;
    public static boolean XG_PUSH_MODE = true;

    /**
     * Constants
     */
    public static final String ROUTER_HEAD = "jewelry";
    public static final String ROUTER_TOTAL_HEAD = "jewelry://";
    public static final String ROUTER_WEBSITE = "www.jewelry.com";

    public static final String PROVIDER_FILE_NAME = "com.hxd.jewelry.demo.fileProvider";

    /**
     * 缓存时间(秒)
     */
    public static int HOME_CACHE_SECONDS = 30;
    public static int RES_CACHE_SECONDS = 30;

    /**
     * 随机横向图
     */
    private static final String HOME_TWO_01 = "http://ojyz0c8un.bkt.clouddn.com/home_two_01.png";
    private static final String HOME_TWO_02 = "http://ojyz0c8un.bkt.clouddn.com/home_two_03.png";
    private static final String HOME_TWO_03 = "http://ojyz0c8un.bkt.clouddn.com/home_two_06.png";
    private static final String HOME_TWO_04 = "http://ojyz0c8un.bkt.clouddn.com/home_two_09.png";
    public static final String[] HOME_TWO_URLS = new String[]{
            HOME_TWO_01,
            HOME_TWO_02,
            HOME_TWO_03,
            HOME_TWO_04
    };

    /**
     * Demo轮播图
     */
    private static final String IMAGE_ONE = "file:///android_asset/old/app/OtherRes/b1.png";
    private static final String IMAGE_TWO = "file:///android_asset/old/app/OtherRes/b2.png";
    private static final String IMAGE_THREE = "file:///android_asset/old/app/OtherRes/b3.png";
    public static final String[] BANNER_URLS = new String[]{
            IMAGE_ONE,
            IMAGE_TWO,
            IMAGE_THREE
    };

}
