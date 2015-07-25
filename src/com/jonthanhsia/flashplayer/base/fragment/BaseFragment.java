package com.jonthanhsia.flashplayer.base.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class  BaseFragment extends Fragment{
	protected static Activity mActivity;
	/**
	 * Fragment创建的时候调用
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initView();//强制子类去实现创建view的方法
		return view;
	}
	
	public abstract View initView() ;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	/**
	 * 子类初始化数据
	 */
	public void initData() {
	}
}
