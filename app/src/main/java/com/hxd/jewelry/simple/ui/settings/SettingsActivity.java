package com.hxd.jewelry.simple.ui.settings;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.hxd.jewelry.simple.BuildConfig;
import com.hxd.jewelry.simple.R;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.data.IconUrl;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.view.PhotoPopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.thejoyrun.router.Router;
import com.thejoyrun.router.RouterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

@RouterActivity("settings")
public class SettingsActivity extends BaseActivity {

    @BindView(R.id.settings_toolbar)
    Toolbar mSettingsToolbar;
    @BindView(R.id.settings_ll_change_pwd)
    LinearLayout mSettingsLlChangePwd;
    @BindView(R.id.settings_ll_change_nick)
    LinearLayout mSettingsLlChangeNick;
    @BindView(R.id.setting_tv_nick)
    TextView settingTvNick;
    @BindView(R.id.settings_iv_icon)
    RoundedImageView mSettingsIvIcon;
    @BindView(R.id.settings_ll_change_icon)
    LinearLayout mSettingsLlChangeIcon;
    @BindView(R.id.settings_ll_clear)
    LinearLayout mSettingsLlClear;
    @BindView(R.id.settings_ll_exit)
    LinearLayout mSettingsLlExit;

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";

    private Uri mImageUri;

    private PhotoPopupWindow mPhotoPopupWindow;

    private SweetAlertDialog cDialog;

    private User user;

