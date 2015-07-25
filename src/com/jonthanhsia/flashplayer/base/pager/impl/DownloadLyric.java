package com.jonthanhsia.flashplayer.base.pager.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.application.utils.FileUtils;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.base.activity.impl.MainUI;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.base.pager.DownloadResource;
import com.jonthanhsia.flashplayer.domain.Lyric;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean.Result;
import com.jonthanhsia.flashplayer.utils.LyricUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 下载歌曲歌词
 * 
 * @author JonathanHsia
 */
public class DownloadLyric extends DownloadResource implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	public DownloadLyric(Activity act, int downMode) {
		super(act, downMode);
	}
	@Override
	protected void processLyricBean(LyricJsonBean lyricBean) {
		System.out.println("歌词下载页弹出了,来处理数据了");
		List<Result> result = lyricBean.result;
		// 初始化歌曲文件路径列表
		lrcFileList = new ArrayList<File>();
		System.out.println("下载歌词啦...");
		int length = result.size() <= 3 ? result.size() : 3;// 只下载三个歌词数据
		lyricCount = length;
		String target = null;// 下载路径
		for (int i = 0; i < length; i++) {
			target = MyContance.LYRIC_TEMP_PATH
					+ (title + "_" + artist + i + ".lrc");
			// if(length==1){//如果只有一首歌词的话,就直接下载到歌词目录
			// target = MyContance.LYRIC_PATH+title+"_"+artist+".lrc";
			// }
			// 有多个歌词的话,让用户选择哪一个
			System.out.println("target:" + target);
			httpUtils.download(result.get(i).lrc, target,
					new RequestCallBack<File>() {
						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							System.out.println("下载成功.....+"
									+ responseInfo.result.getAbsolutePath());
							// Toast.makeText(mActivity, "歌词下载成功", 0).show();
							// 添加歌词到集合中
							lrcFileList.add(responseInfo.result);
							// 展现歌词到ListView中让用户选择
							handler.sendEmptyMessage(1);
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Toast.makeText(mActivity, "歌词下载失败,请重试", 0).show();
						}
					});
		}

	}

	/**
	 * 定义handler下载歌词的显示
	 */
	private Handler handler = new Handler() {
		int num = 0;

		public void handleMessage(android.os.Message msg) {
			num++;
			if (num == lyricCount) {// 当三条信息都执行完成后才显示
				lyricListView.setVisibility(View.VISIBLE);
				System.out.println("设置适配器成功!");
				myAdapter = new MyLyricAdapter();
				lyricListView.setAdapter(myAdapter);
				tvNoLyric.setVisibility(View.INVISIBLE);// 有歌词一定要隐藏没有歌词的提示不管有没有
				llLoadingLyric.setVisibility(View.INVISIBLE);// 隐藏加载进度
				// System.out.println("通知数据更新了");
				// myAdapter.notifyDataSetChanged();
				num = 0;
			}
		};
	};
	private MyLyricAdapter myAdapter;// 歌词的适配器

	/**
	 * 歌词的数据适配器
	 * 
	 * @author JonathanHsia
	 */
	private class MyLyricAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return lrcFileList.size();
		}

		@Override
		public File getItem(int position) {
			if (position == 0) {
				return null;
			}
			return lrcFileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mActivity,
					R.layout.listitem_download_lyric, null);
			TextView tvLrcTitle = (TextView) view
					.findViewById(R.id.tv_down_lyric_title);
			TextView tvLrcSize = (TextView) view
					.findViewById(R.id.tv_down_lyric_size);
			File item = lrcFileList.get(position);
			// System.out.println("getView    item: "+item.getAbsolutePath());
			tvLrcTitle.setText(item.getName());
			tvLrcSize.setText(Formatter.formatFileSize(mActivity, item.length()));
			return view;
		}
	}

	/**
	 * listview的条路点击事件监听器
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {// 点击了头布局
			System.out.println("点击了头布局");
			return;
		}
		// item点击显示一个弹窗,显示歌词的预览
		File item = lrcFileList.get(position - 1);

		System.out.println("file item:" + item.getAbsolutePath());

		showLyric(item, view);
	}

	/**
	 * 显示歌词
	 * 
	 * @param item
	 */
	private void showLyric(File item, View view) {
		View lrcView = View.inflate(mActivity, R.layout.pop_window_show_lyric,
				null);
		TextView tvLyric = (TextView) lrcView
				.findViewById(R.id.tv_pop_window_show_lyric);
		// 读取歌词,设置到控件中
		LyricUtils lyricUtils = new LyricUtils();
		
		//读取歌词
		try {
			lyricUtils.readLyricFile(item);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		ArrayList<Lyric> lyrics = lyricUtils.getLyrics();

//		 歌词集合转换成字符串
		 StringBuilder sb = new StringBuilder();
		 for (int i = 0; i < lyrics.size(); i++) {//转录歌词
		 sb.append(lyrics.get(i).getContent()+"\n");
		 }
		
		tvLyric.setText(sb.toString());
		// 显示歌词对话框
		showLyricDialog(item, lrcView);
	}

	private void showLyricDialog(final File item, View lrcView) {
		// 对话框显示歌词
		Builder abd = new AlertDialog.Builder(mActivity);
		abd.setNegativeButton("使用", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 使用当前的歌词
				useLyric(item);
			}
		});
		abd.setPositiveButton("取消", null);// 取消按钮

		AlertDialog dialog = abd.create();
		dialog.setView(lrcView, 0, 0, 0, 0);
		dialog.setTitle(item.getName());
		// 初始化按钮

		dialog.show();
	}

	/**
	 * 确定使用指定的歌词
	 * 
	 * @param item
	 */
	private void useLyric(final File item) {
		System.out.println("执行了歌词的复制操作了.......");
		// 复制当前歌词到歌词目录,删除缓存文件
		FileUtils.copy(item.getAbsolutePath(), mCurrentMp3Info.lrcUrl, true);// 复制操作
		// 删除缓存操作
		File parentFile = item.getParentFile();
		System.out.println("filte item:" + item.getAbsolutePath());
		List<File> data = new ArrayList<File>();
		FileUtils.getFileListBySuffix(parentFile, ".lrc", data);
		System.out.println("fileData:  " + data);

		FileUtils.deleteFileByFileList(data);// 删除缓存操作

		// 通知更新歌词
		ControlFragment.sIMusicControl.iniLrc();// 初始化歌词控件让它重新的加载歌词
		// 隐藏下载歌词按钮
		((MainUI) mActivity).getControlFragment().getmLyriPager()
				.setSearchLrcButtonVisible(View.INVISIBLE);

		downDialog.dismiss();// 隐藏下载歌词栏
		Toast.makeText(mActivity, "歌词使用成功", 0).show();
	}

	/**
	 * 条目长按点击监听器
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (position == 0) {// 点击了头布局
			System.out.println("长按了头布局");
			return false;
		}
		// item长按点击使用选择的歌词
		File item = lrcFileList.get(position - 1);

		useLyric(item);
		return true;
	}

}
