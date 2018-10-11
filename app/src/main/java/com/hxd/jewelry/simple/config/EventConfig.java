package com.hxd.jewelry.simple.config;

/**
 * 用于配置EventBus的事件
 *
 * @author Cazaea
 * @time 2017/icon_home_11/3 14:48
 * @mail wistorm@sina.com
 */

public class EventConfig {

    /**
     * 登录状态处理
     */
    // 退出登录
    public static String EVENT_LOGOUT = "EVENT_LOGOUT";
    // 登陆成功
    public static String EVENT_LOGIN_SUCCESS = "EVENT_LOGIN_SUCCESS";
    // 未登录及退出
    public static String EVENT_EXIT_WITHOUT_LOGIN = "EVENT_EXIT_WITHOUT_LOGIN";

    /**
     * 单项功能处理
     */
    // 返回键处理
    public static String EVENT_PRESS_BACK = "EVENT_PRESS_BACK";
    // 首页申请权限
    public static String EVENT_REQUEST_PERMISSION = "EVENT_REQUEST_PERMISSION";
    // 弹窗广告消息
    public static String EVENT_ADVERTISING_INFO = "EVENT_ADVERTISING_INFO";
    // 弹出实名认证页面
    public static String EVENT_SHOW_VERIFIED_PAGE = "EVENT_SHOW_VERIFIED_PAGE";

    /**
     * Web页面按钮显示隐藏
     */
    // 签到说明
    public static String EVENT_WEB_BUTTON_SIGN = "EVENT_WEB_BUTTON_SIGN";
    // 分享
    public static String EVENT_WEB_BUTTON_SHARE = "EVENT_WEB_BUTTON_SHARE";

    /**
     * 滑动隐藏Main底部导航栏
     */
    // 向上滑动
    public static String EVENT_SCROLL_TO_UP = "EVENT_SCROLL_TO_UP";
    // 向下滑动
    public static String EVENT_SCROLL_TO_DOWN = "EVENT_SCROLL_TO_DOWN";

    /**
     * 所有刷新操作
     */
    // 刷新Web页面
    public static String EVENT_REFRESH_WEB_INFO = "EVENT_REFRESH_WEB_INFO";
    // 刷新个人信息
    public static String EVENT_REFRESH_MINE_INFO = "EVENT_REFRESH_MINE_INFO";
    // 刷新银行卡信息
    public static String EVENT_REFRESH_CARD_INFO = "EVENT_REFRESH_CARD_INFO";
    // 刷新开发者模式
    public static String EVENT_REFRESH_DEVELOP_INFO = "EVENT_REFRESH_DEVELOP_INFO";
    // 刷新购买、赎回，提现所获取的User信息
    public static String EVENT_REFRESH_WBR_INFO = "EVENT_REFRESH_WBR_INFO";
    // 购买成功，刷新产品购买进度
    public static String EVENT_REFRESH_PRODUCT_INFO = "EVENT_REFRESH_PRODUCT_INFO";

    // 修改昵称成功
    public static String EVENT_MODIFY_NICK_SUCCESS = "EVENT_MODIFY_NICK_SUCCESS";

    /**
     * 页面输入监听，改变按钮状态
     */
    // 银行卡信息监听
    public static String EVENT_BANK_CARD_LISTENER = "EVENT_BANK_CARD_LISTENER";
    // 开户行信息监听
    public static String EVENT_ACCOUNT_BANK_LISTENER = "EVENT_ACCOUNT_BANK_LISTENER";
    // 姓名信息监听
    public static String EVENT_NAME_LISTENER = "EVENT_NAME_LISTENER";
    // 手机号信息监听
    public static String EVENT_PHONE_LISTENER = "EVENT_PHONE_LISTENER";
    // 验证码信息监听
    public static String EVENT_CODE_LISTENER = "EVENT_CODE_LISTENER";
    // 密码信息监听
    public static String EVENT_PASSWORD_LISTENER = "EVENT_PASSWORD_LISTENER";
    // 密码二次输入信息监听
    public static String EVENT_PASSWORD_ONCE_LISTENER = "EVENT_PASSWORD_ONCE_LISTENER";
    // 身份证输入监听
    public static String EVENT_ID_CARD_LISTENER = "EVENT_ID_CARD_LISTENER";

    // 银行卡信息错误
    public static String EVENT_BANK_CARD_INFO_ERROR = "EVENT_BANK_CARD_INFO_ERROR";
    // 开户行信息错误
    public static String EVENT_ACCOUNT_BANK_INFO_ERROR = "EVENT_ACCOUNT_BANK_INFO_ERROR";
    // 姓名信息错误
    public static String EVENT_NAME_INFO_ERROR = "EVENT_NAME_INFO_ERROR";
    // 手机号信息错误
    public static String EVENT_PHONE_INFO_ERROR = "EVENT_PHONE_INFO_ERROR";
    // 验证码信息错误
    public static String EVENT_CODE_INFO_ERROR = "EVENT_CODE_INFO_ERROR";
    // 密码信息错误
    public static String EVENT_PASSWORD_INFO_ERROR = "EVENT_PASSWORD_INFO_ERROR";
    // 密码二次输入信息错误
    public static String EVENT_PASSWORD_ONCE_INFO_ERROR = "EVENT_PASSWORD_ONCE_INFO_ERROR";
    // 身份证信息输入错误
    public static String EVENT_ID_CARD_ERROR = "EVENT_ID_CARD_ERROR";

    /**
     * 所有关闭页面处理
     */
    // 重启应用:关闭已打开页面
    public static String EVENT_CLOSE_ALL_OPENED_ACTIVITY = "EVENT_CLOSE_ALL_OPENED_ACTIVITY";
    // 强制关闭重复登录页面
    public static String EVENT_CLOSE_REPEATED_LOGIN_ACTIVITY = "EVENT_CLOSE_REPEATED_LOGIN_ACTIVITY";
    // 提现,购买,赎回成功关闭页面
    public static String EVENT_WBR_SUCCESS_CLOSE_ACTIVITY = "EVENT_WBR_SUCCESS_CLOSE_ACTIVITY";
    // 充值成功关闭页面
    public static String EVENT_RECHARGE_SUCCESS_CLOSE_ACTIVITY = "EVENT_RECHARGE_SUCCESS_CLOSE_ACTIVITY";
    // 认证成功，关闭认证页
    public static String EVENT_VERIFIED_SUCCESS_CLOSE_ACTIVITY = "EVENT_VERIFIED_SUCCESS_CLOSE_ACTIVITY";

    /**
     * 跳转页面处理
     */
    public static String EVENT_GO_TO_VERIFICATION_ACTIVITY = "EVENT_GO_TO_VERIFICATION_ACTIVITY";

}
