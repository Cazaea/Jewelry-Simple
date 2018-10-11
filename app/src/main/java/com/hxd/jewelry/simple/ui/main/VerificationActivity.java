package com.hxd.jewelry.simple.ui.main;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.utils.ScreenUtil;
import com.hxd.jewelry.simple.utils.loader.PicassoImageLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@RouterActivity("verification")
public class VerificationActivity extends BaseActivity {

    @BindView(R.id.jewelry_banner)
    Banner jewelryBanner;
    @BindView(R.id.ll_verify_nfc)
    LinearLayout llVerifyNfc;
    @BindView(R.id.ll_verify_scan)
    LinearLayout llVerifyScan;
    @BindView(R.id.ll_verify_input)
    LinearLayout llVerifyInput;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_verification;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置Banner数据
        initBanner();
    }

    @OnClick(R.id.ll_verify_nfc)
    public void onLlVerifyNfcClicked() {
        Router.startActivity(VerificationActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "nfc");

    }

    @OnClick(R.id.ll_verify_scan)
    public void onLlVerifyScanClicked() {
        Router.startActivity(VerificationActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "scan");
    }

    @OnClick(R.id.ll_verify_input)
    public void onLlVerifyInputClicked() {
        Router.startActivity(VerificationActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "input");
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
        // 设置banner动画效果2
//        jewelryBanner.setBannerAnimation(Transformer.Accordion);
        // 设置自动轮播，默认为true
        jewelryBanner.isAutoPlay(true);
        // 设置指示器位置,居中（当banner模式中有指示器时）
        jewelryBanner.setIndicatorGravity(BannerConfig.CENTER);
        // Banner设置方法全部调用完毕时最后调用
        jewelryBanner.start();

    }

}
