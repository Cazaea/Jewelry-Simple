package com.hxd.jewelry.simple.ui.main;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

@RouterActivity("mine")
public class MineActivity extends BaseActivity {

    @BindView(R.id.mine_iv_setting)
    ImageView mineIvSetting;
    @BindView(R.id.mine_riv_head_pic)
    RoundedImageView mineRivHeadPic;
    @BindView(R.id.mine_nick)
    TextView mineNick;
    @BindView(R.id.ll_jewelry_story)
    LinearLayout llJewelryStory;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.ll_online)
    LinearLayout llOnline;
    @BindView(R.id.mine_swipe)
    SwipeRefreshLayout mineSwipe;

    String nick_name;

    private PhotoPopupWindow mPhotoPopupWindow;

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";
    public static Uri mImageUri;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        // 刷新数据
        if (message.equals(EventConfig.EVENT_REFRESH_MINE_INFO)) {
//            doRefresh();
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initView() {
        super.initView();
        // 设置深色字体
        setStatusBarMode(true, null, R.color.colorMine);
        // 设置下拉刷新
        mineSwipe.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mineSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                doRefresh();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mineSwipe.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        // 设置名称
        mineNick.setText("茴香豆公司");
        // 加载视图
        Picasso.with(MineActivity.this).load(R.drawable.image_head).error(R.drawable.pic_default_head).into(mineRivHeadPic);

    }

    /**
     * 再按一次退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            EventBus.getDefault().post(EventConfig.EVENT_PRESS_BACK);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.mine_iv_setting)
    public void onMineIvSettingClicked() {
        Router.startActivity(MineActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "settings");
    }

    @OnClick(R.id.mine_riv_head_pic)
    public void onMineRivHeadPicClicked() {
        // 修改头像
        changeHeadPic();
    }

    @OnClick(R.id.mine_nick)
    public void onMineNickClicked() {
        // 修改昵称
        Router.startActivity(MineActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "modify_nick?nick_name=" + nick_name);
    }

    @OnClick(R.id.ll_jewelry_story)
    public void onLlJewelryStoryClicked() {
        // 我的珠宝故事
        Router.startActivity(MineActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.MyStoryApi + "&title=" + "我的珠宝故事");
    }

    @OnClick(R.id.ll_message)
    public void onLlMessageClicked() {
        // 我的消息
        Router.startActivity(MineActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.MessageApi + "&title=" + "我的消息");
    }

    @OnClick(R.id.ll_online)
    public void onLlOnlineClicked() {
        // 在线客服
//        Router.startActivity(MineActivity.this, AppConfig.ROUTER_TOTAL_HEAD + "web?url=" + ApiConfig.LoveLetterApi + "&title=" + "我的情书");
    }

//    /**
//     * 数据刷新操作
//     */
//    private void doRefresh() {
//        mineSwipe.setRefreshing(true);
//        HashMap<String, String> params = new HashMap<>();
//        // 获取保存的 user 数据
//        IDataStorage dataStorage = DataStorageFactory.getInstance(
//                getApplicationContext(), DataStorageFactory.TYPE_DATABASE);
//        User user = dataStorage.load(User.class, "User");
//        try {
//            JSONObject jsonObject = new JSONObject(user.userInfo);
//            params.put("user_id", jsonObject.getString("id"));
//            params.put("token", jsonObject.getString("token"));
//            GetInfoTask task = new GetInfoTask();
//            task.execute(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void changeHeadPic() {
        mPhotoPopupWindow = new PhotoPopupWindow(MineActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 权限申请
                if (ContextCompat.checkSelfPermission(MineActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MineActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，需要在这里写申请权限的代码
                    requestPermission();
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
                if (ContextCompat.checkSelfPermission(MineActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，需要在这里写申请权限的代码
                    requestPermission();
                } else {
                    // 如果权限已经申请过，直接进行图片选择
                    mPhotoPopupWindow.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        getParent().startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } else {
                        ToastUtil.showShortToast(MineActivity.this, "未找到图片查看器");
                    }
                }
            }
        });
        View rootView = LayoutInflater.from(MineActivity.this)
                .inflate(R.layout.activity_settings, null);
        mPhotoPopupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

            return HttpUtil.postHttpJPG(MineActivity.this, ApiConfig.UploadHeadPicApi,
                    strParams, fileParams);
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                // 设置到视图
                Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getEncodedPath());
                mineRivHeadPic.setImageBitmap(bitmap);
                ToastUtil.showShortToast(MineActivity.this, result[1].toString());
                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);
            } else {
                ToastUtil.showShortToast(MineActivity.this, result[1].toString());
            }
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
        getParent().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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
                        ToastUtil.showShortToast(MineActivity.this, "未找到图片查看器");
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

        }
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
        intent.setDataAndType(getImageContentUri(MineActivity.this, inputFile), "image/*");
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
            mineRivHeadPic.setImageBitmap(photo);
        }
    }

    /**
     * 获取用户数据
     */
    private class GetInfoTask extends AsyncTask<HashMap<String, String>, Integer, Object[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(HashMap<String, String>... params) {
            try {
                return HttpUtil.postHttp(MineActivity.this, ApiConfig.PersonInfoApi,
                        params[0], HttpUtil.TYPE_FORCE_NETWORK, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            mineSwipe.setRefreshing(false);
            if ((boolean) result[0]) {
                try {
                    JSONObject data = (JSONObject) result[2];

                    IDataStorage dataStorage = DataStorageFactory.getInstance(
                            getApplicationContext(), DataStorageFactory.TYPE_DATABASE);

                    // 保存数据
                    User user = new User();
                    user.userInfo = data.toString();
                    dataStorage.storeOrUpdate(user, "User");

                    String head_pic = data.getString("head_pic");
                    // 保存头像路径，方便在改变头像处设置
                    IconUrl iconUrl = new IconUrl();
                    iconUrl.url = head_pic;
                    dataStorage.storeOrUpdate(iconUrl, "IconUrl");
                    // 加载视图
                    Picasso.with(MineActivity.this).load(R.drawable.image_head).error(R.drawable.pic_default_head).into(mineRivHeadPic);
                    // 用户名设置
                    nick_name = data.getString("nickname");

                    mineNick.setText("茴香豆公司");

                } catch (Exception e) {
                    ToastUtil.showShortToast(MineActivity.this, "数据解析失败");
                }
            } else {
                ToastUtil.showShortToast(MineActivity.this, result[1].toString());
            }
        }
    }

}
