package com.hxd.jewelry.simple.ui.home;

import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 验证结果
 */
@RouterActivity("write_story")
public class WriteStoryActivity extends BaseActivity {

    @BindView(R.id.story_toolbar)
    Toolbar storyToolbar;
    @BindView(R.id.btn_write_story)
    Button btnWriteStory;

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_write_story;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色色字体
        setStatusBarMode(false, storyToolbar, R.color.colorWhite);
    }

    @OnClick(R.id.btn_write_story)
    public void onViewClicked() {
        // 验证结果
        Router.startActivity(WriteStoryActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.PublishJewelryStoryApi + "&title=" + "发布珠宝故事");
    }
}
