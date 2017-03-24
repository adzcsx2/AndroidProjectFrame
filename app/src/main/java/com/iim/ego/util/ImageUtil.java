package com.iim.ego.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Hoyn on 17/3/24.
 */

public class ImageUtil {

    /**
     * 普通的加载图片
     * @param context
     * @param url
     * @param mImageView
     */
    public static void load(Context context,String url,ImageView mImageView){
        Glide.with(context).load(url).into(mImageView);
    }
    public static void load(Activity activity, String url, ImageView mImageView){
        Glide.with(activity).load(url).into(mImageView);
    }
    public static void load(Fragment fragment, String url, ImageView mImageView){
        Glide.with(fragment).load(url).into(mImageView);
    }
    public static void load(android.support.v4.app.Fragment fragment, String url, ImageView mImageView){
        Glide.with(fragment).load(url).into(mImageView);
    }
    public static void load(FragmentActivity activity, String url, ImageView mImageView){
        Glide.with(activity).load(url).into(mImageView);
    }

}
