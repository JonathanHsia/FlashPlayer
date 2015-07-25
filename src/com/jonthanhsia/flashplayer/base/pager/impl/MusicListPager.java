package com.jonthanhsia.flashplayer.base.pager.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.adapter.MySongListAdapter;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.application.utils.PrefUtils;
import com.jonthanhsia.flashplayer.base.activity.impl.MainUI;
import com.jonthanhsia.flashplayer.base.fragment.BaseFragment;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.base.pager.BasePager;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.jonthanhsia.flashplayer.engine.Mp3InfoProviderBySystem;
/**
 * 音乐列表的fragment
 * @author JonathanHsia
 */
public class MusicListPager extends BasePager {
	private View mView;// 音乐列表的fragment的view
	private List<Mp3Info> sMp3InfosFilter;// 过滤后的音乐列表数据
	private LinearLayout mSongLoading;// 音乐列表加载列表数据的进度条
	private TextView mSongListTitle;// 音乐列表的标题
	private MySongListAdapter sSongListAdapter;
	private ListView mLvSongList;
	private ControlFragment mControlFragment;

	public MusicListPager(Activity act) {
		super(act);
		mControlFragment = ((MainUI) act).getControlFragment();
	}

	@Override
	public View initView() {
		mView = View.inflate(mActivity, R.layout.pager_song_list, null);
		// 歌曲加载的进度条的初始化
		mSongLoading = (LinearLayout) mView
				.findViewById(R.id.songlist_pb_loading);
		// // 歌曲列表标题栏
		mSongListTitle = (TextView) mView.findViewById(R.id.songlist_tv_title);
		// 音乐列表的listView
		mLvSongList = (ListView) mView.findViewById(R.id.lv_song_list);

		return mView;
	}

	
	
	// 初始化子类的数据
	@Override
	public void initData() {
		// 设置单个歌曲item的点击事件(播放音乐)
		mLvSongList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取当前要播放的歌曲洗洗对象
				Mp3Info sCurrentPlayMp3Info = sSongListAdapter
						.getItem(position);

				// 给HomeActivity设置当前播放音乐的值
				mControlFragment.sCurrentPlayMp3Info = sCurrentPlayMp3Info;
				// 调用播放功能播放音乐
				System.out.println("点击了音乐列表中的项目了,开始播放音乐喽.....Fragment中");
				// 点击播放的时候,如果之前有进度就清空
				PrefUtils.putInt(mActivity, MyContance.MUSIC_PROGRESS, 0);
				// 设置歌曲的索引,用于播放歌曲的定位
				mControlFragment.mCurrentMusicposition = sMp3InfosFilter
						.indexOf(sCurrentPlayMp3Info);
				mControlFragment.playMusic(sCurrentPlayMp3Info, 5);

				// 获取item中的图片,然后设置它的值,现在没有思路

				sSongListAdapter.notifyDataSetChanged();
			}
		});

		// 设置单个歌曲item的长按点击事件
		mLvSongList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 显示一个popWindow用户对item进行增删改的操作
				Toast.makeText(mActivity, "长按item的功能,正在开发", 0).show();
				return false;
			}
		});

		// 初始化音乐列表的数据
		initSongListData();
	}

	/**
	 * 音乐列表的消息处理
	 */
	private Handler songListHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyContance.LOAD_SONG_LIST_COMPLETED:// 音乐列表数据加载完成
				sSongListAdapter = new MySongListAdapter(sMp3InfosFilter,
						mActivity);

				mLvSongList.setAdapter(sSongListAdapter);
				mSongLoading.setVisibility(View.GONE);// 隐藏加载音乐进度

				// 加载完数据设置音乐列表的标题
				mSongListTitle.setText("音乐列表(" + sMp3InfosFilter.size() + ")");

				// 加载完数据就给主activity设置音乐列表的值
				mControlFragment.mSongLists = sMp3InfosFilter;

				int playId = PrefUtils.getInt(mActivity,
						MyContance.CURRENT_PLAYSONG_ID, 0);
				MusicListPager.this.setSongPosition(playId);// 初始化listview的初始化的位置

				// 加载完数据之后,判断软件退出后写入sp中的信息,然后在启动时进行播放数据回显数据回显
				mControlFragment.musicDataComeBack();
				break;
			}
		};
	};

	/**
	 * 对HomeActivity提供的获取播放音乐列表的功能
	 */
	public List<Mp3Info> getSongList() {
		return sMp3InfosFilter;
	}

	/**
	 * 初始化音乐列表中的数据:使用异步加载
	 */
	public void initSongListData() {
		mSongLoading.setVisibility(View.VISIBLE);// 加载数据的时候显示进度
		new Thread() {
			public void run() {
				// 拿到歌曲的列表,这是没有过滤的列表集合
				List<Mp3Info> mp3Infos = Mp3InfoProviderBySystem
						.getMp3Infos(mActivity);

				// 过滤我们不要的歌曲
				sMp3InfosFilter = new ArrayList<Mp3Info>();
				for (Mp3Info info : mp3Infos) {
					if (!TextUtils.isEmpty(info.artist)// 歌曲过滤条件,没有艺术家,或者艺术家为unknown的话,或者名称长度太小,小于30秒,我们就不加载到列表中
							&& !"<unknown>".equals(info.artist)
							&& info.duration >= 20000) {// 这里为了做测试,写的很近
						sMp3InfosFilter.add(info);
					}
				}
				// 加载数据完成设置主页面的sMp3InfosFilter的值,为我们获取到的值
				songListHandler
						.sendEmptyMessage(MyContance.LOAD_SONG_LIST_COMPLETED);
			}
		}.start();
	}

	/**
	 * 设置歌曲列表的位置
	 * 
	 * @param position
	 */
	public void setSongPosition(int position) {
		System.out.println("改变ListView的位置了.....");
		if (position >= 0 && position <= 2) {// 如果是前三首歌的话,就不做位置的移动
			System.out.println("前三首歌,不予以定位!...");
			return;
		}
		mLvSongList.setSelection(position - 2);
		sSongListAdapter.notifyDataSetInvalidated();
	}
	
	
	

}
