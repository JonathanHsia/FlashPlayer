package com.jonthanhsia.flashplayer.base.activity;
import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
	private static Activity mForegroundActivity = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		initData();
		initViewPager();
	}
	/**
	 * 初始化viewpager
	 */
	protected abstract void initViewPager();
	
	/**
	 * 初始化视图
	 */
	protected abstract void initView();
	
	/**
	 * 初始化数据
	 */
	protected abstract void initData();
	
	@Override
	protected void onResume() {
		super.onResume();
		BaseActivity.mForegroundActivity = this;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		BaseActivity.mForegroundActivity = null;
	}
	
	public static Activity mForegroundActivity(){
		return mForegroundActivity;
	}
}
