package com.jonthanhsia.flashplayer.base.activity.impl;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.base.fragment.impl.LeftMenuFragment;

/**
 * 主activity
 * 
 * @author JonathanHsia
 */
public class MainUI extends SlidingFragmentActivity {
	private final String CONTROL_FRAGMENT = "ControlFragment";
	private final String LEFT_MENU = "LeftMenu";
	private boolean mIsExit = false;// 两次按钮返回键的标记
	private FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();

		initFragment();
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragment() {
		// 拿到布局管理器
		fm = this.getSupportFragmentManager();

		// 开启事务
		FragmentTransaction ft = fm.beginTransaction();

		// 替换Fragemnt
		ft.replace(R.id.fl_main, new ControlFragment(), CONTROL_FRAGMENT);
		ft.replace(R.id.fl_left_menu, new LeftMenuFragment(), LEFT_MENU);

		// 提交
		ft.commit();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置主界面
		setContentView(R.layout.activity_main);
		// 设置左侧菜单界面
		setBehindContentView(R.layout.left_menu);
		// 设置右侧菜单
		SlidingMenu slidingMenu = this.getSlidingMenu();
//		slidingMenu.setSecondaryMenu(R.layout.right_menu);
		// 设置滑动模式
//		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 设置滑动模式
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// 设置正文的滑动量(也就是侧边栏显示的大小)
		slidingMenu.setBehindOffset(200);
	}
	/**
	 * 获取控制的fragment
	 * @return
	 */
	public  ControlFragment getControlFragment(){
		return (ControlFragment) fm.findFragmentByTag(CONTROL_FRAGMENT);
	}
	
	/**
	 * 获取播放左菜单的fragment
	 */
	public  LeftMenuFragment getLeftMenuFragment(){
		return (LeftMenuFragment) fm.findFragmentByTag(LEFT_MENU);
	}
	
	
	/**
	 * 按返回键进入主界面,要不然当前 界面一结束,服务也就不起作用了
	 */
	@Override
	public void onBackPressed() {
		if (mIsExit == false) {
			mIsExit = true;
			Toast.makeText(this, "再按一次,后台播放音乐哦,亲爱的", Toast.LENGTH_SHORT).show();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					mIsExit = false;
				}
			}, 2000);
		} else {
			// 当再次的点了返回键,我们返回主页面
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}
}
