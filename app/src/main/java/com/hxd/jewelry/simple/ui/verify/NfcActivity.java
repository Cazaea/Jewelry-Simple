package com.hxd.jewelry.simple.ui.verify;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.gyf.barlibrary.ImmersionBar;
import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.utils.LogcatUtil;
import com.hxd.jewelry.simple.utils.NfcVUtil;
import com.hxd.jewelry.simple.utils.ScreenUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.view.CustomDialog;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.thejoyrun.router.RouterField;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cazaea on 2017/9/12.
 */

@RouterActivity("nfc")
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NfcActivity extends BaseActivity {

    @BindView(R.id.check_iv_back)
    ImageView checkIvBack;
    @BindView(R.id.check_iv_reset)
    ImageView checkIvReset;

    @BindView(R.id.iv_nfc_anim)
    ImageView ivNfcAnim;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists;
    private NfcV nfcv = null;
    public static String[][] TECHLISTS;
    public static IntentFilter[] FILTERS;

    // 沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    // 加载等待弹框
    private CustomDialog customDialog;
    // 提示弹框
    private SweetAlertDialog dialog_success;
    private SweetAlertDialog dialog_failed;

    public static boolean isContinue = true;

    // 提示声音
    private MediaPlayer mediaPlayer;

    // 活动id
    @RouterField("id")
    String id;

    String action;

    static {
        try {
            TECHLISTS = new String[][]{{IsoDep.class.getName()},
                    {NfcV.class.getName()}, {NfcF.class.getName()},
                    {NfcA.class.getName()},};

            FILTERS = new IntentFilter[]{new IntentFilter(
                    NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_verify_nfc;
    }

    @SuppressLint("NewApi")
    @Override
    protected void initView() {
        super.initView();
        // 沉浸式状态栏
        if (isImmersionBarEnabled())
            initImmersionBar();
        dialog_success = new SweetAlertDialog(NfcActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        dialog_failed = new SweetAlertDialog(NfcActivity.this, SweetAlertDialog.ERROR_TYPE);
        dialog_failed.setTitleText("验证失败！");
        // 动态设置Image宽高
        initImage();
        // 动画开启
        Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            ivNfcAnim.startAnimation(circle_anim);  // 开始动画
        }

        mediaPlayer = MediaPlayer.create(NfcActivity.this, R.raw.verify_failure);
    }

    /**
     * 动态设置Image宽高
     */
    private void initImage() {
        // 屏幕宽度
        int screenWidth = ScreenUtil.getScreenWidth(this);
        // 动态设置Banner控件宽高
        ViewGroup.LayoutParams _params = ivNfcAnim.getLayoutParams();
        _params.height = (int) (screenWidth * 0.65f);
        _params.width = (int) (screenWidth * 0.65f);
        ivNfcAnim.setLayoutParams(_params);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        action = getIntent().getAction();
        id = getIntent().getStringExtra("id");

        customDialog = new CustomDialog(this, R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading_dialog, null);
        TextView warn = view.findViewById(R.id.tv_loading_dialog);
        warn.setText("签到中...");

//        Glide.with(this).load(R.drawable.sign_nfc).into(signGif);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = manager.getDefaultAdapter();
        if (nfcAdapter == null) {
            Toast.makeText(this, "此手机设备不具备NFC功能", Toast.LENGTH_LONG).show();
            NfcActivity.this.finish();
        } else if (!nfcAdapter.isEnabled()) {
            new AlertDialog.Builder(NfcActivity.this)
                    .setTitle("提示")
                    // 设置对话框标题
                    .setMessage("NFC功能未开启，请在系统设置中先启用NFC功能")
                    // 设置显示的内容
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {// 添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 确定按钮的响应事件
                                    startActivity(new Intent("android.settings.NFC_SETTINGS"));
                                }
                            })
                    .setNegativeButton("返回",
                            new DialogInterface.OnClickListener() {// 添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 关闭页面
                                    finish();
                                }
                            }).show();// 在按键响应事件中显示此对话框
        }
        new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mTechLists = new String[][]{new String[]{NfcV.class.getName()}, new String[]{NfcA.class.getName()}};

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

    @OnClick(R.id.check_iv_back)
    public void onCheckIvBackClicked() {
        finish();
    }

    @OnClick(R.id.check_iv_reset)
    public void onCheckIvResetClicked() {
        finish();
    }

    @SuppressLint("NewApi")
    @Override
    public void onNewIntent(Intent intent) {

        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        final Tag tag = (Tag) p;
        final NfcV nfcv = NfcV.get(tag);
        if (nfcv != null) {
            // return VicinityCard.load(nfcv, res);
            if (intent.getAction().trim().equals("android.nfc.action.TECH_DISCOVERED")) {
                try {
                    nfcv.connect();
                    NfcVUtil mNfcVUtil = new NfcVUtil(nfcv);
                    String result_UID = mNfcVUtil.getUID_MT();
                    // e08104004899cf3a
                    // e0810400489a7891
                    // e081040048996f3a
                    // e08104004899e734
                    // e0040150621d82da

                    // 验证成功
                    if (isContinue(result_UID)) {
                        // 验证结果成功页面
                        Router.startActivity(NfcActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.VerifyResultApi + "&title=" + "验证结果");
                    } else {
                        // 验证结果失败页面, 开始播放提示音
//                        ToastUtil.showLongToast(NfcActivity.this,"验证失败！");
                        dialog_failed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog_failed.dismiss();
                                mediaPlayer.start();
                            }
                        },1000);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean isContinue(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            uid = uid.trim();
            LogcatUtil.d(uid);
        } else {
            return false;
        }
        return uid.equals("e08104004899cf3a") || uid.equals("e0810400489a7891") || uid.equals("e081040048996f3a") || uid.equals("e08104004899e734") || uid.equals("e0040150621d82da");
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, FILTERS,
                    mTechLists);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();

        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (nfcv != null && nfcv.isConnected())
            try {
                nfcv.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        // 释放媒体
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

}
