package com.jonthanhsia.flashplayer.application.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference的工具类
 * 
 * @author JonathanHsia
 */
public class PrefUtils {
	private static SharedPreferences sp;

	/**
	 * 向sp中添加属性值
	 * 
	 * @param ctx
	 *            上下文
	 * @param key
	 *            插入的键
	 * @param value
	 *            插入键的值
	 */
	public static void putBoolean(Context ctx, String key, Boolean value) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key, Boolean defValue) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 写入int类型的值
	 * 
	 * @param ctx
	 * @param key
	 * @param value
	 */
	public static void putInt(Context ctx, String key, int value) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();// 记得一定要提交啊
	}

	public static int getInt(Context ctx, String key, int defValue) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}

	/**
	 * 写入字符串类型的值
	 * 
	 * @param ctx
	 * @param key
	 * @param value
	 */
	public static void putString(Context ctx, String key, String value) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defValue) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(MyContance.SP_NAME,
					Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
}
