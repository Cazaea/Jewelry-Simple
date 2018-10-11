package com.hxd.jewelry.simple.ui.login;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.CodeConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.CheckUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作 者： Cazaea
 * 日 期： 2018/4/27
 * 邮 箱： wistorm@sina.com
 * 找回密码
 */
@RouterActivity("recover")
public class RecoverActivity extends BaseActivity {

    @BindView(R.id.recover_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.btn_recover)
    Button btnRecover;

    // 提示框
    private ProgressDialog progressDialog;

    // 注册信息：账号，验证码，密码
    private String phone, code, newPassword;
    // 填写信息状态
    private boolean phone_status, code_status, pwd_status;

    /**
     * 按钮状态动态获取
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_PHONE_LISTENER)) {
            phone_status = true;
            btnGetCode.setEnabled(true);
            btnGetCode.setClickable(true);
            btnGetCode.setBackground(getResources().getDrawable(R.drawable.shape_get_code_button));
            btnGetCode.setTextColor(getResources().getColor(R.color.colorMainBlue));
        } else if (message.equals(EventConfig.EVENT_CODE_LISTENER)) {
            code_status = true;
        } else if (message.equals(EventConfig.EVENT_PASSWORD_LISTENER)) {
            pwd_status = true;
        }
        if (message.equals(EventConfig.EVENT_PHONE_INFO_ERROR)) {
            phone_status = false;
            btnGetCode.setEnabled(false);
            btnGetCode.setClickable(false);
            btnGetCode.setBackground(getResources().getDrawable(R.drawable.shape_get_code_button_normal));
            btnGetCode.setTextColor(getResources().getColor(R.color.colorLightGray));
        } else if (message.equals(EventConfig.EVENT_CODE_INFO_ERROR)) {
            code_status = false;
        } else if (message.equals(EventConfig.EVENT_PASSWORD_INFO_ERROR)) {
            pwd_status = false;
        }

        // 如果都有内容，按钮可点击
        if (phone_status && code_status && pwd_status) {
            btnRecover.setEnabled(true);
            btnRecover.setClickable(true);
            btnRecover.setBackgroundResource(R.drawable.shape_main_button);
        } else {
            // 默认按钮不可点击
            btnRecover.setEnabled(false);
            btnRecover.setClickable(false);
            btnRecover.setBackgroundResource(R.drawable.gray_button_background);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_recover;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色字体
        setStatusBarMode(false, mToolbar, R.color.colorWhite);

        // 监听输入内容
        etAccount.addTextChangedListener(phoneTextWatcher);
        etCode.addTextChangedListener(codeTextWatcher);
        etNewPassword.addTextChangedListener(pwdTextWatcher);

        // 获取验证码按钮
        btnGetCode.setEnabled(false);
        btnGetCode.setClickable(false);
        btnGetCode.setTextColor(getResources().getColor(R.color.colorLightGray));
        btnGetCode.setBackgroundResource(R.drawable.shape_get_code_button_normal);

        // 默认按钮不可点击
        btnRecover.setEnabled(false);
        btnRecover.setClickable(false);
        btnRecover.setBackgroundResource(R.drawable.shape_gray_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonTimer != null) {
            buttonTimer.cancel();
            buttonTimer = null;
        }
    }

    @OnClick(R.id.btn_recover)
    public void onRegisterClicked() {

        if (newPassword.length() < 6) {
            ToastUtil.showShortToast(this, "请设置6位以上更安全的密码");
            return;
        }
        // 找回账号（重置密码）
        runRecoverTask();
    }

    /**
     * 获取验证码
     */
    @OnClick(R.id.btn_get_code)
    public void onGetCodeClicked() {
        runGetCodeTask();
    }

    /**
     * 运行注册任务
     */
    private void runRecoverTask() {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", phone);
        params.put("code", code);
        params.put("password", newPassword);
        RecoverTask task = new RecoverTask();
        task.execute(params);
    }

