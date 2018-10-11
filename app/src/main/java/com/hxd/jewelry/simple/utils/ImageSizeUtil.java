package com.hxd.jewelry.simple.utils;

/**
 * 作 者： Cazaea
 * 日 期： 2018/6/20
 * 邮 箱： wistorm@sina.com
 */

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by ZaneLove on 2015/3/15.
 */
public class ImageSizeUtil {
    /**
     * a.根据ImageView获得适应的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    public static ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        //获取ImageView控件的宽和高
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        // 获取ImageView的实际宽度
        int width = imageView.getWidth();
        //首先企图通过getWidth获取显示的宽,如果返回0;那么再去看看它有没有在布局文件中声明宽
        if (width <= 0) {
            width = lp.width;
        }
        //如果布局文件中也没有精确值，那么我们再去看看它有没有设置最大值
        /**
         * 可以看到这里或者最大宽度，我们用的反射，而不是getMaxWidth()；
         * 为啥呢，因为getMaxWidth竟然要API 16，我也是醉了；
         * 为了兼容性，我们采用反射的方案。
         */
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        //如果最大值也没设置，那么我们只有拿出我们的终极方案，使用我们的屏幕宽度
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        // 获取ImageView的实际高度
        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 通过反射获取ImageView控件的某个属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            //Field：属性
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static class ImageSize {
        public int width;
        public int height;
    }
}
