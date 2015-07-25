package com.jonthanhsia.flashplayer.base.pager;

import android.app.Activity;
import android.view.View;

/**
 * 播放页面的的ViewPager的基类
 * @author JonathanHsia
 */
public abstract class BasePager {
	public View rootView;
	protected Activity mActivity;

	public BasePager(Activity act) {
		this.mActivity = act;
		this.rootView = initView();
	}

	// 因为子类的布局不固定,所以是抽象的
	public abstract View initView();

	/**
	 * 子类覆盖此方法,实现自己的数据初始化
	 */
	public void initData() {
	}
}
