package com.iim.ego.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.iim.ego.R;


public class MYToast extends Toast{

	private static Toast mToast;
	
	public MYToast(Context context) {
		super(context);
	}

	/**
	 * 自定义Toast样式
	 *
	 * @description
	 * @param context
	 * @param resId 图片ID，如果图片ID为空，则默认为没有图片
	 * @param text
	 * @param duration
	 *            hrq 2014-7-10下午2:15:36
	 */
	public static Toast makeText(Context context, @Nullable int resId, CharSequence text,
								 int duration) {
		Toast result = new Toast(context);

		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.toast, null);

		// 实例化ImageView和TextView对象
		ImageView imageView = (ImageView) layout.findViewById(R.id.ImageView);
		TextView textView = (TextView) layout.findViewById(R.id.message);

		//这里我为了给大家展示就使用这个方面既能显示无图也能显示带图的toast
		if (resId == 0) {
			imageView.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageResource(resId);
		}

		textView.setText(text);

		result.setView(layout);
		result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		result.setDuration(duration);

		return result;
	}

}
