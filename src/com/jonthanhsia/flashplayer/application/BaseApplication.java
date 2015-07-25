package com.jonthanhsia.flashplayer.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
/**
 * 
 * @author JonathanHsia
 *
 */
public class BaseApplication extends Application {
	//获取到主线程的上下文
	private static BaseApplication mContext = null;
	//获取到主线程的handler
	private static Handler mMainThreadHandler = null;
	//获取到主线程
	private static Thread mMainThread = null;
	//获取到主线程的id
	private static int mMainThreadId;
	//获取主线程的looper
	private static Looper mMainThreadLooper = null;
	
	private MyUncaughtHandler handler;
	@Override
	public void onCreate() {
		super.onCreate();
		this.mContext = this;
		this.mMainThreadHandler = new Handler();
		this.mMainThread = Thread.currentThread();
		this.mMainThreadId = (int) Thread.currentThread().getId();
		this.mMainThreadLooper = getMainLooper();
		//捕获全局的异常
				//未捕获异常的的处理类
				handler = new MyUncaughtHandler();
				Thread.setDefaultUncaughtExceptionHandler(handler);

	}
	/**
	 * 返回上下文:
	 * 放在application在任何地方都能访问的到
	 * @return
	 */
	public static BaseApplication getContext(){
		return mContext;
	}
	/**
	 * 返回主线程的handler
	 * @return
	 */
	public static Handler getMainThreadHandler(){
		return mMainThreadHandler;
	}
	/**
	 * 返回主线程
	 * @return
	 */
	public static Thread getMainThread(){
		return mMainThread;
	}
	/**
	 * 返回主线程id
	 * @return
	 */
	public static int getMainThreadId(){
		return mMainThreadId;
	}
	/**
	 * 返回主线程的looper
	 * @return
	 */
	public static Looper getMainThreadLooper(){
		return mMainThreadLooper;
	}
	
	class MyUncaughtHandler implements UncaughtExceptionHandler{
		//一点有异常未捕获,产生崩溃,就会回调到此方法中
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("发现一个为处理的异常,但是被捕获了");
			ex.printStackTrace();
			
			//将错误日志写在本地的文件中,(在应用退出前)
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/flashErr.log");
			try {
				PrintWriter err = new PrintWriter(file);
				ex.printStackTrace(err);
				err.close();//流必须关闭
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//可以将该文件上传到服务器
			//....
			
			//强制应用退出,结束进程:这个俗称闪退,这种比应用崩溃的用户体验要好一些
			android.os.Process.killProcess(android.os.Process.myPid());
			//或者使用system.exit(0);
		}
	}
}
