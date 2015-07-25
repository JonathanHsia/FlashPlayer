package com.jonthanhsia.flashplayer.base.fragment.impl;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.adapter.MyBasePagerAdapter;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.application.utils.PrefUtils;
import com.jonthanhsia.flashplayer.base.activity.impl.MainUI;
import com.jonthanhsia.flashplayer.base.fragment.BaseFragment;
import com.jonthanhsia.flashplayer.base.pager.BasePager;
import com.jonthanhsia.flashplayer.base.pager.impl.LyricShowPager;
import com.jonthanhsia.flashplayer.base.pager.impl.MusicListPager;
import com.jonthanhsia.flashplayer.base.pager.impl.MusicPlayPager;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.jonthanhsia.flashplayer.service.IMusicControl;
import com.jonthanhsia.flashplayer.service.MusicService;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 主界面控制的fragment
 * @author JonathanHsia
 */
public class ControlFragment extends BaseFragment implements OnClickListener,
		OnPageChangeListener {
	// 使用注解的方式找到控件
	@ViewInject(R.id.vp_music)
	private ViewPager mVpMusic;
	public MusicPlayPager getmPlayPager() {
		return mPlayPager;
	}
	// 音乐控制按钮
	@ViewInject(R.id.iv_play)
	private ImageView mIvPlay;
	@ViewInject(R.id.iv_next)
	private ImageView mIvNext;
	@ViewInject(R.id.iv_previous)
	private ImageView mIvPrev;
	// 音乐的播放显示信息
	@ViewInject(R.id.tv_song_name)
	private TextView mTvSongName;
	@ViewInject(R.id.tv_song_singer)
	private TextView mTvSongSinger;
	@ViewInject(R.id.tv_song_album)
	private TextView mTvSongAlbum;

	// 音乐的播放进度显示
	@ViewInject(R.id.sb_song_progress)
	private static SeekBar mSbProgress;
	@ViewInject(R.id.tv_music_time)
	private static TextView mTvCurrentTime;
	@ViewInject(R.id.tv_music_alltime)
	private static TextView mTvAllTime;

	// 音乐服务
	public static IMusicControl sIMusicControl;
	private MyMusicConn mMusicServiceConn;
	private Intent mMusicServiceIntent;

	// 通知栏更新相关
	private NotifyReceiver notifyReceiver;
	public NotificationManager manager;
	private IntentFilter notifyFilter;
	private static RemoteViews rv;
	private static final int NOTIFICATION_FLAG = 1;

//	public static LrcView sLrcView;// 自定义的歌词显示控件

	// 播放控制
	public List<Mp3Info> mSongLists;// 歌曲列表信息
	public static Mp3Info sCurrentPlayMp3Info;// 当前正在播放的歌曲信息
	public int mCurrentMusicposition;// 当前歌曲的索引
	private static int mLastProgress;// 上次音乐播放的进度

	// 播放模式
	private static int currentPlayMode;

	// ViewPager的三个分页
	private List<BasePager> pagers;// fragmen集合
	private MyBasePagerAdapter mAdapter;// Fragment的vp适配器
	private MusicPlayPager mPlayPager;// 播放的fragment
	private MusicListPager mListPager;// 歌曲列表的fragment
	private LyricShowPager mLyriPager;// 歌词的fragment
	private static FragmentManager fm;// fragment的管理器

	public LyricShowPager getmLyriPager() {
		return mLyriPager;
	}

	// ViewPager页面切换小点
	@ViewInject(R.id.iv_point0)
	private ImageView mIvPoint0;
	@ViewInject(R.id.iv_point1)
	private ImageView mIvPoint1;
	@ViewInject(R.id.iv_point2)
	private ImageView mIvPoint2;

	private boolean mFragmentIsPause = false;//

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.control_fragment, null);
		ViewUtils.inject(this, view);// 注入

		// 初始化图片加载工具
		bitmapUtils = new BitmapUtils(mActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		return view;
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {
		// //初始化音乐进度处理 的handler对象
		// mMusicProcessHandler = new InnerMusicProcessHandler();
		// 初始化播放控制
		initPlayControl();
		// 初始化ViewPager
		initViewPager();
		// 设置播放进度监听器
		setSBListener();
		// 设置播放按钮长按退出应用的功能
		setPlayBtToOutApp();
		// 注册通知的广播接受者
		registerNotifyReceiver();

	}

	/**
	 * 播放的音乐信息的数据回显
	 */
	public void musicDataComeBack() {
		// 当前歌曲的播放id
		int playId = PrefUtils.getInt(mActivity,
				MyContance.CURRENT_PLAYSONG_ID, 0);
		sCurrentPlayMp3Info = mSongLists.get(playId);
		// 回显专辑图片
		mPlayPager.setAlbumAtrtistPic();

		// 回显歌曲播放显示信息
		mTvSongName.setText(sCurrentPlayMp3Info.title);
		mTvSongSinger.setText(sCurrentPlayMp3Info.artist);
		mTvSongAlbum.setText(sCurrentPlayMp3Info.album);
		
		//判断歌词文件是否存在,然后显示下载按钮
		showSearchLrcButton();
		
		// 拿到上次播放遗留下来的进度
		mLastProgress = PrefUtils.getInt(mActivity, MyContance.MUSIC_PROGRESS,
				0);
		sIMusicControl.iniLrc();//开始更新歌词
		
//		LyricShowPager.lrcView.setShowNextLyric(mLastProgress);//歌词进度回显
//		LyricShowPager.lrcView.invalidate();
		
		System.out.println("回显的进度是: " + mLastProgress);
		// 音乐的总长度
		int duration = (int) sCurrentPlayMp3Info.duration;
		mSbProgress.setMax(duration);
		mSbProgress.setProgress(mLastProgress);// 设置进度条的显示

		// 设置数字播放进度的回显
		String durationStr = parseTimeMillionToString(duration);// 将毫秒值转换成分秒模式
		String progressStr = parseTimeMillionToString(mLastProgress);
		mTvAllTime.setText(durationStr);
		mTvCurrentTime.setText(progressStr);
		System.out.println("音乐播放的数据回显了");
	}

	/**
	 * 音乐进度的消息处理
	 */
	public static Handler musicProcessHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyContance.MUSIC_PROCESS_CONTROL:// 音乐播放进度的控制
				// 取得消息更新进度条
				Bundle data = msg.getData();
				int duration = data.getInt("duration");
				int progress = data.getInt("progress");

				mSbProgress.setMax(duration);// 设置进度条的初始参数
				mSbProgress.setProgress(progress);

				String durationStr = parseTimeMillionToString(duration);// 将毫秒值转换成分秒模式
				String progressStr = parseTimeMillionToString(progress);
				// 记录播放位置,这里不做记录,播放器停止的时候记录播放位置,我们只要记录住当前播放的歌曲就行了
				// PrefUtils.putInt(sCtx, MyContance.MUSIC_PROGRESS, progress);

				// 在设置View控件的Text属性的时候,一定要注意传入的值是不是String类型,如果不是的话,传入int或long类型编译也是不会
				// 报错的,但是一运行,就报出没有找到资源的错误;
				mTvAllTime.setText(durationStr);
				mTvCurrentTime.setText(progressStr);
				break;
			case MyContance.MUSIC_PLAY_COMPLETE:// 播放完成的通知
				((MainUI) mActivity).getControlFragment().nextSongOrPrev(true,
						true);
				Intent intent = new Intent();
				intent.setAction("com.flashing.refresh");
				mActivity.sendBroadcast(intent);// 发送刷新广播
				System.out.println("接收到播放完成的消息了,播放下一首");
				break;
			}
		};

	};// 使用handler来更新音乐播放的界面
	private BitmapUtils bitmapUtils;
	private File lrcFile;

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		pagers = new ArrayList<BasePager>();
		// 初始化Fragment对象的数据
		mPlayPager = new MusicPlayPager(mActivity);
		mListPager = new MusicListPager(mActivity);
		mLyriPager = new LyricShowPager(mActivity);

		pagers.add(mListPager);
		pagers.add(mPlayPager);
		pagers.add(mLyriPager);

		// fragment的数据适配器
		mAdapter = new MyBasePagerAdapter(pagers);
		mVpMusic.setOffscreenPageLimit(5);//设置最懂的页面缓存数,防止每次都重新的加载从第三个页面到第一个页面的时候
		mVpMusic.setAdapter(mAdapter);
		
		
