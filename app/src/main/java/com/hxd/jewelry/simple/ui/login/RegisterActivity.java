package com.hxd.jewelry.simple.ui.login;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.CodeConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.Register;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.CheckUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.thejoyrun.router.RouterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Cazaea on 2017/3/14.
 * 找回密码
 */
@RouterActivity("register")
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.et_referrer)
    EditText etReferrer;

    private ProgressDialog progressDialog;

    // 注册信息：账号，验证码，密码
    private String account, code, password;
    // 推荐人手机号
    private String referrer;
    // 填写信息状态
    private boolean phone_status, code_status, pwd_status;

    /**
     * 设置按钮显示隐藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_PHONE_LISTENER)) {
            phone_status = true;
            btnGetCode.setEnabled(true);
            btnGetCode.setClickable(true);
            // 更改验证码背景(如果已经是灰背景，不更改获取验证码按钮)
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
            btnRegister.setEnabled(true);
            btnRegister.setClickable(true);
            btnRegister.setBackgroundResource(R.drawable.shape_main_button);
        } else {
            // 默认按钮不可点击
            btnRegister.setEnabled(false);
            btnRegister.setClickable(false);
            btnRegister.setBackgroundResource(R.drawable.shape_gray_button);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色色字体
        setStatusBarMode(false, mToolbar, R.color.colorWhite);

        // 监听输入内容
        etAccount.addTextChangedListener(phoneTextWatcher);
        etCode.addTextChangedListener(codeTextWatcher);
        etPassword.addTextChangedListener(pwdTextWatcher);

        // 获取验证码按钮
        btnGetCode.setEnabled(false);
        btnGetCode.setClickable(false);
        btnGetCode.setTextColor(getResources().getColor(R.color.colorLightGray));
        btnGetCode.setBackgroundResource(R.drawable.shape_get_code_button_normal);

        // 默认按钮不可点击
        btnRegister.setEnabled(false);
        btnRegister.setClickable(false);
        btnRegister.setBackgroundResource(R.drawable.shape_gray_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonTimer != null) {
            buttonTimer.cancel();
            buttonTimer = null;
        }
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClicked() {

//        if (password.length() < 6) {
//            ToastUtil.showShortToast(this, "请设置6位以上更安全的密码");
//            return;
//        }

        // 推荐人信息
        referrer = etReferrer.getText().toString().trim();
        if (!TextUtils.isEmpty(referrer)) {
            if (referrer.length() != 11) {
                ToastUtil.showShortToast(this, "请输入正确的推荐人手机号");
                return;
            } else if (!CheckUtil.isPhoneNumberType(referrer)) {
                ToastUtil.showShortToast(this, "请输入正确的推荐人手机号");
                return;
            }
        }

        // 注册账号
        runRegisterTask();
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
    private void runRegisterTask() {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("code", code);
        params.put("password", password);
        // TODO 添加参数
        params.put("referee_tel", referrer);
        RegisterTask task = new RegisterTask();
        task.execute(params);
    }

    /**
     * 注册账号网络任务
     */
    private class RegisterTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(RegisterActivity.this, "提示",
                        "正在注册账号", true, false);
            } else if (progressDialog.isShowing()) {
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在注册账号");
            }
            progressDialog.show();
        }

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtil.postHttp(RegisterActivity.this, ApiConfig.RegisterApi,
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
                // 提示信息
                ToastUtil.showShortToast(RegisterActivity.this, result[1].toString());
                // 传递信息，自动登录
                Register register = new Register();
                register.mAccount = account;
                register.mPassword = password;
                EventBus.getDefault().post(register);
                // 延时1.2S关闭Toast,然后关闭页面
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.cancelToast();
                        // 发消息，关闭上一个LoginActivity
//                        EventBus.getDefault().post(EventConfig.EVENT_CLOSE_REPEATED_LOGIN_ACTIVITY);
                        // 携带信息跳转登录页面
//                        Router.startActivity(RegisterActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "login?account=" + account);
                        RegisterActivity.this.finish();
                    }
                }, 1000);
            } else {
                ToastUtil.showShortToast(RegisterActivity.this, result[1].toString());
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
        params.put("phone", account);
        params.put("type", CodeConfig.CODE_TYPE_REGISTER);
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
                return HttpUtil.postHttp(RegisterActivity.this, ApiConfig.AuthCodeApi,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                ToastUtil.showShortToast(RegisterActivity.this, result[1].toString());
            } else {
                ToastUtil.showShortToast(RegisterActivity.this, result[1].toString());
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
                account = temp.toString().trim();
                if (CheckUtil.isPhoneNumberType(account)) {
                    phone_status = true;
                    EventBus.getDefault().post(EventConfig.EVENT_PHONE_LISTENER);
                } else {
                    ToastUtil.showShortToast(RegisterActivity.this, "请输入正确的手机号");
                }
            } else if (temp.length() == 14 && "+86".equals(temp.toString().substring(0, 2))) {
                account = temp.toString().trim().substring(3);
                if (CheckUtil.isPhoneNumberType(account)) {
                    phone_status = true;
                    EventBus.getDefault().post(EventConfig.EVENT_PHONE_LISTENER);
                } else {
                    ToastUtil.showShortToast(RegisterActivity.this, "请输入正确的手机号");
                }
            } else {
                EventBus.getDefault().post(EventConfig.EVENT_PHONE_INFO_ERROR);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // 输入内容
            String content = s.toString().trim();
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
                password = temp.toString().trim();
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
