package com.hxd.jewelry.simple;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LocalActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.hxd.jewelry.simple.config.ApiConfig;
import com.hxd.jewelry.simple.data.User;
import com.hxd.jewelry.simple.http.HttpUtil;
import com.hxd.jewelry.simple.ui.main.HomeActivity;
import com.hxd.jewelry.simple.ui.main.VerificationActivity;
import com.hxd.jewelry.simple.ui.main.InformationWebActivity;
import com.hxd.jewelry.simple.ui.main.MineActivity;
import com.hxd.jewelry.simple.adapters.PagerAdapter;
import com.hxd.jewelry.simple.base.BaseActivity;
import com.hxd.jewelry.simple.config.AppConfig;
import com.hxd.jewelry.simple.config.EventConfig;
import com.hxd.jewelry.simple.utils.ToastUtil;
import com.hxd.jewelry.simple.utils.helper.BottomNavigationViewHelper;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.thejoyrun.router.RouterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import xiaofei.library.datastorage.DataStorageFactory;
import xiaofei.library.datastorage.IDataStorage;

/**
 * ██████▒      ██  ▄████▄   ██ ▄█▀       ██████╗ ██╗   ██╗ ██████╗
 * ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝
 * ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗
 * ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║
 * ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝
 * ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒       ╚═════╝  ╚═════╝  ╚═════╝
 * ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
 * ░ ░    ░░░ ░ ░ ░        ░ ░░ ░
 * ░     ░ ░      ░  ░
 */
@SuppressLint("Registered")
@RouterActivity("main")
public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    // 控制页面切换
    private LocalActivityManager manager;
    // 底部导航菜单
    private MenuItem menuItem;

    // 沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    /**
     * 通过滑动RecyclerView设置Tab显示隐藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {

        // 滑动隐藏底部导航栏
        if (message.equals(EventConfig.EVENT_SCROLL_TO_UP)) {
            scrollUp();
        } else if (message.equals(EventConfig.EVENT_SCROLL_TO_DOWN)) {
            scrollDown();
        }

        // 未登录即退出操作
        if (message.equals(EventConfig.EVENT_LOGOUT)) {
            viewPager.setCurrentItem(0);
        }

        // 登录成功，进入个人页面
        if (message.equals(EventConfig.EVENT_LOGIN_SUCCESS)) {
            viewPager.setCurrentItem(3);
        }

        // 首页通过按钮进入验证页面
        if (message.equals(EventConfig.EVENT_GO_TO_VERIFICATION_ACTIVITY)) {
            viewPager.setCurrentItem(1);
        }

        // 动态获取权限
        if (message.equals(EventConfig.EVENT_REQUEST_PERMISSION)) {
            // 申请权限
            sharePermissionRequest();
        }

    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        // 沉浸式状态栏
        if (isImmersionBarEnabled())
            initImmersionBar();
        // 初始化控件, 及ViewPager绑定
        initViewPager(savedInstanceState);
        // 初始化底部导航栏
        initNavigation();
        // 添加页面
        addActivities();
        // 注册Bugly更新
        registerBugly();
    }

    /**
     * 注册Bugly版本更新
     */
    private void registerBugly() {
        // 设置更新弹框弹出位置
        Beta.canShowUpgradeActs.add(MainActivity.class);
        // 注册Bugly更新，最后参数是否开启log
        Bugly.init(this, "54c0995137", AppConfig.DEBUG_MODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            // 必须调用该方法，防止内存泄漏
            // 不调用该方法，如果界面bar发生改变，在不关闭app的情况下
            // 退出此界面再进入将记忆最后一次bar改变的状态
            mImmersionBar.destroy();
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

    /**
     * RecyclerView向上滑动
     */
    private void scrollUp() {
        // 隐藏底部导航栏
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    /**
     * RecyclerView向下滑动
     */
    private void scrollDown() {
        // 显示底部导航栏
        bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * 初始化主页面
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager(Bundle savedInstanceState) {

        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        // ViewPager添加切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 禁止ViewPager滑动
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    /**
     * ViewPager绑定，底部导航栏实现
     */
    private void initNavigation() {
        // 默认>3时选中效果会影响ViewPager的滑动切换效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        // 底部导航栏绑定ViewPager
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
//                            case R.id.menu_home:
//                                viewPager.setCurrentItem(0);
//                                break;
                            case R.id.menu_verification:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.menu_information:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.menu_mine:
                                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

    }

    /**
     * 将Activity添加进ViewPager
     */
    private void addActivities() {

        List<View> mViews = new ArrayList<>();
        Intent intent = new Intent();

//        intent.setClass(this, HomeActivity.class);
//        intent.putExtra("id", 1);
//        mViews.add(getView("activity_home", intent));

        intent.setClass(this, VerificationActivity.class);
        intent.putExtra("id", 1);
        mViews.add(getView("activity_information", intent));

        intent.setClass(this, InformationWebActivity.class);
        intent.putExtra("id", 2);
        mViews.add(getView("activity_story", intent));

        intent.setClass(this, MineActivity.class);
        intent.putExtra("id", 3);
        mViews.add(getView("activity_mine", intent));

        PagerAdapter adapter = new PagerAdapter(mViews);
        viewPager.setAdapter(adapter);

    }

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    /**
     * 分享权限申请
     */
    private void sharePermissionRequest() {
        if (Build.VERSION.SDK_INT >= 23) {
            // 所需要的权限组
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS
            };
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
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

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";
    public static Uri mImageUri;

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

            return HttpUtil.postHttpJPG(MainActivity.this, ApiConfig.UploadHeadPicApi,
                    strParams, fileParams);
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ((boolean) result[0]) {
                // 设置到视图
                ToastUtil.showShortToast(MainActivity.this, result[1].toString());
                EventBus.getDefault().post(EventConfig.EVENT_REFRESH_MINE_INFO);
            } else {
                ToastUtil.showShortToast(MainActivity.this, result[1].toString());
            }
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
        intent.setDataAndType(getImageContentUri(MainActivity.this, inputFile), "image/*");
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

}