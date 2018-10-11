package com.hxd.jewelry.simple.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Cazaea
 * @time 2018/3/9 9:51
 * @mail wistorm@sina.com
 */
public class BitmapUtil {

    /**
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return
     * @description 计算图片的压缩比率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     * @description 从Resources中加载图片
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置成了true,不占用内存，只获取bitmap宽高
        options.inJustDecodeBounds = true;
        // 读取图片长宽，目的是得到图片的宽高
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        // 载入一个稍大的缩略图
        Bitmap src = BitmapFactory.decodeResource(res, resId, options);
        // 通过得到的bitmap，进一步得到目标大小的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     * @description 从SD卡上加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * 根据url下载图片并压缩
     */
    public static Bitmap downloadImageByUrl(String urlStr, ImageView imageview) {
        InputStream inputStream = null;
        try {
            // 网络图片地址
            URL url = new URL(urlStr);
            //网络连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //管道输入流
            inputStream = new BufferedInputStream(conn.getInputStream());
            /**
             * BufferedInputStream类调用mark(int readLimit)方法后读取多少字节标记才失效
             * 是取readLimit和BufferedInputStream类的缓冲区大小两者中的最大值，而并非完全由readLimit确定。
             * 这个在JAVA文档中是没有提到的。
             */
            inputStream.mark(inputStream.available());

            //图片压缩
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // 获取ImageView想要显示的宽和高
            ImageSizeUtil.ImageSize imageViewSize = ImageSizeUtil.getImageViewSize(imageview);
            options.inSampleSize = calculateInSampleSize(options, imageViewSize.width, imageViewSize.height);
            options.inJustDecodeBounds = false;
            inputStream.reset();
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            conn.disconnect();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据url下载图片并压缩
     */
    public static File decodeSampledBitmapFromUrl(String urlStr, ImageView imageview) {
        InputStream inputStream = null;
        try {
            // 网络图片地址
            URL url = new URL(urlStr);
            //网络连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //管道输入流
            inputStream = new BufferedInputStream(conn.getInputStream());
            /**
             * BufferedInputStream类调用mark(int readLimit)方法后读取多少字节标记才失效
             * 是取readLimit和BufferedInputStream类的缓冲区大小两者中的最大值，而并非完全由readLimit确定。
             * 这个在JAVA文档中是没有提到的。
             */
            inputStream.mark(inputStream.available());

            //图片压缩
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // 获取ImageView想要显示的宽和高
            ImageSizeUtil.ImageSize imageViewSize = ImageSizeUtil.getImageViewSize(imageview);
            options.inSampleSize = calculateInSampleSize(options, imageViewSize.width, imageViewSize.height);
            options.inJustDecodeBounds = false;
            inputStream.reset();
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            conn.disconnect();
            return compressImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public static File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bas中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bas);
        int options = 100;
        // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
        while (bas.toByteArray().length / 1024 > 500) {
            // 重置bas，即清空bas
            bas.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到bas中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, bas);
            long length = bas.toByteArray().length;
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(bas.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                LogcatUtil.e(e.getMessage());
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            LogcatUtil.e(e.getMessage());
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }

    /**
     * Bitmap资源释放
     *
     * @param bitmaps
     */
    private static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

}
