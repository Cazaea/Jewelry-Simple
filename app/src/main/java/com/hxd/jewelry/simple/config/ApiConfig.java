package com.hxd.jewelry.simple.config;

/**
 * @author Cazaea
 * @time 2017/icon_home_11/3 13:26
 * @mail wistorm@sina.com
 */

public class ApiConfig {

    //=========================PathData=========================//
    public static final String PATH_NAME = "Lease";

    //=========================SeverUrl=========================//
    public static String ROOT = "http://139.196.226.118:8012/app";

    // 测试服
    public static String TEST_ROOT = "http://lease.nfc-hxd.com";
    // 正式服
    public static String FORMAL_ROOT = "https://www.haoyu.top";


    public static String MOCK_ROOT = "file:///android_asset/old/index.html?to=";
    public static String NEW_MOCK_ROOT = "file:///android_asset/new/index.html";

    //=========================ApiData=========================//

    // 登录
    public static String LoginApi = ROOT + "/login/check";
    // 注册
    public static String RegisterApi = ROOT + "/login/register";
    // 找回密码
    public static String FindPwdApi = ROOT + "/login/findPassword";
    // 短信验证码
    public static String AuthCodeApi = ROOT + "/login/getCode";
    // 用户信息
    public static String PersonInfoApi = ROOT + "/vmmember/getData";
    // 图片上传
    public static String UploadPicApi = ROOT + "/cmapply/uploadApplyPic";
    // 上传头像
    public static String UploadHeadPicApi = ROOT + "/vmmember/uploadMemberPic";
    // 修改密码
    public static String ModifyPwdApi = ROOT + "/vmmember/changePassword";
    // 修改昵称
    public static String ModifyNickApi = ROOT + "/vmmember/rename";

    // 资讯
    public static String InformationApi = MOCK_ROOT + "88FD2414-513D-4CC2-8C31-D47C6CD56EAB";
    // 表白
    public static String ConfessionApi = MOCK_ROOT + "B0AAC377-9424-4938-844D-62F3A9EED7DA";
    // 故事
    public static String StoryApi = MOCK_ROOT + "06FFBA09-C435-4E67-8804-FCBB98A3C992";

    // 我的情书
    public static String LoveLetterApi = MOCK_ROOT + "E9030E6B-529D-4B8F-A88A-48FD997B4E88";
    // 我的珠宝故事
    public static String MyStoryApi = MOCK_ROOT + "70321595-0A87-4EFF-9C8E-F3B5CAB50D7E";
    // 我的消息
    public static String MessageApi = MOCK_ROOT + "CE4A28FE-EC38-448D-891E-408C907B722E";
    // 在线客服
//    public static String LoveLetterApi = MOCK_ROOT + "E9030E6B-529D-4B8F-A88A-48FD997B4E88";

    // 验证结果
//    public static String VerifyResultApi = MOCK_ROOT + "8D073F85-8238-4443-B2D3-225A22DC8AAF";
    public static String VerifyResultApi = NEW_MOCK_ROOT;
    // 输入证书编号验证
    public static String InputVerifyApi = MOCK_ROOT + "A286A2EE-817C-4103-8325-89B3D35A0F13";

}