    /**
     * 找回密码网络任务
     */
    private class RecoverTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(RecoverActivity.this, "提示",
                        "正在重置密码", true, false);
            } else if (progressDialog.isShowing()) {
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在重置密码");
            }
            progressDialog.show();
        }

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtil.postHttp(RecoverActivity.this, ApiConfig.FindPwdApi,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                ToastUtil.showShortToast(RecoverActivity.this, result[1].toString());
                // 延时1.0S关闭Toast,然后关闭页面
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.cancelToast();
                        // 发消息，关闭上一个LoginActivity
                        EventBus.getDefault().post(EventConfig.EVENT_CLOSE_REPEATED_LOGIN_ACTIVITY);
                        Router.startActivity(RecoverActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "login?account=" + phone);
                        RecoverActivity.this.finish();
                    }
                }, 1000);
            } else {
                ToastUtil.showShortToast(RecoverActivity.this, result[1].toString());
            }

        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    /**
     * 运行获取验证码
     */
    private void runGetCodeTask() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("type", CodeConfig.CODE_TYPE_RECOVER);
        GetCodeTask task = new GetCodeTask();
        task.execute(params);
        // 倒计时开始
        buttonTimer.start();
        // 手机号不可改变
        etAccount.setFocusable(false);
        etAccount.setFocusableInTouchMode(false);
    }

    /**
     * 获取验证码网络请求
     */
    private class GetCodeTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtil.postHttp(RecoverActivity.this, ApiConfig.AuthCodeApi,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                ToastUtil.showShortToast(RecoverActivity.this, result[1].toString());
            } else {
                ToastUtil.showShortToast(RecoverActivity.this, result[1].toString());
            }

        }

    }

    /**
     * 倒计时按钮控制
     */
    CountDownTimer buttonTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnGetCode.setEnabled(false);
            btnGetCode.setClickable(false);
            btnGetCode.setBackground(getResources().getDrawable(R.drawable.shape_get_code_button_normal));
            btnGetCode.setTextColor(getResources().getColor(R.color.colorLightGray));
            btnGetCode.setTextSize(12f);
            btnGetCode.setText(millisUntilFinished / 1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            btnGetCode.setEnabled(true);
            btnGetCode.setClickable(true);
            btnGetCode.setBackground(getResources().getDrawable(R.drawable.shape_get_code_button));
            btnGetCode.setTextColor(getResources().getColor(R.color.colorMainBlue));
            btnGetCode.setText("重新获取验证码");
            // 倒计时走完，可更新手机号
            etAccount.setFocusableInTouchMode(true);
            etAccount.setFocusable(true);
        }
    };

    /**
     * 手机号输入框监听
     */
    TextWatcher phoneTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence temp, int start, int before, int count) {

            if (temp.length() == 11) {
                phone = temp.toString().trim();
                if (CheckUtil.isPhoneNumberType(phone)) {
                    phone_status = true;
                    EventBus.getDefault().post(EventConfig.EVENT_PHONE_LISTENER);
                } else {
                    ToastUtil.showShortToast(RecoverActivity.this, "请输入正确的手机号");
                }
            } else {
                EventBus.getDefault().post(EventConfig.EVENT_PHONE_INFO_ERROR);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * 验证码输入框监听
     */
    TextWatcher codeTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //将输入的内容实时显示
        }

        @Override
        public void onTextChanged(CharSequence temp, int start, int before, int count) {

            if (temp.length() == 6) {
                code = temp.toString().trim();
                EventBus.getDefault().post(EventConfig.EVENT_CODE_LISTENER);
            } else {
                EventBus.getDefault().post(EventConfig.EVENT_CODE_INFO_ERROR);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * 密码输入框监听
     */
    TextWatcher pwdTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //将输入的内容实时显示
        }

        @Override
        public void onTextChanged(CharSequence temp, int start, int before, int count) {
            if (temp.length() > 0) {
                newPassword = temp.toString().trim();
                EventBus.getDefault().post(EventConfig.EVENT_PASSWORD_LISTENER);
            } else {
                EventBus.getDefault().post(EventConfig.EVENT_PASSWORD_INFO_ERROR);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
