package com.hxd.jewelry.simple.ui;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hxd.jewelry.simple.MainActivity;
import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.utils.ScreenUtil;
import com.hxd.jewelry.simple.view.SplashTimerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 启动页
 *
 * @author Cazaea
 * @time 2017/5/9 9:01
 * @mail wistorm@sina.com
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @BindView(R.id.timer_view)
    SplashTimerView splashTimerView;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        super.initView();
        initViewValue();
    }

    @Override
    protected void setListener() {
        super.setListener();
        // 延时跳转主页
        delayEnterMain();
    }

    /**
     * 动态设置View宽高
     */
    private void initViewValue() {
        // 屏幕宽度
        int screenWidth = ScreenUtil.getScreenWidth(this);
        // 动态设置控件宽高
        ViewGroup.LayoutParams _params = ivSplash.getLayoutParams();
        _params.height = (int) (screenWidth * (222f / 568));
        _params.width = screenWidth;
        ivSplash.setLayoutParams(_params);
        // 初始化数据
        ivSplash.setImageDrawable(this.getResources().getDrawable(R.drawable.image_splash));
    }

    /**
     * 延时跳转
     */
    private void delayEnterMain() {

        // 设置倒计时时间为3S
        splashTimerView.setTimeMillis(2000);
        // 设置正向进度
        splashTimerView.setProgressType(SplashTimerView.ProgressType.COUNT);
        // 监听进度条
        splashTimerView.setProgressListener(new SplashTimerView.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                // 进度走完，进入主页
                if (progress == 100) {
                    enterMain();
                }
            }
        });
        // 开始转动
        splashTimerView.start();
    }

    // 进入主界面
    private void enterMain() {
        // 设置
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.timer_view)
    public void onViewClicked() {
        splashTimerView.stop();
        enterMain();
    }

}
