package com.jonthanhsia.flashplayer.adapter;

import java.util.List;

import com.jonthanhsia.flashplayer.base.pager.BasePager;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment的ViewPager数据适配器
 * 
 * @author JonathanHsia
 * 
 */
public class MyBasePagerAdapter extends PagerAdapter {
	private List<BasePager> pagers;

	public MyBasePagerAdapter(List<BasePager> pagers) {
		this.pagers = pagers;
	}

	@Override
	public int getCount() {
		return pagers.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		BasePager basePager = pagers.get(position);
//		container.removeView(basePager.rootView);
		container.addView(basePager.rootView);
		basePager.initData();// 初始化页面的数据
		return basePager.rootView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
