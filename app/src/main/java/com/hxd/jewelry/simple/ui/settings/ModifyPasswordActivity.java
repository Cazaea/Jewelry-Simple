package com.hxd.jewelry.simple.ui.settings;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.thejoyrun.router.RouterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

@RouterActivity("modify_password")
public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.modify_pwd_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.change_pwd_et_old)
    EditText mChangePwdEtOld;
    @BindView(R.id.change_pwd_et_new)
    EditText mChangePwdEtNew;
    @BindView(R.id.change_pwd_submit)
    Button mChangePwdSubmit;

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色字体
        setStatusBarMode(false, mToolbar, R.color.colorWhite);
    }

    /**
     * Click to change pwd
     */
    @OnClick(R.id.change_pwd_submit)
    public void modifyPassword() {
        String oldPwd = mChangePwdEtOld.getText().toString();
        String newPwd = mChangePwdEtNew.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("old_password", oldPwd);
        params.put("new_password", newPwd);

        ChangePwdTask task = new ChangePwdTask();
        task.execute(params);
    }

    /**
     * 监听返回按钮
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 修改密码的网络任务
     */
    private class ChangePwdTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                String url = ApiConfig.ModifyPwdApi;
                try {
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = dataStorage.load(User.class, "User");
                    JSONObject jsonObject = new JSONObject(user.userInfo);
                    String user_id = jsonObject.getString("id");
                    String token = jsonObject.getString("token");
                    //url = url + "?user_id=" + user_id + "&token=" + token;
                    params[0].put("user_id", user_id);
                    params[0].put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return HttpUtil.postHttp(ModifyPasswordActivity.this, url,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {

            if ((boolean) result[0]) {
                // 提示信息
                ToastUtil.showShortToast(ModifyPasswordActivity.this, result[1].toString());
                // 延时1.2S关闭Toast,然后关闭页面
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.cancelToast();
                        ModifyPasswordActivity.this.finish();
                    }
                }, 1000);
            }else{
                ToastUtil.showShortToast(ModifyPasswordActivity.this, result[1].toString());
            }
        }
    }

}
