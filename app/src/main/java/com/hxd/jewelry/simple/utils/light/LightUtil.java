package com.hxd.jewelry.simple.utils.light;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import com.hxd.jewelry.simple.utils.LogcatUtil;

import java.util.Objects;

/**
 * 作 者： Cazaea
 * 日 期： 2018/5/15
 * 邮 箱： wistorm@sina.com
 */
public class LightUtil {

    private static Camera mCamera;
    private static Camera.Parameters parameters;
    private static PackageManager mPackageManager;
    private static CameraManager mCameraManager;

    /**
     * 开启闪光灯
     */
    private static void openLight() {
        try {
            mCamera = Camera.open();
            int textureId = 0;
            mCamera.setPreviewTexture(new SurfaceTexture(textureId));
            mCamera.startPreview();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            LogcatUtil.i("打开闪光灯失败：==>" + e.toString());
        }
    }

    /**
     * 开启闪光灯2
     */
    public static void openLight2(Context pContext) {
        mPackageManager = pContext.getPackageManager();
        FeatureInfo[] features = mPackageManager.getSystemAvailableFeatures();
        for (FeatureInfo f : features) {
            // 判断设备是否支持闪光灯
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                if (null == mCamera) {
                    mCamera = Camera.open();
                }
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } else {
                LogcatUtil.e("手机没有手电筒硬件。");
            }

        }
    }

    /**
     * 关闭闪光灯
     */
    public static void closeLight(Context pContext) {
        if (isKitKat()) {
            closeLight();
        } else if (isMarshmallow()) {
            lightSwitch(pContext, true);
        }
    }

    /**
     * 开启闪光灯
     */
    public static void openLight(Context pContext) {
        if (isKitKat()) {
            openLight();
        } else if (isMarshmallow()) {
            lightSwitch(pContext, false);
        }
    }

    /**
     * 关闭闪光灯
     */
    private static void closeLight() {

        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 手电筒控制方法
     *
     * @param lightStatus 手电筒状态，true:开通，false:关闭
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void lightSwitch(Context pContext, final boolean lightStatus) {
        // 初始化相机管理
        if (mCameraManager == null) {
            mCameraManager = (CameraManager) pContext.getSystemService(Context.CAMERA_SERVICE);
        }
        // 关闭手电筒逻辑：如果手电筒开启状态下，关闭
        try {
            if (lightStatus)
                // 关闭手电筒
                Objects.requireNonNull(mCameraManager).setTorchMode("0", false);
            else
                // 打开手电筒
                Objects.requireNonNull(mCameraManager).setTorchMode("0", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断Android系统版本是否 >= M(API23)
     */
    private static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 判断Android系统版本是否 >= K(API19)
     * 并且小于 < M
     */
    private static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

}
