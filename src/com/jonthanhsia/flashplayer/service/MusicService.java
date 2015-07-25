package com.jonthanhsia.flashplayer.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.application.utils.PrefUtils;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.base.pager.impl.LyricShowPager;
import com.jonthanhsia.flashplayer.domain.Lyric;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.jonthanhsia.flashplayer.utils.LyricUtils;

/**
 * 音乐播放的服务
 * 
 * @author JonathanHsia
 */
public class MusicService extends Service {
	private MyMusicBinder mBinder = new MyMusicBinder();
	private MediaPlayer player;// 音乐播放对象
	private MyLockScreenReceiver mLockReceiver;
	private IntentFilter lockFilter;
	private boolean updateMusicPb = true;// 更近进度结束的标记
	private Thread updateProgress;
	// 监听来去电
	private TelephonyManager mTM;
	private MyPhoneStateListener mListener;
	private MyOutcallReceiver mOutcallReceiver;
	private boolean isOutCallFlag;// 是播出电话的标记
	// 当前播放器的状态
	private boolean isPrepared = false;// 准备好了吗a,也就是有没有设置过资源
	private boolean isAutoNextSong = false;// 是否是自动下一曲
	private int lastPosition;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		// 1.创建媒体播放器对象
		if (player == null) {
			player = new MediaPlayer();
			player.reset();
		}
		player.setVolume(0.5f, 0.5f);// 设置播放器的音量
		// 设置播放器的播放完成通知
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				System.out.println("收到了播放完成的消息了,还不知道是谁发的");
				if (isAutoNextSong) {// 不是用户手动的下一首歌,说明就是播放完成了,我们就发自动切歌的消息
					ControlFragment.musicProcessHandler
							.sendEmptyMessage(MyContance.MUSIC_PLAY_COMPLETE);
					System.out.println("是自动播放完成的");
					isAutoNextSong = false;
				} else {
					System.out.println("是用户手动切换的");
				}
			}
		});
		player.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// 当播放器发生错误的时候,会回调这个方法
				// 播放器一旦发生错误就重置播放器
				musicReset();
				return false;
			}
		});

		// 异步准备需要准备监听
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				musicStart();
				// startUpdatePbProgress();// 开始播放,就开启进度条
				isPrepared = true;// 准备好了置播放标记为true;
			}
		});
		// 注册屏幕开启和关闭广播接收者
		mLockReceiver = new MyLockScreenReceiver();
		lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		lockFilter.addAction(Intent.ACTION_SCREEN_ON);
		this.registerReceiver(mLockReceiver, lockFilter);

		// 拿到电话管理器, 监听来电
		mTM = (TelephonyManager) this
				.getSystemService(Service.TELEPHONY_SERVICE);
		mListener = new MyPhoneStateListener();
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
		// 定义内部的去电广播接受者,拨电话的时候,暂停播放器
		IntentFilter mOutcallfilter = new IntentFilter();
		mOutcallfilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		mOutcallReceiver = new MyOutcallReceiver();
		this.registerReceiver(mOutcallReceiver, mOutcallfilter);
	}

	/**
	 * 音乐播放控制
	 */
	private void musicPlayControl(Mp3Info song, int mode) throws Exception {
		System.out.println("isPrepared: " + isPrepared);
		isAutoNextSong = false;
		if (mode == 2 || mode == 5) {// 正在播放,或者暂停的时候来了然后来了下一首
			System.out.println("mode2: 上下曲播放,或者是点击了item播放按钮");
			if (lastSong == song) {// 如果上下首歌是相同的话.
				System.out.println("前后歌曲不同");
				if (player.isPlaying()) {
					player.pause();
				} else {
					player.start();
				}
				return;
			}
			// 这个不同的操作

			isAutoNextSong = false;
			musicReset();
			player.setDataSource(song.url);
			player.prepareAsync();// 异步准备
			// isAutoNextSong = true;
		}
		if (mode == 4) {
			System.out.println("自动播放");
			isAutoNextSong = false;
			musicReset();
			player.setDataSource(song.url);
			player.prepareAsync();// 异步准备
			// isPrepared = false;

		}

		if (mode == 3) {// seek播放
			System.out.println("seekbar: 播放");
			int progress = PrefUtils.getInt(getApplicationContext(),
					MyContance.SEEK_PROGRESS, 0);
			System.out.println("seekProgress" + progress);

			startMusicWithProgress(song, progress);
		}
		if (mode == 1) {// 点击播放按钮播放
			System.out.println("mode1: 按钮点击播放");
			if (player.isPlaying()) {
				musicPause();
				System.out.println("暂停操作");
			} else {
				if (isPrepared) {
					musicStart();
					System.out.println("重新启动播放");
				} else {
					System.out.println("没有设置资源呢!");
					int progress = PrefUtils.getInt(getApplicationContext(),
							MyContance.MUSIC_PROGRESS, 0);
					System.out.println("lastProgress: " + progress);
					startMusicWithProgress(song, progress);
				}
			}
		}
		if (mode == 6) {// 单曲循环
			startMusicWithProgress(song, 0);
		}
		lastSong = song;
		isAutoNextSong = true;
	}

	/**
	 * 开始播放音乐,如果存在播放进度的话
	 * 
	 * @param song
	 * @param progress
	 * @throws IOException
	 */
	private void startMusicWithProgress(Mp3Info song, int progress)
			throws IOException {
		if (progress >= 0) {
			isAutoNextSong = false;
			musicReset();
			player.setDataSource(song.url);
			player.prepare();// 异步准备
			player.seekTo(progress);
			musicStart();
			// player.start();
			isPrepared = true;
			// isAutoNextSong = true;
		}
	}

	private void musicPause() {
		player.pause();
		// 一暂停就取消定时器更新进度
		stopUpdatePbProgress();
		PrefUtils.putInt(getApplicationContext(),
				MyContance.PLAYER_SETED_SOURCE, 3);

		stopUpdateLyric();// 暂停就停止更新歌词
		System.out.println("暂停停止更新歌词....");
	}

	/**
	 * 停止更新歌词
	 */
	private void stopUpdateLyric() {
		if (mRunnable != null && handler != null) {
			handler.removeCallbacks(mRunnable);
		}
	}

	/**
	 * 开始更新歌词
	 */
	private void startUpdateLyric() {
		handler.post(mRunnable);
	}

	private void musicStop() {
		player.stop();
		stopUpdatePbProgress();
		stopUpdateLyric();
		System.out.println("停止播放,停止更新歌词////");
	}

	private void musicStart() {
		// 开始播放
		PrefUtils.putInt(getApplicationContext(),
				MyContance.PLAYER_SETED_SOURCE, 1);
		player.start();
		// 开始播放的同时设置进度条
		startUpdatePbProgress();
		startUpdateLyric();
	}

	/**
	 * 重置播放器
	 */
	private void musicReset() {
		isPrepared = false;
		stopUpdatePbProgress();
		stopUpdateLyric();
		player.reset();
	}

	/**
	 * 是否正在播放
	 */
	private boolean musicPlaying() {
		return player.isPlaying();
	}

	/**
	 * 是否在循环播放
	 */
	private boolean musicLooping() {
		return player.isLooping();
	}

	/**
	 * 设置是否循环播放
	 */
	private void setLooping(boolean loop) {
		player.setLooping(loop);
	}

	@Override
	public void onDestroy() {
		// 停止刷新播放进度
		stopUpdatePbProgress();
		stopUpdateLyric();
		// 停止更新歌词
		if (handler != null) {
			handler.removeCallbacks(mRunnable);
			handler = null;
			System.out.println("停止更新歌词");
		}

		// 服务停止,保存播放数据
		int position = player.getCurrentPosition();
		PrefUtils.putInt(getApplicationContext(), MyContance.MUSIC_PROGRESS,
				position);
		System.out.println("播放服务停止,播放进度保存了..position" + position);
		// 销毁播放器,释放player
		player.stop();
		player.release();
		player = null;

		// 停止锁屏的监听
		if (mLockReceiver != null) {
			this.unregisterReceiver(mLockReceiver);
			mLockReceiver = null;
		}

		// 停止监听来电
		if (mTM != null) {
			mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
			mTM = null;
			mListener = null;
		}

		// 停止监听去电
		if (mOutcallReceiver != null) {
			unregisterReceiver(mOutcallReceiver);
			mOutcallReceiver = null;
		}
	}

	// 快进的设置
	private void seekTo(int progress) {
		player.seekTo(progress);
	}

	/**
	 * 添加并启动进度条的播放进度
	 */
	private void startUpdatePb() {
		updateProgress = new Thread(new Runnable() {
			@Override
			public void run() {
				while (updateMusicPb) {
					// 获得歌曲的信息
					int duration = player.getDuration();// 歌曲的总时常(单位毫秒)
					int progress = player.getCurrentPosition();// 当前播放的位置
					// 获取activity中的handler对象没,然后拿到message对象
					Message msg = ControlFragment.musicProcessHandler
							.obtainMessage();
					// 测试进度更新
					// System.out.println("正在更新进度....");
					// 使用bundle对象封装数据
					// 只要实现了序列化接口的对象(Parcelable,Serializable)就可以存放到intent或bundle中
					Bundle data = new Bundle();
					data.putInt("duration", duration);
					data.putInt("progress", progress);
					// 将数据设置到message对象中
					msg.setData(data);
					msg.what = MyContance.MUSIC_PROCESS_CONTROL;
					// 发送数据
					ControlFragment.musicProcessHandler.sendMessage(msg);
					try {
						Thread.sleep(500);// 每个一秒更新一次播放歌曲的进度
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		updateProgress.start();
	}

	/**
	 * 关闭进度条数据的更新
	 */
	private void stopUpdatePbProgress() {
		if (updateMusicPb == true) {
			updateMusicPb = false;
			// updateProgress.interrupt();
			System.out.println("更新进度条进度取消");
		}
	}

	/**
	 * 开启进度条数据的更新
	 */
	private void startUpdatePbProgress() {
		updateMusicPb = true;
		if (player.isPlaying()) {// 屏幕开启,如果正在播放,我们就更加进度条,如果是暂停就不更新了
			startUpdatePb();
		}
	}

	/**
	 * 中间人对象
	 * 
	 * @author JonathanHsia
	 */
	private class MyMusicBinder extends Binder implements IMusicControl {
		// 播放上一首下一首的功能应该交给主页面去控制,音乐服务制负责播放音乐
		/**
		 * 停止播放音乐
		 */
		@Override
		public void stopMusic() {
			musicStop();
		}

		/**
		 * 音乐暂停
		 */
		@Override
		public void pauseMusic() {
			musicPause();
		}

		/**
		 * 播放音乐
		 */
		@Override
		public void playMusic(Mp3Info song, int playState) {
			try {
				initLrc(song);// 播放歌曲的时候才显示歌词
				musicPlayControl(song, playState);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		/**
		 * 音乐是否正在播放
		 */
		@Override
		public boolean isPlaying() {
			return player.isPlaying();
		}

		/**
		 * 重置播放器
		 */
		@Override
		public void resetMusic() {
			musicReset();
		}

		/**
		 * 重新启动播放器
		 */
		@Override
		public void startMusic() {

			musicStart();
		}

		/**
		 * 动态的更新播放进度
		 */
		@Override
		public void seekProgress(int progress) {
			seekTo(progress);
		}

		/**
		 * 音乐进度条的是否更新情况
		 */
		@Override
		public void startUpdatePd() {
			startUpdatePbProgress();
		}

		@Override
		public void stopUpdatePd() {
			stopUpdatePbProgress();// 停止更新进度条
		}

		@Override
		public void startUpdateLrc() {
			startUpdateLyric();
		}

		@Override
		public void stopUpdateLrc() {
			stopUpdateLyric();
		}

		@Override
		public void iniLrc() {
			lastPosition = PrefUtils.getInt(MusicService.this,
					MyContance.MUSIC_PROGRESS, 0);
			// System.out.println("lastPosition============="+lastPosition);

			initLrc(ControlFragment.sCurrentPlayMp3Info);

		}
		// //显示歌词
		// @Override
		// public void showLyric(Mp3Info song) {
		// // 启动歌词显示
		// initLrc(song);
		// }
	}

	/**
	 * 增加功能,锁屏的时候,就不更新进度了
	 */
	private class MyLockScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// 屏幕开启了
				// startUpdatePbProgress();
				System.out.println("屏幕开启了,不做更新进度条操作");
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 屏幕关闭了
				System.out.println("屏幕关闭,不更新进度条了,这里不调用了,全交给焦点君了");
				// stopUpdatePbProgress();
			}
		}

	}

	/**
	 * 电话状态的监听器
	 * 
	 * @author JonathanHsia
	 */
	private class MyPhoneStateListener extends PhoneStateListener {
		private boolean flag = false;// 标记是否是之前有来电,然后空闲的电话

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲
				// 如果播放器不在播放,并且之前是暂停的话,就继续播放
				if (player != null && !player.isPlaying() && flag) {
					player.start();
					System.out.println("来电结束了,播放器播放继续,并更新进度条");

					startUpdatePbProgress();
					startUpdateLyric();
				}

				if (player != null && isOutCallFlag && !player.isPlaying()) {
					player.start();
					System.out.println("去电结束了...启动音乐,并更新进度条");

					startUpdatePbProgress();
					startUpdateLyric();

				}
				// 开启进度pb更新

				flag = false;
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃
				// 正在响铃的时候就判断音乐播放器是否正在播放
				if (player != null && player.isPlaying()) {
					player.pause();
					flag = true;
				}
				System.out.println("监听到来电,音乐播放器关闭,停止进度更新");
				stopUpdatePbProgress();
				stopUpdateLyric();
				System.out.println("监听到来电,音乐播放器关闭,停止歌词更新");

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机
				//
				break;
			}
		}
	}

	/**
	 * 我的去电广播监听
	 * 
	 * @author JonathanHsia
	 */
	private class MyOutcallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果有信的外拨电话,这里会接收到
			if (player != null && player.isPlaying()) {
				player.pause();
				System.out.println("监听到了手机在打电话,音乐播放器暂停等待.停止更新进度");
				stopUpdatePbProgress();
				stopUpdateLyric();
				System.out.println("监听到了手机在打电话,停止更新歌词");

				isOutCallFlag = true;
			} else {
				isOutCallFlag = false;
				System.out.println("监听到来电,但是播放器没有播放音乐,不做操作!");
			}
		}
	}

	// private LrcProcess mLrcProcess; // 歌词处理
	// private LrcParser mLrcParser;//歌词转换
	// private List<LrcContentBean> lrcList = new ArrayList<LrcContentBean>();
	// // 存放歌词列表对象
	// private int index = 0; // 歌词检索值
	private Handler handler = new Handler();
	private List<Lyric> lyrics = null;

	public void initLrc(Mp3Info song) {
		LyricUtils lrcUtils = new LyricUtils();
		File lrcFile = null;
		if (song != null) {// 判空
			lrcFile = new File(song.lrcUrl);
		} else {
			if (mRunnable != null) {
				handler.removeCallbacks(null);
			}
			LyricShowPager.lrcView.setLyrics(lyrics);
			LyricShowPager.lrcView.invalidate();
		}
		// 获取解析歌词
		try {
			lrcUtils.readLyricFile(lrcFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lrcUtils.isExistLyric()) {// 判断歌词文件是否存在
			System.out.println("文件存在===============lrcFile:========"
					+ lrcFile.getAbsolutePath());

			lyrics = lrcUtils.getLyrics();

			LyricShowPager.lrcView.setLyrics(lyrics);

			// 切换带动画显示歌词
			// PlayerActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(PlayerService.this,R.anim.alpha_z));

			handler.post(mRunnable);
		} else {// 歌词文件不存在
			// handler.removeCallbacks(null);// 停止更新歌词
			stopUpdateLyric();

			lyrics = lrcUtils.getLyrics();
			LyricShowPager.lrcView.setLyrics(lyrics);
			LyricShowPager.lrcView.invalidate();
			System.out.println("服务中检测到歌词不存在,不更新歌词");
		}
	}

	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			runTask();
		}
	};

	/**
	 * 更新歌词的任务
	 */
	private void runTask() {
		int position = 0;
		System.out.println("lastPosition");
		if (lastPosition !=-1) {
			position = lastPosition;
			System.out.println("要做数据回显--------------------------------");
			lastPosition = -1;
			LyricShowPager.lrcView.setShowNextLyric(position);
		} else {
			System.out.println("正常的播放....................");
			position = player.getCurrentPosition();
			LyricShowPager.lrcView.setShowNextLyric(position);
			handler.postDelayed(mRunnable, 200); // 每800毫秒更新一次歌词
		}
	}
	private Mp3Info lastSong;
}
