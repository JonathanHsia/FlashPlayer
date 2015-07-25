package com.jonthanhsia.flashplayer.base.pager;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.application.utils.MyContance;
import com.jonthanhsia.flashplayer.base.fragment.impl.ControlFragment;
import com.jonthanhsia.flashplayer.domain.LyricJsonBean;
import com.jonthanhsia.flashplayer.domain.Mp3Info;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 专门用于下载资源的工具类
 * 
 * @author JonathanHsia
 */
public abstract class DownloadResource implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	public static final int LYRIC_MODE = 0;
	public static final int Album_PIC_MODE = 1;
	protected Activity mActivity;
	protected EditText etAlbumName;// 专辑名
	protected EditText etSongName;// 歌曲名
	protected EditText etArtistName;// 歌手名
	protected Button ibSearch;// 在线搜索的按钮

	protected Mp3Info mCurrentMp3Info;// 当前播放歌曲的信息
	
	protected String album;
	protected String title;
	protected String artist;
	
	protected List<File> lrcFileList;
	protected ListView lyricListView;
	protected TextView tvSearchLyric;// 点击下载歌词的控件

	protected TextView headerView;// listview的提示头布局
	protected int lyricCount;// 歌词的数量
	protected View downView;
	protected LinearLayout llLoadingLyric;
	protected TextView tvNoLyric;
	protected TextView tvTitle;
	protected View downloadView;
	protected ViewPager vpShowAlbumPic;
	private String lrcUrl;
	private LyricJsonBean lyricBean;
	private int downMode = LYRIC_MODE;
	protected AlertDialog downDialog;
	protected HttpUtils httpUtils;

	public DownloadResource(Activity act,int downMode) {
		this.mActivity = act;
		this.downMode = downMode;
		httpUtils = new HttpUtils();
		
	}
	/**
	 * 初始化下载布局
	 * 
	 * @return
	 */
	private View initDownView() {
		View view = View.inflate(mActivity, R.layout.pop_window_search_for_net,
				null);
		etAlbumName = (EditText) view
				.findViewById(R.id.et_pop_search_album_name);
		etSongName = (EditText) view
				.findViewById(R.id.et_pop_search_song_name);
		etArtistName = (EditText) view
				.findViewById(R.id.et_pop_search_artist_name);
		ibSearch = (Button) view
				.findViewById(R.id.ib_pop_search_artist_search);
		
		lyricListView = (ListView) view
				.findViewById(R.id.lv_pop_search_show_lyric_list);
		llLoadingLyric = (LinearLayout) view
				.findViewById(R.id.ll_pop_search_loading_lyric);
		tvNoLyric = (TextView) view
				.findViewById(R.id.tv_pop_search_no_resource);
		// 标题
		tvTitle = (TextView) view
				.findViewById(R.id.tv_pop_window_title);
		//专辑图片的展示滑动页
		vpShowAlbumPic = (ViewPager) view.findViewById(R.id.vp_pop_search_show_album);

		// 初始化头布局
		headerView = new TextView(mActivity);
		headerView.setTextSize(18);
		headerView.setText("点击查看,长按使用");
		headerView.setTextColor(Color.WHITE);
		headerView.setGravity(Gravity.CENTER);
		lyricListView.addHeaderView(headerView);// 添加提示头

		// 初始化控件数据
		initDownPopViewData();
		return view;
	}
	/**
	 * 显示下载对话框
	 */
	public void showDownloadDialog() {
		//获取下载视图
		 downloadView = initDownView();
		
		Builder builder = new AlertDialog.Builder(mActivity);
		builder.setCancelable(false);//不让对话框主动的小时
		
		builder.setOnKeyListener(new MyKeyListener());
		downDialog = builder.create();
		downDialog.setView(downloadView, 0, 0, 0, 0);
		downDialog.show();
	}
	/**
	 * 对话框的按钮监听器
	 * @author JonathanHsia
	 *
	 */
	private class MyKeyListener implements OnKeyListener{
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK){//当点击了返回键,就隐藏对话框
				dialog.dismiss();
				System.out.println("按下了返回键");
				return true;
			}
			return false;
		}
	}

	/**
	 * 初始化下载弹窗的view数据
	 */
	protected  void initDownPopViewData(){
		mCurrentMp3Info = ControlFragment.sCurrentPlayMp3Info;
		// 设置控件的默认值,为当前正在播放的歌曲
		etAlbumName.setText(mCurrentMp3Info.album);
		etSongName.setText(mCurrentMp3Info.title);
		etArtistName.setText(mCurrentMp3Info.artist);
		ibSearch.setOnClickListener(this);
		
		//根据不同的下载模式初始化不同的布局数据
		if(downMode == LYRIC_MODE){
			tvTitle.setText("歌词下载");
			vpShowAlbumPic.setVisibility(View.INVISIBLE);
			lyricListView.setVisibility(View.VISIBLE);
			//设置listview的item点击事件监听器
			lyricListView.setOnItemClickListener(this);
			lyricListView.setOnItemLongClickListener(this);
		}else if(downMode ==Album_PIC_MODE){
			tvTitle.setText("封面下载");
			vpShowAlbumPic.setVisibility(View.VISIBLE);
			lyricListView.setVisibility(View.INVISIBLE);
		}
	}
		
	


	/**
	 * 搜索按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		//显示下载弹窗的默认显示信息
		llLoadingLyric.setVisibility(View.VISIBLE);//现在加载弹窗
		tvNoLyric.setVisibility(View.INVISIBLE);//默认是不显示没有歌词的
		lyricListView.setVisibility(View.INVISIBLE);
		//显示下载对话框的默认信息
		showPopDefInfo();
	}
	
	/**
	 * 显示下载弹窗的默认信息
	 */
	private void showPopDefInfo() {
		//搜索歌词的时候获取最新的输入信息
		album = etAlbumName.getText().toString().trim();
		title = etSongName.getText().toString().trim();
		artist = etArtistName.getText().toString().trim();
		if(TextUtils.isEmpty(title)){
			Toast.makeText(mActivity, "歌曲名不能为空", 0).show();
			return;
		}
		lrcUrl = MyContance.NetLyricBaseURL+title;
		
		if(!TextUtils.isEmpty(artist)){
			//如果歌手名不为空的话,就加上歌手名尽心收缩
			lrcUrl = lrcUrl+File.separator+artist;
		}
		
		System.out.println("歌曲要的歌词地址: "+mCurrentMp3Info.lrcUrl);
		
		//联网获取歌词的json数据
		getLrcJsonDataFromNet(lrcUrl);
	}
	
	/**
	 * 联网获取歌词信息
	 * @param lrcUrl2
	 */
	private void getLrcJsonDataFromNet(String url) {
		
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("歌词数据获取成功json数据如下\n"+responseInfo.result);
				processData(responseInfo.result);
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("搜索歌词失败!");
				Toast.makeText(mActivity, "歌词搜索失败", 0).show();
			}
		});
	}
	
	
	/**
	 * 解析结果数据
	 * @param result
	 */
	private void processData(String result) {
		//获得歌词的json的bean
		lyricBean = parseJson(result,LyricJsonBean.class);
		if(lyricBean.count<=0){
			tvNoLyric.setVisibility(View.VISIBLE);//显示没有搜索到歌词
			llLoadingLyric.setVisibility(View.INVISIBLE);//隐藏加载进度条
			lyricListView.setVisibility(View.INVISIBLE);//当没有歌词的时候如果如果之前有搜索结果的话就清空一下
			Toast.makeText(mActivity, "没有搜索到歌词", 0).show();
		}
		processLyricBean(lyricBean);
	}
	
	/**
	 * 子类处理歌词bean的方式不一样,交给子类去实现
	 */
	protected abstract void processLyricBean(LyricJsonBean lyricBean);
	
	/**
	 * 解析json数据返回对应的bean
	 * 
	 * @param <T>
	 * @param result
	 * @return
	 */
	protected <T> T parseJson(String json, Class<T> clazz) {
		Gson gson = new Gson();
		T t = gson.fromJson(json, clazz);
		return t;
	}
	
}
