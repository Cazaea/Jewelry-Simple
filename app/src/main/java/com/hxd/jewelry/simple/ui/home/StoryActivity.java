package com.hxd.jewelry.simple.ui.home;

import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.AppConfig;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 珠宝故事
 */
@RouterActivity("story")
public class StoryActivity extends BaseActivity {

    @BindView(R.id.story_toolbar)
    Toolbar storyToolbar;
    @BindView(R.id.btn_my_jewelry_story)
    Button btnMyJewelryStory;

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_home_story;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色色字体
        setStatusBarMode(false, storyToolbar, R.color.colorWhite);
    }

    @OnClick(R.id.btn_my_jewelry_story)
    public void onViewClicked() {
        // 验证结果
        Router.startActivity(StoryActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "write_story");
    }
}
