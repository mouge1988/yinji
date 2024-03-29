package com.vjiazhi.shuiyinwang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

public class MyConfig {
	private static int m_fSizePerScale = 100;//
	private static String Tag = "shuiyin";
	private static String TEXT = "wenzi";
	private static String COLOR = "color";
	private static String POSITION = "position";

	public static void setNewColor(Context context, int nColor) {
		saveIntToSharePreference(context, COLOR, nColor);
	}

	public static void setNewPosition(Context context, int nIndex) {
		saveIntToSharePreference(context, POSITION, nIndex);
	}

	public static void setNewText(Context context, String strText) {
		saveStringToSharePreference(context, TEXT, strText);
	}

	public static int getNewColor(Context context) {
		return getIntFromSharePrefenrence(context, COLOR, Color.YELLOW);
	}

	public static int getNewPosition(Context context) {
		return getIntFromSharePrefenrence(context, POSITION, 4);// 中间的位置
	}

	public static String getNewText(Context context) {
		return getStringFromSharePrefenrence(context, TEXT, "By 印记.Print");
	}

	public static void setNewSize(int nPercentScale) {
		if (nPercentScale <= 0 || nPercentScale > 200) {
			return;
		}
		m_fSizePerScale = nPercentScale;
	}

	public static int getNewSize() {
		return m_fSizePerScale;
	}

	private static void saveIntToSharePreference(Context context, String key,
			int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Tag,
				Context.MODE_PRIVATE); // 私有数据
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putInt(key, value);
		editor.commit();// 提交修改
	}

	private static void saveStringToSharePreference(Context context,
			String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Tag,
				Context.MODE_PRIVATE); // 私有数据
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString(key, value);
		editor.commit();// 提交修改
	}

	private static int getIntFromSharePrefenrence(Context context, String key,
			int defaultValue) {
		SharedPreferences share = context.getSharedPreferences(Tag,
				Activity.MODE_WORLD_READABLE);

		int i = share.getInt(key, defaultValue);
		return i;

	}

	private static String getStringFromSharePrefenrence(Context context,
			String key, String defautValue) {
		SharedPreferences share = context.getSharedPreferences(Tag,
				Activity.MODE_WORLD_READABLE);

		String in = share.getString(key, defautValue);
		return in;

	}

}