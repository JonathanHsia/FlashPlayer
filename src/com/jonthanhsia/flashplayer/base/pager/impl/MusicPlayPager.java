package com.jonthanhsia.flashplayer.base.pager.impl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.application.utils.PrefUtils;
import com.jonthanhsia.flashplayer.base.activity.impl.MainUI;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.base.pager.BasePager;
import com.jonthanhsia.flashplayer.base.pager.DownloadResource;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.jonthanhsia.flashplayer.factory.DownloadResourcFactory;
import com.lidroid.xutils.BitmapUtils;

/**
 * 音乐播放的fragment
 * 
 * @author JonathanHsia
 */
public class MusicPlayPager extends BasePager implements OnClickListener, OnLongClickListener {
	private ControlFragment mControlFragment;
	public MusicPlayPager(Activity mActivity) {
		super(mActivity);
		mControlFragment = ((MainUI) mActivity).getControlFragment();
		//初始化图片加载工具
		bitmapUtils = new BitmapUtils(mActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}
	private static ImageView ivSongAlbum;//歌曲的专辑图片
	
	private ImageButton ibPlayMode;
	int[] modeIds = { R.drawable.playmode_normal,
			R.drawable.playmode_repeat_all, R.drawable.playmode_shuffle,
			R.drawable.playmode_repeat_current };
	private int currentMode;
	private BitmapUtils bitmapUtils;

	private ImageButton ibDownload;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_song_play, null);
		// 专辑图片
		ivSongAlbum = (ImageView) view.findViewById(R.id.iv_song_album);
		// 切换播放模式
		ibPlayMode = (ImageButton) view.findViewById(R.id.ib_play_mode);
		//下载专辑图片按钮
		ibDownload = (ImageButton) view.findViewById(R.id.ib_play_dolwnload_album);
		
		return view;
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {
		// 播放模式数据回显
		currentMode = PrefUtils.getInt(mActivity, MyContance.PLAY_MODE, 0);
		ibPlayMode.setBackgroundResource(modeIds[currentMode]);
		ibPlayMode.setOnClickListener(this);
		// 设置专辑图片的动画
		setMusicAlbumAnim(false, 0);// 默认不显示动画
		ivSongAlbum.setOnLongClickListener(this);//设置专辑图片的长按点击事件
		ibDownload.setOnClickListener(this);
	}
	/**
	 * 设置专辑照片
	 */
	public void setAlbumAtrtistPic(){
		//设置专辑图片
		Mp3Info mCurrentPlayMp3Info = mControlFragment.sCurrentPlayMp3Info;
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPlayMp3Info.artistPicPath);
		if(bitmap!=null){//如果专辑图片为空就设置默认的图片
			ivSongAlbum.setImageBitmap(bitmap);
			
			ibDownload.setEnabled(false);
		}else{
			ivSongAlbum.setImageResource(R.drawable.ic_launcher);//设置默认的图片
			ibDownload.setEnabled(true);
		}
	}
	
	/**
	 * 设置专辑图片旋转动画
	 */
	public void setMusicAlbumAnim(boolean show, int duration) {
		if (show) {
			// 设置专辑动画
			RotateAnimation anim = new RotateAnimation(0, 359,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(duration);// 间隔时间
			anim.setRepeatCount(Animation.INFINITE);// 无限循环
			anim.setInterpolator(new LinearInterpolator());// 匀速循环,不停顿
			anim.setFillAfter(true);// 动画结束的时候为止不变
			//每次开启动画的时候,都重新设置一下专辑图片
			setAlbumAtrtistPic();
			//启动动画
			ivSongAlbum.startAnimation(anim);

		}else {
			// 记录退出时的图片角度
			if (Build.VERSION.SDK_INT > 11) {
				// 该方法只能在sdk11版本以上才能用
				int rotation = (int) ivSongAlbum.getRotationX();
				System.out.println("动画停止时的角度!...:" + rotation);
				
				PrefUtils.putInt(mActivity, MyContance.ALBUM_STOP_ROTATION,
						rotation);
//				ivSongAlbum.setRotation(rotation);
			}
			// 设置iv结束时候的位置
			ivSongAlbum.clearAnimation();// 清除动画
			// 结束的时候设置角度
		}
	}

	/**
	 * 点击事件的响应
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_play_mode:
			// 播放模式的点击事件
			// 分析一下共有三种模式:单曲循环,列表播放,列表循环,随机播放
			// 设置切换的模式顺序为:1.默认列表播放2.列表循环3.随机播放,4.单曲循环
			currentMode = PrefUtils.getInt(mActivity, MyContance.PLAY_MODE, 0);
			if (currentMode == modeIds.length - 1) {
				currentMode = -1;
			}
			currentMode += 1;
			ibPlayMode.setBackgroundResource(modeIds[currentMode]);

			String strMode = "";
			switch (currentMode) {
			case 0:
				strMode = "列表播放";
				break;
			case 1:
				strMode = "列表循环";
				break;
			case 2:
				strMode = "随机播放";
				break;
			case 3:
				strMode = "单曲循环";
				break;
			}
			Toast.makeText(mActivity, strMode, 0).show();
			PrefUtils.putInt(mActivity, MyContance.PLAY_MODE, currentMode);
			break;
			
		case R.id.ib_play_dolwnload_album://下载按钮的点击事件
			//显示下载对话框
			showDownloadDialog();
			break;
		}
	}
	/**
	 * 专辑图片的长按点击事件
	 */
	@Override
	public boolean onLongClick(View v) {
		showDownloadDialog();
		return true;
	}

	private void showDownloadDialog() {
		DownloadResource downloadAlbumPic = DownloadResourcFactory.getDownloadResource(mActivity,DownloadResource.Album_PIC_MODE);
		
		downloadAlbumPic.showDownloadDialog();
	}
}
