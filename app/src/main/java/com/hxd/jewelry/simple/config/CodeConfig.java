package com.hxd.jewelry.simple.config;

/**
 * 作 者： Cazaea
 * 日 期： 2018/4/27
 * 邮 箱： wistorm@sina.com
 */
public class CodeConfig {
    /**
     * 验证码类型
     * 0 注册 1修改密码 2找回密码 3付款 4绑定银行卡
     */
    public static final String CODE_TYPE_REGISTER = "0";
    public static final String CODE_TYPE_MODIFY = "1";
    public static final String CODE_TYPE_RECOVER = "2";
    public static final String CODE_TYPE_PAY = "3";
    public static final String CODE_TYPE_BIND = "4";

    /**
     * 带返回值跳转，RequestCode类型
     * 默认初始值100
     */
    private static final int ROOT_CODE = 100;
    // 权限申请
    public static final int CODE_REQUEST_PERMISION = ROOT_CODE + 1;

    // 相机拍照
    public static final int CODE_TAKE_CAMERA = ROOT_CODE + 2;
    // 图片选择
    public static final int CODE_SELECT_IMAGE = ROOT_CODE + 3;

}
