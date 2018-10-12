package com.hxd.jewelry.simple.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.google.zxing.client.android.AutoScannerView;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import com.hxd.jewelry.simple.R;

/**
 * 模仿微信的扫描界面
 */
@RouterActivity("scan")
public class ScanActivity extends BaseCaptureActivity implements View.OnClickListener{

    private static final String TAG = ScanActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private AutoScannerView autoScannerView;

    private ImageView checkIvBack;
    private ImageView checkIvReset;

    // 沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_scan);
        // 沉浸式状态栏
        if (isImmersionBarEnabled())
            initImmersionBar();
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        autoScannerView = (AutoScannerView) findViewById(R.id.auto_scanner_view);
        checkIvBack = (ImageView) findViewById(R.id.check_iv_back);
        checkIvReset = (ImageView) findViewById(R.id.check_iv_reset);
        checkIvBack.setOnClickListener(this);
        checkIvReset.setOnClickListener(this);
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 初始化状态栏对象
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        // 所有子类都将继承这些相同的属性
        mImmersionBar.init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScannerView.setCameraManager(cameraManager);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.preview_view) : surfaceView;
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        playBeepSoundAndVibrate(true, true);
        if (!TextUtils.isEmpty(rawResult.getText()))
            // 验证结果
            Router.startActivity(ScanActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.VerifyResultApi + "&title=" + "验证结果");
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_iv_back:
                finish();
                break;
            case R.id.check_iv_reset:
                reScan();
                break;
        }
    }
}
