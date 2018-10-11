package com.hxd.jewelry.simple.utils;

/**
 * 随机数相关工具
 *
 * @author Cazaea
 * @time 2017/icon_home_11/28 icon_home_10:40
 * @mail wistorm@sina.com
 */

public class RandomUtil {

    /**
     * 随机生成指定位数的字符串(包含小写字母、大写字母、数字)
     *
     * @param length 字符串位数
     */
    public static String randomString(int length) {
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    /**
     * 随机生成 num位数字字符数组
     *
     * @param num 字符数组位数
     * @return
     */
    public static char[] randomArray(int num) {
        String chars = "0123456789";
        char[] rands = new char[num];
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * 10);
            rands[i] = chars.charAt(rand);
        }
        return rands;
    }

}