    public static String nick_name = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if (message.equals(EventConfig.EVENT_MODIFY_NICK_SUCCESS)) {
            settingTvNick.setText(nick_name);
        }
    }

    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    protected int setLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色字体
        setStatusBarMode(false, mSettingsToolbar, R.color.colorWhite);
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取保存的昵称数据
        IDataStorage dataStorage = DataStorageFactory.getInstance(
                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
        user = dataStorage.load(User.class, "User");

        try {
            JSONObject user_json = new JSONObject(user.userInfo);
            nick_name = user_json.getString("nickname");
            settingTvNick.setText(nick_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        settingTvNick.setText("茴香豆公司");
        Picasso.with(this).load(R.drawable.image_head).into(mSettingsIvIcon);

        // 获取保存的 user 数据
        IconUrl iconUrl = dataStorage.load(IconUrl.class, "IconUrl");
        if (iconUrl != null && iconUrl.url != null) {
            Picasso.with(this).load(R.drawable.image_head).into(mSettingsIvIcon);
        }

    }

    /**
     * 处理回调结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 小图切割
                case REQUEST_SMALL_IMAGE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                // 大图切割
                case REQUEST_BIG_IMAGE_CUTTING:
                    // 获取保存的 user 数据
                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                    User user = dataStorage.load(User.class, "User");
                    try {
                        JSONObject jsonObject = new JSONObject(user.userInfo);
                        String user_id = jsonObject.getString("id");
                        String token = jsonObject.getString("token");
                        // 上传至服务器
                        ChangeIconTask task = new ChangeIconTask();
                        // user_id & token
                        HashMap<String, String> strParams = new HashMap<>();
                        strParams.put("user_id", user_id);
                        strParams.put("token", token);
                        // file
                        HashMap<String, File> fileParams = new HashMap<>();
                        File file = new File(mImageUri.getEncodedPath());
                        fileParams.put("file", file);
                        task.execute(new HashMap[]{strParams, fileParams});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                // 相册选取
                case REQUEST_IMAGE_GET:
                    try {
                        // startSmallPhotoZoom(data.getData());
                        startBigPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    // startSmallPhotoZoom(Uri.fromFile(temp));
                    startBigPhotoZoom(temp);
                    break;
            }
        }
    }

    /**
     * 处理权限回调结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } else {
                        ToastUtil.showShortToast(SettingsActivity.this, "未找到图片查看器");
                    }
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPopupWindow.dismiss();
                    imageCapture();
                } else {
                    mPhotoPopupWindow.dismiss();
                }
                break;
            case 400:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*
                    String path = Environment.getExternalStorageDirectory().getPath()
                            + "/ARResource";
                    deleteFolderFile(path, false);
                    cDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "清理完毕", Toast.LENGTH_SHORT).show();
                    */
//                    String path = null;
                    try {
//                        path = DownloadUtil.get().isExistDir(ARConfig.filepackageName,SettingsActivity.this);
//                        deleteFolderFile(path, false);
//                        CacheDataManager.clearAllCache(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cDialog.dismiss();
                    ToastUtil.showShortToast(SettingsActivity.this, "清理完毕");
                } else {
                    cDialog.dismiss();
                }
        }
    }

    /**
     * 所有 LinearLayout 的点击事件
     */
    @OnClick({R.id.settings_ll_change_pwd, R.id.settings_ll_change_nick, R.id.settings_ll_change_icon, R.id.settings_ll_clear, R.id.settings_ll_exit})
    public void linearClick(LinearLayout linearLayout) {
        switch (linearLayout.getId()) {

            // 修改密码
            case R.id.settings_ll_change_pwd:
                Router.startActivity(SettingsActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "modify_password");
                break;
            // 修改昵称
            case R.id.settings_ll_change_nick:
                // 修改昵称
                Router.startActivity(SettingsActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "modify_nick?nick_name=" + "茴香豆公司");
                break;
            // 更换头像
            case R.id.settings_ll_change_icon:
                mPhotoPopupWindow = new PhotoPopupWindow(SettingsActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(SettingsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                        } else {
                            // 权限已经申请，直接拍照
                            mPhotoPopupWindow.dismiss();
                            imageCapture();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            //权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                        } else {
                            // 如果权限已经申请过，直接进行图片选择
                            mPhotoPopupWindow.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            // 判断系统中是否有处理该 Intent 的 Activity
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_IMAGE_GET);
                            } else {
                                ToastUtil.showShortToast(SettingsActivity.this, "未找到图片查看器");
                            }
                        }
                    }
                });
                View rootView = LayoutInflater.from(SettingsActivity.this)
                        .inflate(R.layout.activity_settings, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            // 清理缓存
            case R.id.settings_ll_clear:
                cDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("确认")
                        .setCancelText("取消");
                cDialog.setTitleText("确定要清除缓存吗？");
                cDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // 权限申请
                        if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 400);
                        } else if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，需要在这里写申请权限的代码
                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 400);
                        } else {
//                            String path = null;
                            try {
//                                path = DownloadUtil.get().isExistDir(ARConfig.filepackageName,SettingsActivity.this);
//                                deleteFolderFile(path, false);
//                                CacheDataManager.clearAllCache(getApplicationContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cDialog.dismiss();
                            ToastUtil.showShortToast(SettingsActivity.this, "清理完毕");
                        }
                    }
                });
                cDialog.show();
                break;
            // 退出账号
            case R.id.settings_ll_exit:
                SweetAlertDialog eDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setConfirmText("确认")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                EventBus.getDefault().post(EventConfig.EVENT_LOGOUT);
                                IDataStorage dataStorage = DataStorageFactory.getInstance(
                                        getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
                                User user = dataStorage.load(User.class, "User");
                                // 将用户数据设置为默认数
                                user.userInfo = User.defaultInfo;
                                user.fromAccount = true;
                                user.hasLogin = false;
                                dataStorage.storeOrUpdate(user, "User");
                                SettingsActivity.this.finish();
                            }
                        }).setCancelText("取消");
                eDialog.setTitleText("确定要退出当前账号吗？");
                eDialog.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 反注册EventBus
        EventBus.getDefault().unregister(this);

        String path = Environment.getExternalStorageDirectory().getPath()
                + "/bigIcon";
        deleteFolderFile(path, false);
    }

    /**
     * 判断系统及拍照
     */
    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /*
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());
            pictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            */
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID.concat(".fileProvider"), pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    /**
     * 删除指定目录下文件及目录
     */
    private void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) { // 如果是文件，删除
                        file.delete();
                    } else { // 目录
                        if (file.listFiles().length == 0) { // 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 小图模式切割图片
     * 此方式直接返回截图后的 bitmap，由于内存的限制，返回的图片会比较小
     */
    public void startSmallPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(File inputFile) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            /*
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.hxd.niubowang.fileProvider", file);
            } else {
                imageUri = Uri.fromFile(file);
            }
            */
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(SettingsActivity.this, inputFile), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public void startBigPhotoZoom(Uri uri) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            /*
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.hxd.niubowang.fileProvider", file);
            } else {
                imageUri = Uri.fromFile(file);
            }
            */
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 小图模式中，保存图片后，设置到视图中
     * 将图片保存设置到视图中
     */
    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            // 创建 icon 文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String storage = Environment.getExternalStorageDirectory().getPath();
                File dirFile = new File(storage + "/smallIcon");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.e("TAG", "文件夹创建失败");
                    } else {
                        Log.e("TAG", "文件夹创建成功");
                    }
                }
                File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
                // 保存图片
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 在视图中显示图片
            mSettingsIvIcon.setImageBitmap(photo);
        }
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
     * 上传头像的网络任务
     */
    private class ChangeIconTask extends AsyncTask<HashMap<String, Object>, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(HashMap<String, Object>... params) {
            HashMap<String, String> strParams = new HashMap<>();
            HashMap<String, File> fileParams = new HashMap<>();
            // user_id & token
            strParams.put("user_id", params[0].get("user_id").toString());
            strParams.put("token", params[0].get("token").toString());
            // file
            File file = new File(params[1].get("file").toString());
            fileParams.put("file", file);

            return HttpUtil.postHttpJPG(SettingsActivity.this, ApiConfig.UploadHeadPicApi,
                    strParams, fileParams);
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                // 设置到视图
                Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getEncodedPath());
                mSettingsIvIcon.setImageBitmap(bitmap);
                ToastUtil.showShortToast(SettingsActivity.this, result[1].toString());
                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);
            } else {
                ToastUtil.showShortToast(SettingsActivity.this, result[1].toString());
            }
        }
    }

}
