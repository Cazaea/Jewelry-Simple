package com.hxd.jewelry.simple.app;

import android.support.multidex.MultiDexApplication;

import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.data.Config;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.utils.LogcatUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.QbSdk;
import com.thejoyrun.router.Router;

import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

/*
 * @author Cazaea
 * @time 2017/icon_home_11/14 14:41
 * @mail wistorm@sina.com
 *
 *                        ___====-_  _-====___
 *                  _--^^^#####//      \\#####^^^--_
 *               _-^##########// (    ) \\##########^-_
 *              -############//  |\^^/|  \\############-
 *            _/############//   (@::@)   \\############\_
 *           /#############((     \\//     ))#############\
 *          -###############\\    (oo)    //###############-
 *         -#################\\  / VV \  //#################-
 *        -###################\\/      \//###################-
 *       _#/|##########/\######(   /\   )######/\##########|\#_
 *       |/ |#/\#/\#/\/  \#/\##\  |  |  /##/\#/  \/\#/\#/\#| \|
 *       `  |/  V  V  `   V  \#\| |  | |/#/  V   '  V  V  \|  '
 *          `   `  `      `   / | |  | | \   '      '  '   '
 *                           (  | |  | |  )
 *                          __\ | |  | | /__
 *                         (vvv(VVV)(VVV)vvv)
 *
 *                           HERE BE DRAGONS
 *
 */

public class MainApp extends MultiDexApplication {

    private static MainApp sMainApp;
    private IDataStorage mDataStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        // 给app赋值
        sMainApp = this;
        // 初始化Logger打印
        initLogger();
        // 注册路由工具
        initRouter();
        // 初始化x5内核,X5的预加载
        initTBSX5();
        // 初始化存储工具
        initData();
        // 初始化User数据
        initUser();
        // 初始化Config数据
        initConfig();

    }

    /**
     * 静态MainApp对象
     */
    public static MainApp getApp() {
        return sMainApp;
    }

    /**
     * 初始化AndroidDataStorage
     */
    public static IDataStorage getData() {
        return DataStorageFactory.getInstance(getApp(), DataStorageFactory.TYPE_DATABASE);
    }

    /**
     * 初始化Logger打印
     */
    private void initLogger() {
        Logger.t("Jewelry");
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


    /**
     * 初始化路由框架
     */
    private void initRouter() {
        Router.init(AppConfig.ROUTER_HEAD);
        Router.setHttpHost(AppConfig.ROUTER_WEBSITE);
    }

    /**
     * 初始化TBS浏览服务X5内核
     */
    private void initTBSX5() {
        // 非wifi条件下允许下载X5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定是用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean status) {
                // x5内核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败会自动切换到系统内核。
                LogcatUtil.d("TBS浏览服务X5内核是否加载成功-->" + status);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        // X5内核初始化接口
        QbSdk.initX5Environment(getApp(), cb);
    }

    /**
     * 初始化 存储工具
     */
    private void initData() {
        mDataStorage = DataStorageFactory.getInstance(this, DataStorageFactory.TYPE_DATABASE);
    }

    /**
     * 给User传入 默认数据
     */
    private void initUser() {
        User user = mDataStorage.load(User.class, "User");
        // 如果没有存入数据，先初始化User数据为未登录状态
        if (user == null) {
            user = new User();
            user.userInfo = User.defaultInfo;
            user.hasLogin = false;
            user.fromAccount = true;
            mDataStorage.storeOrUpdate(user, "User");
        }
    }

    /**
     * 初始化配置 默认数据
     */
    private void initConfig() {
        Config config = mDataStorage.load(Config.class, "Config");
        // 如果没有存入数据，先初始化Config数据为默认数据
        if (config == null) {
            config = new Config();
            config.mImageList = null;
            config.mAboutUrl = null;
            config.mCustomerUrl = null;
            config.mAgreementUrl = null;
            config.mTradeRecordUrl = null;
            config.mRechargeUrl = null;
            config.mWithdrawUrl = null;
            config.mMessageCenterUrl = null;
            config.mAccountDetailsUrl = null;
            config.mSignInUrl = null;
            config.mRegisterSuccessUrl = null;
            mDataStorage.storeOrUpdate(config, "Config");
        }
    }

}