//		int currentItem = Integer.MAX_VALUE/2 - (Integer.MAX_VALUE/2%pagers.size())+1;
//		System.out.println("currentItem:  "+currentItem);
		mVpMusic.setCurrentItem(1);// 默认显示页

		// 设置页面改变时的监听器
		mVpMusic.setOnPageChangeListener(this);
	}

	/**
	 * 代码注册广播接收者
	 */
	private void registerNotifyReceiver() {
		notifyReceiver = new NotifyReceiver();
		notifyFilter = new IntentFilter();
		notifyFilter.addAction("com.flashing.prev");
		notifyFilter.addAction("com.flashing.next");
		notifyFilter.addAction("com.flashing.play");
		notifyFilter.addAction("com.flashing.close");
		notifyFilter.addAction("com.flashing.refresh");
		mActivity.registerReceiver(notifyReceiver, notifyFilter);
	}

	/**
	 * 显示通知栏常驻
	 */
	public void showNotification(String albumPicPath) {
		manager = (NotificationManager) mActivity
				.getSystemService(Service.NOTIFICATION_SERVICE);
		// "自定义通知：您有新短信息了，请注意查收！", System.currentTimeMillis());
		Notification myNotify = new Notification();
		// myNotify.icon = R.raw.cd2;
//		if(Build.VERSION.SDK_INT > 11){//解决版本兼容问题
//			myNotify. = BitmapFactory.decodeFile(albumPicPath);//api11以上才能用
//		}else{
//		}
		myNotify.icon = R.drawable.ic_launcher;
		
		myNotify.tickerText = "TickerText:您有新短消息，请注意查收！";
		myNotify.when = System.currentTimeMillis(); // 这是不能清除通知的条件
		myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
		rv = new RemoteViews(mActivity.getPackageName(), R.layout.notify_item);
		// 初始化远程的view
		if (sCurrentPlayMp3Info == null) {// 判空操作
			sCurrentPlayMp3Info = new Mp3Info();
			sCurrentPlayMp3Info.title = "";
			sCurrentPlayMp3Info.artist = "";
		}
		//设置通知栏的显示文本和图片
		rv.setTextViewText(R.id.notify_tv_song_name, sCurrentPlayMp3Info.title);
		rv.setTextViewText(R.id.notify_tv_song_artist,
				sCurrentPlayMp3Info.artist);
		Bitmap bitmap = BitmapFactory.decodeFile(albumPicPath);
		if(bitmap!=null){//如果装机图片为空的话,就显示默认的专辑图片
			rv.setBitmap(R.id.notify_iv_album, "setImageBitmap", bitmap);//设置专辑图片
		}else{
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			rv.setBitmap(R.id.notify_iv_album, "setImageBitmap", bitmap);//设置专辑图片
		}
		// 初始化远程的点击
		// 上一首广播
		Intent prevIntent = new Intent();
		prevIntent.setAction("com.flashing.prev");
		PendingIntent prevPdIntent = PendingIntent.getBroadcast(mActivity, 110,
				prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.notify_iv_prev, prevPdIntent);
		// 下一首广播
		Intent nextIntent = new Intent();
		nextIntent.setAction("com.flashing.next");
		PendingIntent nextPdIntent = PendingIntent.getBroadcast(mActivity, 110,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.notify_iv_next, nextPdIntent);
		// 退出广播
		Intent closeIntent = new Intent();
		closeIntent.setAction("com.flashing.close");
		PendingIntent closePdIntent = PendingIntent.getBroadcast(mActivity,
				110, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.notify_iv_exit, closePdIntent);
		Intent playIntent = new Intent();
		playIntent.setAction("com.flashing.play");
		PendingIntent playPdIntent = PendingIntent.getBroadcast(mActivity, 110,
				playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.notify_iv_play_pause, playPdIntent);
		// 更新播放暂停按钮
		int playState = PrefUtils.getInt(mActivity,
				MyContance.PLAYER_SETED_SOURCE, 2);
		if (playState == 0 || playState == 1) {
			rv.setImageViewResource(R.id.notify_iv_play_pause,// 通知栏中的数据
					R.drawable.img_button_notification_play_pause);
		}
		if (playState == 3) {
			rv.setImageViewResource(R.id.notify_iv_play_pause,
					R.drawable.img_button_notification_play_play);
		}
		myNotify.contentView = rv;
		Intent intent = new Intent(mActivity, MainUI.class);// 点击跳转到播放界面
		PendingIntent contentIntent = PendingIntent.getActivity(mActivity, 110,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setOnClickPendingIntent(R.id.ll_notify_item_root, contentIntent);

		myNotify.contentIntent = contentIntent;
		manager.notify(NOTIFICATION_FLAG, myNotify);
	}

	/**
	 * 接收通知栏发来的更新操作
	 * 
	 * @author JonathanHsia
	 */
	private class NotifyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.flashing.close".equals(action)) {
				System.out.println("结束广播收到");
				manager.cancel(NOTIFICATION_FLAG);
				outApp();
				return;
			}
			if ("com.flashing.prev".equals(action)) {
				System.out.println("上一首的广播收到");
				nextSongOrPrev(false, false);
			}
			if ("com.flashing.next".equals(action)) {
				System.out.println("下一首的广播收到");
				nextSongOrPrev(true, false);
			}
			if ("com.flashing.play".equals(action)) {
				System.out.println("播放暂停的广播收到");
				playMusic(sCurrentPlayMp3Info, 1);
			}
			if ("com.flashing.refresh".equals(action)) {
				System.out.println("刷新界面操作");
			}
			// 更新通知栏
			showNotification(sCurrentPlayMp3Info.artistPicPath);// 图片设置的是当前播放的图片

		}
	}

	/**
	 * 退出应用,所做的关闭操作
	 */
	private void outApp() {
		System.out.println("应用关闭了");
		Toast.makeText(mActivity, "亲爱的,下次再见!唔嘛", Toast.LENGTH_LONG).show();
		sIMusicControl.stopMusic();
		if(mMusicServiceConn!=null){
			mActivity.unbindService(mMusicServiceConn);
			mActivity.stopService(mMusicServiceIntent);
			
		}
		if(notifyReceiver!=null){
			mActivity.unregisterReceiver(notifyReceiver);
		}
		// 取消所有通知
		if (manager != null) {
			manager.cancelAll();
		}
		mActivity.finish();
	}

	/**
	 * 长按退出按钮停止播放
	 */
	private void setPlayBtToOutApp() {
		mIvPlay.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// 创意功能,可以给v设置一个消失的动画
				// 关闭页面,解绑服务,停止服务,退出应用
				outApp();
				return false;
			}

		});
	}

	/**
	 * 设置SeekBar的播放监听器
	 */
	private void setSBListener() {
		mSbProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// 当用户活动滚动条,然后停下的时候,我们就调用服务中的方法,改变歌曲的进度
				// 原理是player对象有seekto到指定位置的功能
				int progress = seekBar.getProgress();
				seekBar.setProgress(progress);
				PrefUtils.putInt(mActivity, MyContance.SEEK_PROGRESS, progress);
				playMusic(sCurrentPlayMp3Info, 3);
				PrefUtils.putInt(mActivity, MyContance.SEEK_PROGRESS, 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
	}

	/**
	 * 初始化控制播放界面
	 */
	private void initPlayControl() {
		// 为按钮绑定点击事件
		mIvPlay.setOnClickListener(this);
		mIvNext.setOnClickListener(this);
		mIvPrev.setOnClickListener(this);

		// 音乐控制的链接中间人
		mMusicServiceConn = new MyMusicConn();
		// 混合启动音乐播放服务
		mMusicServiceIntent = new Intent(mActivity, MusicService.class);
		// 将音乐进度管理者带到音乐服务中(不可用,那边还是接受不到呢!...)
		// mMusicServiceIntent.putExtra("handler", mMusicProcessHandler);

		mActivity.startService(mMusicServiceIntent);// 先将进程变成服务进程
		mActivity.bindService(mMusicServiceIntent, mMusicServiceConn,
				Context.BIND_AUTO_CREATE);

		// 重置播放器的播放参数
		PrefUtils.putInt(mActivity, MyContance.PLAYER_SETED_SOURCE, 0);
	}

	/**
	 * 播放控制按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_play:
			// // 播放音乐按钮的点击事件
			playMusic(sCurrentPlayMp3Info, 1);
			break;
		case R.id.iv_next:
			// 切换到下一曲
			nextSongOrPrev(true, false);
			break;
		case R.id.iv_previous:
			// 切换到上一曲
			nextSongOrPrev(false, false);
			break;
		}
	}
	/**
	 * 音乐服务控制的中间人
	 * 
	 * @author JonathanHsia
	 */
	private class MyMusicConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 拿到中间人
			sIMusicControl = (IMusicControl) service;
		}

		/**
		 * 服务断开的时候,调用的方法
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mActivity.unbindService(mMusicServiceConn);
		}
	}

	/**
	 * 播放音乐
	 * 
	 * @param info
	 *            要播放音乐的信息
	 * @param playState
	 *            播放的参数,是用什么类型的方式播放:1 表示按钮点击播放,2表示上下首的播放,3seekBar,拖动播放的,4.自动播放的
	 * 
	 */
	public void playMusic(Mp3Info info, int mode) {
		// 一播放歌曲的时候,在播放页面就定位到当前歌曲的位置
		mListPager.setSongPosition(mCurrentMusicposition);
		//判断是否显示下载歌词按钮
		showSearchLrcButton();
		
		//设置播放封面
		mPlayPager.setAlbumAtrtistPic();

		System.out.println("当前播放音乐的信息:\n" + sCurrentPlayMp3Info);
		// 设置播放界面
		mTvSongName.setText(info.title);
		mTvSongSinger.setText(info.artist);
		mTvSongAlbum.setText(info.album);
		// 播放音乐
		sIMusicControl.playMusic(info, mode);

		// 设置播放按钮的显示状态
		int playState = PrefUtils.getInt(mActivity,
				MyContance.PLAYER_SETED_SOURCE, 2);
		setPauseAndPlayIcon(playState);

		// 每次播放歌曲的时候都用sp记录这正在播放歌曲的item的id
		mCurrentMusicposition = mSongLists.indexOf(sCurrentPlayMp3Info);// 拿到当前歌曲的索引
		PrefUtils.putInt(mActivity, MyContance.CURRENT_PLAYSONG_ID,
				mCurrentMusicposition);
	}
	/**
	 * 判断是否显示歌词下载按钮
	 */
	private void showSearchLrcButton() {
		lrcFile = new File(sCurrentPlayMp3Info.lrcUrl);
		if(lrcFile.exists() && lrcFile.length()>0){//判断歌词文件是否存在
			System.out.println("存在歌词不显示现在控件");
			//如果有歌词
			mLyriPager.setSearchLrcButtonVisible(View.INVISIBLE);
		}else{
			System.out.println("不存在歌词显示下载控件");
			mLyriPager.setSearchLrcButtonVisible(View.VISIBLE);
		}
	}

	/**
	 * 上一首下一首歌曲功能抽取
	 * 
	 * @param isNextOrPrev
	 *            true:代表下一首歌曲 false:代表上一首歌曲
	 */
	private void nextSongOrPrev(boolean isNextOrPrev, boolean isAutoPlay) {
		String toastInfo = "";// 没有上下首歌曲的提示信息
		currentPlayMode = PrefUtils.getInt(mActivity, MyContance.PLAY_MODE, 0);
		System.out.println("当前的播放模式:   " + currentPlayMode);
		System.out.println("当前的歌曲索引:   " + mCurrentMusicposition);
		if (currentPlayMode == 3) {
			// 单曲循环
			playMusic(sCurrentPlayMp3Info, 6);
			return;
		}
		if (currentPlayMode == 2) {
			// 随机播放
			int size = mSongLists.size();
			int nextInt = new Random().nextInt(size);
			if (mCurrentMusicposition == nextInt) {
				if (nextInt == size - 1) {
					// 随机到最后一个元素的情况
					mCurrentMusicposition = nextInt - 1;
				}
				mCurrentMusicposition = nextInt + 1;// 如果随机数,是当前正在播放的话,就顺延一个
			} else {
				mCurrentMusicposition = nextInt;
			}
			sCurrentPlayMp3Info = mSongLists.get(mCurrentMusicposition);
			playMusic(sCurrentPlayMp3Info, 4);
			return;
		}
		mCurrentMusicposition = mSongLists.indexOf(sCurrentPlayMp3Info);// 拿到当前歌曲的索引
		if (isNextOrPrev) {// 下一首
			mCurrentMusicposition = mCurrentMusicposition + 1;
		} else {// 上一首
			mCurrentMusicposition = mCurrentMusicposition - 1;
		}
		// 角标越界的异常提示信息
		if (currentPlayMode == 1 && mCurrentMusicposition < 0) {
			mCurrentMusicposition = mSongLists.size() - 1;// 是第一首,自动的跳转到下一首
		} else if (mCurrentMusicposition < 0) {
			toastInfo = "亲没有上一首啦,试试下一首";
			Toast.makeText(mActivity, toastInfo, 0).show();
			return;
		}

		if (mCurrentMusicposition == mSongLists.size() && currentPlayMode == 1) {// 是走后一首歌的情况
			// 列表循环
			System.out.println("列表循环...自动跳第一首");
			mCurrentMusicposition = 0;// 跳到第一首歌
		} else if (mCurrentMusicposition >= mSongLists.size()) {
			toastInfo = "亲没有下一首啦,试试上一首";
			Toast.makeText(mActivity, toastInfo, 0).show();
			return;
		}

		sCurrentPlayMp3Info = mSongLists.get(mCurrentMusicposition);

		if (isAutoPlay) {
			playMusic(sCurrentPlayMp3Info, 4);
		} else {
			playMusic(sCurrentPlayMp3Info, 2);
		}
		// 跳转到播放页面
		// sVpMusic.setCurrentItem(0);

		// 设置播放的图标
		setPauseAndPlayIcon(1);
	}

	/**
	 * 播放和暂停按钮图标的切换逻辑
	 * 
	 * @param playState
	 */
	private void setPauseAndPlayIcon(int playState) {
		// 更新播放按钮的显示,如果是正在播放就显示暂停的按钮,如果是暂停的按钮就显示播放的按钮
		System.out.println("playState: " + playState);
		if (playState == 0 || playState == 1) {
			mIvPlay.setBackgroundResource(R.drawable.selector_player_pause_button);
			mPlayPager.setMusicAlbumAnim(true, 12000);
		}
		if (playState == 3) {
			mIvPlay.setBackgroundResource(R.drawable.selector_player_play_button);
			mPlayPager.setMusicAlbumAnim(false, 0);
		}
	}

	/**
	 * 页面切合的时候调用的方法
	 */
	@Override
	public void onPageSelected(int position) {
		//界面的滑动模式,只有在第一个页面可以滑动
		if(position ==0){
			((MainUI)mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}else{
			((MainUI)mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		System.out.println("PageSelected: \nposition: " + position);
		switch (position) {
		// 小点点的切换动画效果的实现
		case 0:
			// 当滑到音乐播放页面
			mIvPoint0.setBackgroundResource(R.drawable.yellow_point);
			mIvPoint1.setBackgroundResource(R.drawable.green_point);
			mIvPoint2.setBackgroundResource(R.drawable.green_point);
			break;
		case 1:
			// 当滑动音乐列表页面
			// 增加功能,跳转到我们正在播放歌曲的位置
			mIvPoint0.setBackgroundResource(R.drawable.green_point);
			mIvPoint1.setBackgroundResource(R.drawable.yellow_point);
			mIvPoint2.setBackgroundResource(R.drawable.green_point);
			break;
		case 2:
			// 歌词播放页面
			mIvPoint0.setBackgroundResource(R.drawable.green_point);
			mIvPoint1.setBackgroundResource(R.drawable.green_point);
			mIvPoint2.setBackgroundResource(R.drawable.yellow_point);
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	/**
	 * 将时间的毫秒值转换成,资费卡格式的时间字符串
	 * 
	 * @param duration
	 */
	private static String parseTimeMillionToString(long timeMillion) {
		// Date date = new Date(timeMillion);
		DateFormat timeFormat = new SimpleDateFormat("mm:ss");// 分秒的格式,想要其他显示格式,查找javaApi_SimpleDateFormat
		String formatStr = timeFormat.format(timeMillion);
		return formatStr;
	}

	// /**
	// * 失去用户焦点就停止更新进度条
	// */
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("PlayActivity失去用户焦点了,不更新歌词和那什么了进度了");
		if (sIMusicControl != null) {
			sIMusicControl.stopUpdatePd();
			sIMusicControl.stopUpdateLrc();
			mFragmentIsPause = true;
		}
		// 失去焦点,就显示通知栏
		// 通知栏常驻用户快捷操作
		showNotification(sCurrentPlayMp3Info.artistPicPath);
	}

	//
	// // /**
	// // * 重新启动activity的时候,调用的方法
	// // */
	@Override
	public void onResume() {
		super.onResume();
		if (mFragmentIsPause) {
			System.out.println("重新获得用户的焦点,更新进度和歌词....");
			sIMusicControl.startUpdatePd();
			sIMusicControl.startUpdateLrc();
			mFragmentIsPause = false;// 还原标记
		}
		if (manager != null) {
			manager.cancelAll();
		}
	}
	
	/**
	 * 页面销毁调用的方法
	 */
	@Override
	public void onDestroy() {
		System.out.println("homeactivity关闭了");
		// 关闭通知广播
//		if (notifyReceiver != null) {
//			mActivity.unregisterReceiver(notifyReceiver);
//		}
//		if(mMusicServiceConn!=null){
//			mActivity.unbindService(mMusicServiceConn);
//			mActivity.stopService(mMusicServiceIntent);
//		}
		if (manager != null) {
			manager.cancelAll();
		}
		mActivity.finish();
		super.onDestroy();
	}
}
