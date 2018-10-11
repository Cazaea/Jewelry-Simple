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
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.thejoyrun.router.RouterActivity;
import com.thejoyrun.router.RouterField;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

@RouterActivity("modify_nick")
public class ModifyNickActivity extends BaseActivity {

    @RouterField("nick_name")
    String nick_name = "";
    @BindView(R.id.nick_toolbar)
    Toolbar nickToolbar;
    @BindView(R.id.nick_et_nick)
    EditText nickEtNick;
    @BindView(R.id.nick_bt_submit)
    Button nickBtSubmit;

    String new_nick;

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_modify_nick;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色字体
        setStatusBarMode(false, nickToolbar, R.color.colorWhite);
        nickEtNick.setText(nick_name);
    }

    @OnClick(R.id.nick_bt_submit)
    public void modifyNick() {
        new_nick = nickEtNick.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("name", new_nick);

        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        User user = dataStorage.load(User.class, "User");
        try {
            JSONObject jsonObject = new JSONObject(user.userInfo);
            params.put("user_id", jsonObject.getString("id"));
            params.put("token", jsonObject.getString("token"));
        } catch (Exception e) {

        }
        NotifyTask task = new NotifyTask();
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
    private class NotifyTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                String url = ApiConfig.ModifyNickApi;
                return HttpUtil.postHttp(ModifyNickActivity.this, url,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {

            if ((boolean) result[0]) {
                SettingsActivity.nick_name = new_nick;
                // 提示信息
                ToastUtil.showShortToast(ModifyNickActivity.this, result[1].toString());
                // 延时1.2S关闭Toast,然后关闭页面
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.cancelToast();
                        EventBus.getDefault().post(EventConfig.EVENT_MODIFY_NICK_SUCCESS);
                        ModifyNickActivity.this.finish();
                    }
                }, 1000);
            } else {
                ToastUtil.showShortToast(ModifyNickActivity.this, result[1].toString());
            }
        }
    }

}
