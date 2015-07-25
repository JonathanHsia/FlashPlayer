package com.jonthanhsia.flashplayer.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * MusicViewPager数据适配器
 */
public class MusicViewPagerAdapter extends PagerAdapter {
	List<View> listViews;
	public MusicViewPagerAdapter(List<View> listViews) {
		this.listViews = listViews;
	}

	/**
	 * VP一共有几个页面
	 * 
	 * @return
	 */
	@Override
	public int getCount() {
		return listViews.size();
	}

	/**
	 * 是否是从对象变换成View的
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	/**
	 * 向VP中添加list中的view数据,然后返回list中的数据 说白了,就是在初始化VP中的view
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(listViews.get(position));
		return listViews.get(position);
	}

	/**
	 * VP的当前页面滑出的时候调用的方法.不复写它会出错;
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(listViews.get(position));
	}
}