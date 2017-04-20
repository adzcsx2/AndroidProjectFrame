package com.iim.ego.util.image;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Encoder;

/**
 * Created by Hoyn on 17/3/24.
 */

public class ImageUtil {

    /**
     * 普通的加载图片
     *
     * @param context
     * @param url
     * @param mImageView
     */
    public static void load(Context context, String url, ImageView mImageView) {
        Glide.with(context).load(url).into(mImageView);
    }

    public static void load(Context contedxt, int resId, ImageView mImageView) {
        Glide.with(contedxt).load(resId).into(mImageView);
    }

    public static void load(Activity activity, String url, ImageView mImageView) {
        Glide.with(activity).load(url).into(mImageView);
    }

    public static void load(Fragment fragment, String url, ImageView mImageView) {
        Glide.with(fragment).load(url).into(mImageView);
    }

    public static void load(android.support.v4.app.Fragment fragment, String url, ImageView mImageView) {
        Glide.with(fragment).load(url).into(mImageView);
    }

    public static void load(FragmentActivity activity, String url, ImageView mImageView) {
        Glide.with(activity).load(url).into(mImageView);
    }

    /**
     * glide转圆形
     */
    public static void loadWithCircle(Context context, String url, ImageView mImageView) {
        Glide.with(context).load(url).transform(new GlideCircleTransform(context)).into(mImageView);
    }

    /**
     * glide 转圆形
     */
    public static void loadWithRound(Context context, String url, int size, ImageView mImageView) {
        Glide.with(context).load(url).transform(new GlideRoundTransform(context, size)).into(mImageView);
    }

    /**
     * @return
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     */
    public static String toBase64(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

}
