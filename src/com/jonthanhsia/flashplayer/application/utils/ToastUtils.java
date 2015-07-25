package com.jonthanhsia.flashplayer.application.utils;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {

	/**
	 * 在任意线程中填出吐司
	 * 
	 * @param thread
	 * @param string
	 * @param i
	 */
	public static void showInfo(final Activity act, final String string,
			final int i) {
		// 判断当前线程是主线程还是子线程,是主线程就直接的弹出吐司
		if ("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(act, string, i).show();
		} else {
			// 是子线程,在子线程中不使用发消息的方式更新ui
			act.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(act, string, i).show();
				}
			});
		}
	}

}
