package com.hxd.jewelry.simple.ui.main;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.adapters.HomeFunctionGridAdapter;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.utils.ScreenUtil;
import com.hxd.jewelry.simple.utils.UrlAnalysis;
import com.hxd.jewelry.simple.utils.loader.PicassoImageLoader;
import com.hxd.jewelry.simple.view.SelfGridView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@RouterActivity("home")
public class HomeActivity extends BaseActivity {

    @BindView(R.id.jewelry_banner)
    Banner jewelryBanner;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.home_function_grid)
    SelfGridView homeFunctionGrid;
    @BindView(R.id.tv_go_verify)
    TextView tvGoVerify;

    // 适配数据(标题)
    private String[] titleList = {
            "定制珠宝",
            "个性搭配",
            "维修保养",
            "AR试戴",
            "寻找同款",
            "明星代言",
            "进店有礼",
            "礼品卡",
            "活动招募",
            "珠宝故事",
            "AR情书",
            "告白录音"};

    // 适配数据(图片)
    private int[] imageList = {
            R.drawable.icon_home_01,
            R.drawable.icon_home_02,
            R.drawable.icon_home_03,
            R.drawable.icon_home_04,
            R.drawable.icon_home_05,
            R.drawable.icon_home_06,
            R.drawable.icon_home_07,
            R.drawable.icon_home_08,
            R.drawable.icon_home_09,
            R.drawable.icon_home_10,
            R.drawable.icon_home_11,
            R.drawable.icon_home_12
    };

    // 功能模块数据
    String[] urlList = {
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.CustomJewelryApi + "&title=定制珠宝",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.PersonalityMatchApi + "&title=个性搭配",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.MaintenanceApi + "&title=维修保养",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.ARTryOnApi + "&title=AR试戴",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.LookingForTheSameParagraphApi + "&title=寻找同款",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.CelebrityEndorsementsApi + "&title=明星代言",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.EnterTheStoreApi + "&title=进店有礼",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.GiftCardApi + "&title=礼品卡",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.EnentRecruitmentApi + "&title=活动招募",
            AppConfig.ROUTER_TOTAL_HEAD + "story",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.ARLoveLetterApi + "&title=AR情书",
            AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.ConfessionRecordingApi + "&title=告白录音"
    };

    @Override
    protected int setLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置Banner数据
        initBanner();
        // 功能导航
        initFunctionGrid(titleList);
    }

    /**
     * 动态设置Banner宽高
     */
    private void initBanner() {
        // 屏幕宽度
        int screenWidth = ScreenUtil.getScreenWidth(this);
        // 动态设置Banner控件宽高
        ViewGroup.LayoutParams _params = jewelryBanner.getLayoutParams();
        _params.height = (int) (screenWidth * 410f / 750);
        _params.width = screenWidth;
        jewelryBanner.setLayoutParams(_params);

        // 初始化数据
        initBanner(AppConfig.BANNER_URLS);
    }

    /**
     * 首页Banner数据处理
     */
    private void initBanner(final String[] bannerUrls) {

        if (bannerUrls == null)
            return;
        // Banner携带数据
        List<String> imageUrls = new ArrayList<>(Arrays.asList(bannerUrls));

        // 设置banner样式,圆形指示器(无标题)
        jewelryBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        // 设置图片加载器
        jewelryBanner.setImageLoader(new PicassoImageLoader());
        // 设置图片集合
        jewelryBanner.setImages(imageUrls);
        // 设置banner动画效果1
        jewelryBanner.setBannerAnimation(Transformer.Default);
        // 设置自动轮播，默认为true
        jewelryBanner.isAutoPlay(true);
        // 设置指示器位置,居中（当banner模式中有指示器时）
        jewelryBanner.setIndicatorGravity(BannerConfig.CENTER);
        // Banner设置方法全部调用完毕时最后调用
        jewelryBanner.start();

    }

    /**
     * 功能模块GridView数据处理
     * 设置活动导航
     */
    private void initFunctionGrid(String[] titleList) {

        final int length = titleList.length;
        final int num = length % 3;

        /**
         * 首页功能模块点击事件
         */
        AdapterView.OnItemClickListener functionGridClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (num != 0) {
                    if (i < (adapterView.getCount() - (3 - num))) {
                        //Toast.makeText(HomeActivity.this, "第" + i + "个=URL==>" + jsonList.get(i).getString("link_url"), Toast.LENGTH_SHORT).show();
                        String url = urlList[i];
                        if (url.trim().equals("")) {
                            return;
                        }
                        String schemePart = "";
                        if (urlList[i].contains(":")) {
                            schemePart = url.substring(0, url.indexOf(":"));//截取url中的scheme部分 如"http","https","nbw"
                        }
                        String routerPart = UrlAnalysis.UrlPage(url);//url中的链接部分 如"http://www.rubulls.com"
                        Map<String, String> valuePart = UrlAnalysis.URLRequest(url);//url中的参数部分
                        if (schemePart.equals(AppConfig.ROUTER_HEAD)) {
                            Router.startActivity(HomeActivity.this, urlList[i]);
                        } else {
                            Router.startActivity(HomeActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + urlList[i].replace("&", "%26"));
                        }
                    }
                } else {
                    String url = urlList[i];
                    if (url.trim().equals("")) {
                        return;
                    }
                    String schemePart = "";
                    if (urlList[i].contains(":")) {
                        schemePart = url.substring(0, url.indexOf(":"));//截取url中的scheme部分 如"http","https","nbw"
                    }
                    String routerPart = UrlAnalysis.UrlPage(url);//url中的链接部分 如"http://www.rubulls.com"
                    Map<String, String> valuePart = UrlAnalysis.URLRequest(url);//url中的参数部分
                    if (schemePart.equals(AppConfig.ROUTER_HEAD)) {
                        Router.startActivity(HomeActivity.this, urlList[i]);
                    } else {
                        Router.startActivity(HomeActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + urlList[i].replace("&", "%26"));
                    }
                }

            }
        };

        HomeFunctionGridAdapter _functionAdapter = new HomeFunctionGridAdapter(this, titleList, imageList);
        homeFunctionGrid.setAdapter(_functionAdapter);
        homeFunctionGrid.setOnItemClickListener(functionGridClick);

        // 强制关闭自定义ListView抢占焦点问题
        homeFunctionGrid.setFocusable(false);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            EventBus.getDefault().post(EventConfig.EVENT_PRESS_BACK);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.tv_go_verify)
    public void onViewClicked() {
        EventBus.getDefault().post(EventConfig.EVENT_GO_TO_VERIFICATION_ACTIVITY);
    }
}
