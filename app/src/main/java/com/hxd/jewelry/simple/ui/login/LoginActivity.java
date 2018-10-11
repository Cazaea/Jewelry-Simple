package com.hxd.jewelry.simple.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.app.MainApp;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.Develop;
import com.hxd.jewelry.simple.data.Register;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.utils.VersionUtil;
import com.hxd.jewelry.simple.view.LongPressTextView;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;
import com.thejoyrun.router.RouterField;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import xiaofei.library.datastorage.IDataStorage;

@RouterActivity("login")
public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_toolbar)
    Toolbar loginToolbar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_find_password)
    TextView tvFindPassword;
    @BindView(R.id.login_tv_version)
    LongPressTextView loginTvVersion;

    @RouterField("account")
    String account;
    @RouterField("password")
    String password;
    @RouterField("url")
    String url = null;      // 是不是网页拦截登录Url
    @RouterField("type")
    String type;            // 区分是不是注册后的自动登录:

    private LoginTask task;
    private SweetAlertDialog progressDialog;

    // 应用版本号
    private String versionName;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_REFRESH_DEVELOP_INFO)) {
            // 更新开发者数据
            refreshDevelopInfo();
        }
        if (message.equals(EventConfig.EVENT_CLOSE_REPEATED_LOGIN_ACTIVITY)) {
            // SingTop启动模式失效，强制关闭重复页面
            this.finish();
        }
    }

    // 处理自动登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Register pRegister) {
        if (pRegister != null) {
            etAccount.setText(pRegister.mAccount);
            etPassword.setText(pRegister.mPassword);
            type = "register";
            // 自动登录
            login();
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色色字体
        setStatusBarMode(false, loginToolbar, R.color.colorWhite);
        // 获取版本号信息
        versionName = VersionUtil.getVersionName(LoginActivity.this);
        // 设置版本号
        refreshDevelopInfo();
    }

    @Override
    protected void initData() {
        super.initData();
        // 加载用户信息
        if (!TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }
        if (!TextUtils.isEmpty(password)) {
            etPassword.setText(password);
        }
        // 网页Url信息
        if (url == null)
            url = getIntent().getStringExtra("url");
        // type区分
        if (type == null)
            type = getIntent().getStringExtra("type") == null ? "login" : getIntent().getStringExtra("type");

    }

    @SuppressLint("SetTextI18n")
    private void refreshDevelopInfo() {

        // 获取开发者模式数据
        IDataStorage dataStorage = MainApp.getData();
        Develop developInfo = dataStorage.load(Develop.class, "Develop");

        if (developInfo != null) {
            if (developInfo.isUse) {
                // 开发者模式已打开
                loginTvVersion.setText("开发者模式" + "\n" + "Version:" + versionName);
                // 单击进入开发者页面
                loginTvVersion.setSingleClickListener(new LongPressTextView.SingleClickListener() {
                    @Override
                    public void onSingleClick() {
                        Router.startActivity(LoginActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "develop");
                    }
                });
                loginTvVersion.setLongPressListener(null);
            } else {
                // 开发者模式已打开
                loginTvVersion.setText("Version:" + versionName);
                // 长按开启开发者模式
                loginTvVersion.setLongPressListener(new LongPressTextView.LongPressListener() {
                    @Override
                    public void onLongPress() {
                        Router.startActivity(LoginActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "develop");
                    }
                });
                loginTvVersion.setSingleClickListener(null);
            }
        }

    }

    /**
     * 登录账户
     */
    private void login() {
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(account)) {
            etAccount.setHintTextColor(Color.RED);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setHintTextColor(Color.RED);
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        task = new LoginTask();
        task.execute(params);
    }

    /**
     * 注册账户
     */
    private void register() {
        Router.startActivity(LoginActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "register");
    }

    /**
     * 恢复密码
     */
    private void recoverPassword() {
        Router.startActivity(LoginActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "recover");
    }

    /**
     * 监听返回按钮
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            exitWithoutLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回键重载
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitWithoutLogin();
            finish();
        }
        return true;
    }

    /**
     * 未登录即退出
     */
    private void exitWithoutLogin() {
        IDataStorage dataStorage = MainApp.getData();
        User user = dataStorage.load(User.class, "User");
        user.fromAccount = true;
        dataStorage.storeOrUpdate(user, "User");
        EventBus.getDefault().post(EventConfig.EVENT_EXIT_WITHOUT_LOGIN);
    }

    @OnClick({R.id.btn_login, R.id.tv_register, R.id.tv_find_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.tv_find_password:
                recoverPassword();
                break;
        }
    }

    private class LoginTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (task != null) {
                                    task.cancel(true);
                                }
                            }
                        });
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("登录中");
            } else {
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("登录中");
            }
            progressDialog.show();
        }

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtil.postHttp(LoginActivity.this, ApiConfig.LoginApi, params[0],
                        HttpUtil.TYPE_FORCE_NETWORK, 0);
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
                try {
                    JSONObject data = (JSONObject) result[2];
                    // 保存数据
                    IDataStorage storage = MainApp.getData();
                    User user = storage.load(User.class, "User");
                    user.userInfo = data.toString();
                    user.fromAccount = false;
                    user.hasLogin = true;
                    storage.storeOrUpdate(user, "User");

                    // 提示登录成功
                    if (!ToastUtil.isShowing())
                        ToastUtil.showShortToast(LoginActivity.this, "登录成功");
                    // 延时500ms关闭页面
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginSuccess();
                            LoginActivity.this.finish();
                        }
                    }, 500);
                } catch (Exception ex) {
                    ToastUtil.showShortToast(LoginActivity.this, "数据解析失败");
                }
            } else {
                ToastUtil.showShortToast(LoginActivity.this, result[1].toString());
            }

        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        /**
         * 登录完成操作
         */
        private void loginSuccess() {

            // 刷新个人页面布局，数据
            EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);

            switch (type) {
                case "register":
                    // TODO 弹出实名认证页面
                    EventBus.getDefault().post(EventConfig.EVENT_SHOW_VERIFIED_PAGE);
                    break;
                case "hot":
                    // TODO 跳转到购买页面
                    Router.startActivity(LoginActivity.this, url);
                    break;
                case "web":
                    // TODO 继续网页操作
                    Intent intent = new Intent();
                    // 把返回数据存入Intent
                    intent.putExtra("url", url);
                    // 设置返回数据
                    LoginActivity.this.setResult(RESULT_OK, intent);
                    break;
                case "login":
                    // TODO 普通登陆成功
                    // 通知Main页面显示个人页面
                    EventBus.getDefault().post(EventConfig.EVENT_LOGIN_SUCCESS);
                    break;
            }

        }
    }

}

