package com.hxd.jewelry.simple.ui.home;

import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 编号验证
 */
@RouterActivity("input")
public class InputActivity extends BaseActivity {

    @BindView(R.id.input_toolbar)
    Toolbar inputToolbar;
    @BindView(R.id.et_certificate_no)
    EditText etCertificateNo;
    @BindView(R.id.et_security_code)
    EditText etSecurityCode;
    @BindView(R.id.btn_sure)
    Button btnSure;

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_verify_input;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色色字体
        setStatusBarMode(false, inputToolbar, R.color.colorWhite);
    }

    @OnClick(R.id.btn_sure)
    public void onViewClicked() {
        // 验证结果
        Router.startActivity(InputActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.VerifyResultApi + "&title=" + "验证结果");
    }
}
