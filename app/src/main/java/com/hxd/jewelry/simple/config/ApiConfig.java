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

    // 老版本
    public static String MOCK_OLD_ROOT = "file:///android_asset/old/index.html?to=";
    // 新版本
    public static String MOCK_NEW_ROOT = "file:///android_asset/new/index.html";

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
    public static String InformationApi = MOCK_OLD_ROOT + "88FD2414-513D-4CC2-8C31-D47C6CD56EAB";

    // 我的珠宝故事
    public static String MyStoryApi = MOCK_OLD_ROOT + "70321595-0A87-4EFF-9C8E-F3B5CAB50D7E";
    // 我的消息
    public static String MessageApi = MOCK_OLD_ROOT + "CE4A28FE-EC38-448D-891E-408C907B722E";
    // 在线客服
    public static String OnlineCustomerServiceApi = MOCK_OLD_ROOT + "";

    // 定制珠宝
    public static String CustomJewelryApi = MOCK_OLD_ROOT + "6B27CC5D-1539-4A6F-835D-13FDEAFD3462";
    // 个性搭配
    public static String PersonalityMatchApi = MOCK_OLD_ROOT + "56D19101-0973-44CD-9912-D7D0FA767D8D";
    // 维修保养
    public static String MaintenanceApi = MOCK_OLD_ROOT + "AA8693FF-0196-413B-802F-D5BE77168DD8";
    // AR试戴
    public static String ARTryOnApi = MOCK_OLD_ROOT + "25F20B0A-614F-4372-9058-76CFD67A0E6A";
    // 寻找同款
    public static String LookingForTheSameParagraphApi = MOCK_OLD_ROOT + "25471034-3F71-4E6B-AD4D-4E832252A4D9";
    // 明星代言
    public static String CelebrityEndorsementsApi = MOCK_OLD_ROOT + "72325E0A-C0AC-469D-A918-9980B1403EB5";
    // 进店有礼
    public static String EnterTheStoreApi = MOCK_OLD_ROOT + "519B795C-BB31-44CB-AB3E-A82B07D2C04D";
    // 礼品卡
    public static String GiftCardApi = MOCK_OLD_ROOT + "A7FD51DE-D53B-4DCD-AA48-7D605E861DC8";
    // 活动招募
    public static String EnentRecruitmentApi = MOCK_OLD_ROOT + "409ADBFD-B616-45BA-B478-53060263072F";
    // 珠宝故事
    public static String JewelryStoryApi = MOCK_OLD_ROOT + "06FFBA09-C435-4E67-8804-FCBB98A3C992";
    // AR情书
    public static String ARLoveLetterApi = MOCK_OLD_ROOT + "4B8F1B62-BC8B-42CC-946D-A7510C9A8323";
    // 告白录音
    public static String ConfessionRecordingApi = MOCK_OLD_ROOT + "6FA8B4C2-25A7-4D31-9524-AD2A24CAE74F";

    // 验证结果
//    public static String VerifyResultApi = MOCK_OLD_ROOT + "D8D267CF-7A18-4641-8745-1F7AE38FAD93";
    public static String VerifyResultApi = MOCK_NEW_ROOT;
    // 输入证书编号验证
    public static String InputVerifyApi = MOCK_OLD_ROOT + "A286A2EE-817C-4103-8325-89B3D35A0F13";

    // 发布珠宝故事
    public static String PublishJewelryStoryApi = MOCK_OLD_ROOT + "2280004D-D09A-4589-9F9D-596C0422944B";

}
