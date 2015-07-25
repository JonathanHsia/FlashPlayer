package com.jonthanhsia.flashplayer.base.fragment.impl;
import android.view.View;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.base.fragment.BaseFragment;
/**
 * 左侧菜单的fragment
 * @author JonathanHsia
 *
 */
public class LeftMenuFragment extends BaseFragment {
	@Override
	public View initView() {

		View view = View.inflate(mActivity,R.layout.left_menu_fragment,null);

		return view;
	}
}